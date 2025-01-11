package org.lasantha.kotlindemo.semver

/**
 * See https://semver.org/ for more information.
 *
 * Backus–Naur Form Grammar for Valid SemVer Versions:
 *
 * <valid semver> ::= <version core>
 *                  | <version core> "-" <pre-release>
 *                  | <version core> "+" <build>
 *                  | <version core> "-" <pre-release> "+" <build>
 *
 * <version core> ::= <major> "." <minor> "." <patch>
 * <major> ::= <numeric identifier>
 * <minor> ::= <numeric identifier>
 * <patch> ::= <numeric identifier>
 *
 * <pre-release> ::= <dot-separated pre-release identifiers>
 * <dot-separated pre-release identifiers> ::= <pre-release identifier>
 *                                           | <pre-release identifier> "." <dot-separated pre-release identifiers>
 * <pre-release identifier> ::= <alphanumeric identifier> | <numeric identifier>
 *
 * <build> ::= <dot-separated build identifiers>
 * <dot-separated build identifiers> ::= <build identifier>
 *                                     | <build identifier> "." <dot-separated build identifiers>
 * <build identifier> ::= <alphanumeric identifier> | <digits>
 *
 * <alphanumeric identifier> ::= <non-digit>
 *                             | <non-digit> <identifier characters>
 *                             | <identifier characters> <non-digit>
 *                             | <identifier characters> <non-digit> <identifier characters>
 *
 * <identifier characters> ::= <identifier character> | <identifier character> <identifier characters>
 * <identifier character> ::= <digit> | <non-digit>
 *
 * <numeric identifier> ::= "0" | <positive digit> | <positive digit> <digits>
 *
 * <non-digit> ::= <letter> | "-"
 * <digits> ::= <digit> | <digit> <digits>
 * <digit> ::= "0" | <positive digit>
 * <positive digit> ::= "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
 * <letter> ::= "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J"
 *            | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T"
 *            | "U" | "V" | "W" | "X" | "Y" | "Z" | "a" | "b" | "c" | "d"
 *            | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n"
 *            | "o" | "p" | "q" | "r" | "s" | "t" | "u" | "v" | "w" | "x"
 *            | "y" | "z"
 */
internal object VersionUtil {
    private val digits = '0'..'9'
    private val alphaUpper = 'A'..'Z'
    private val alphaLower = 'a'..'z'

    fun parsePreReleaseIdentifiers(preReleaseString: String): List<Identifier<Any>> {
        return parseIdentifiers(preReleaseString, VersionUtil::preReleaseMatcher).map {
            when (val i = mapToInt(it)) {
                null -> AlphaNumericIdentifier(it)
                else -> NumericIdentifier(i)
            }
        }
    }

    private fun mapToInt(s: String): Int? {
        for (c in s) {
            if (!isNumeric(c)) return null
        }
        return s.toInt()
    }

    fun parseBuildMetadataIdentifiers(buildMetadataString: String): List<AlphaNumericIdentifier> {
        return parseIdentifiers(buildMetadataString, VersionUtil::buildMetadataMatcher)
            .map { AlphaNumericIdentifier(it) }
    }

    private fun parseIdentifiers(s: String, matcher: (StringBuffer) -> Int): List<String> {
        val list = mutableListOf<String>()
        val buf = StringBuffer(s)
        val start = buf.position()

        if (matcher(buf) == start) return emptyList()
        else list.add(buf.substring(start))

        while (buf.hasNext() && isPeriod(buf.peek())) {
            buf.next()
            val partialStart = buf.position()
            if (matcher(buf) == partialStart) return emptyList()
            else list.add(buf.substring(partialStart))
        }

        if (buf.hasNext()) return emptyList()

        return list
    }

    fun parse(versionString: String): Version? {
        if (versionString.isEmpty() || versionString.isBlank()) return null
        val buf = StringBuffer(versionString)

        val major = matchNumber(buf) ?: return null
        if (!matchChar(buf, '.')) return null
        val minor = matchNumber(buf) ?: return null
        if (!matchChar(buf, '.')) return null
        val patch = matchNumber(buf) ?: return null

        val preRelease = when (matchChar(buf, '-')) {
            true -> matchIdentifiers(buf, VersionUtil::preReleaseMatcher) ?: return null
            else -> null
        }

        val buildMetadata = when (matchChar(buf, '+')) {
            true -> matchIdentifiers(buf, VersionUtil::buildMetadataMatcher) ?: return null
            else -> null
        }

        if (buf.hasNext()) return null
        return Version(major, minor, patch, preRelease, buildMetadata)
    }

    private fun matchNumber(buf: StringBuffer): Int? {
        val start = buf.position()
        if (buf.hasNext() && isZero(buf.peek())) {
            buf.next()
            return 0
        }
        while (buf.hasNext() && isNumeric(buf.peek())) {
            buf.next()
        }
        return when (buf.position() > start) {
            true -> buf.substring(start).toInt()
            else -> null
        }
    }

    private fun matchChar(buffer: StringBuffer, c: Char): Boolean {
        if (buffer.hasNext() && buffer.peek() == c) {
            buffer.next()
            return true
        }
        return false
    }

    private fun matchIdentifiers(buf: StringBuffer, matcher: (StringBuffer) -> Int): String? {
        val start = buf.position()
        if (matcher(buf) == start) return null

        while (buf.hasNext() && isPeriod(buf.peek())) {
            buf.next()
            val partialStart = buf.position()
            if (matcher(buf) == partialStart) return null
        }

        return when (buf.position() > start) {
            true -> buf.substring(start)
            else -> null
        }
    }

    private fun buildMetadataMatcher(buf: StringBuffer): Int {
        while (buf.hasNext() && isAlphaNumeric(buf.peek())) {
            buf.next()
        }
        return buf.position()
    }

    private fun preReleaseMatcher(buf: StringBuffer): Int {
        val start = buf.position()
        var leadingZeroNumber = false
        if (buf.hasNext() && isZero(buf.peek())) {
            buf.next()
            leadingZeroNumber = buf.hasNext() && isNumeric(buf.peek())
        }

        while (buf.hasNext() && isAlphaNumeric(buf.peek())) {
            if (leadingZeroNumber && !isNumeric(buf.peek())) {
                leadingZeroNumber = false
            }
            buf.next()
        }

        if (leadingZeroNumber) return start
        return buf.position()
    }

    private fun isPeriod(c: Char) = c == '.'

    private fun isZero(c: Char) = c == '0'

    private fun isNumeric(c: Char) = digits.contains(c)

    private fun isAlphaNumeric(c: Char) =
        alphaUpper.contains(c) || alphaLower.contains(c) || digits.contains(c) || c == '-'
}

private class StringBuffer(val s: String) {
    private val length = s.length
    private var i = 0

    fun next(): Char {
        return s[i++]
    }

    fun hasNext(): Boolean {
        return i < length
    }

    fun peek(): Char {
        return s[i]
    }

    fun position() = i

    fun substring(start: Int, end: Int = i) = s.substring(start, end)
}
