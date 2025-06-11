package org.lasantha.kotlindemo.semver

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.lasantha.kotlindemo.semver.Version.Companion.parse

class VersionTest {


    @Test
    fun testOutput() {
        //val version = Version(1, 12, 35)
        val version = Version.Companion.parse("1.0.0-alpha.1.3+021.AF2.6D3----117B344092BD") ?: throw IllegalArgumentException()
        println(version.major)
        println(version.minor)
        println(version.patch)
        println(version.preReleaseIdentifiers)
        println(version.buildMetadataIdentifiers)
    }

    @Test
    fun testCreateValidation_Valid() {
        Version(7, 12, 345)
        Version(7, 12, 345, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1")
        Version(7, 12, 345, preRelease = "20250109.prod-p1.123")
        Version(7, 12, 345, preRelease = "-")
        Version(7, 12, 345, preRelease = "-A3")
        Version(7, 12, 345, preRelease = "A3-")
        Version(7, 12, 345, preRelease = "a.b.---")
        Version(7, 12, 345, preRelease = "abc.120.0a1b2c3")
        Version(7, 12, 345, buildMetadata = "20250109.prod-p1.123")
        Version(7, 12, 345, buildMetadata = "-")
        Version(7, 12, 345, buildMetadata = "-A3")
        Version(7, 12, 345, buildMetadata = "A3-")
        Version(7, 12, 345, buildMetadata = "a.b.---")
        Version(7, 12, 345, buildMetadata = "abc.120.007.0a1b2c3")
    }

    @Test
    fun testCreateValidation_Invalid() {
        val errorClass = IllegalArgumentException::class.java
        assertThrowsExactly(errorClass) { Version(-1, 2, 3) }
        assertThrowsExactly(errorClass) { Version(1, -2, 3) }
        assertThrowsExactly(errorClass) { Version(1, 2, -3) }

        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "çπå") } // non-ascii
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "Abc_123") } // _ (underscore)
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "Abc 123") } // space
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "Abc..123") } // ..
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "abc.123.0732.a1b2c3") } // 0732 (leading zero)

        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "çπå") } // non-ascii
        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "Abc_123") } // _ (underscore)
        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "Abc 123") } // space
        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "Abc..123") } // ..
        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "A+B") } // +
        assertThrowsExactly(errorClass) { Version(1, 0, 0, buildMetadata = "+Abc-123") } // ++ (first as delimiter)

        // both pre-release and build metadata are invalid
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "Ab.023", buildMetadata = "aa bb") }
    }

    @Test
    fun testCreateIdentifiers() {
        val version1 = Version(1, 0, 0, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1")
        assertEquals(
            listOf(AlphaNumericIdentifier("beta-1"), NumericIdentifier(123)), version1.preReleaseIdentifiers
        )
        assertEquals(
            listOf(AlphaNumericIdentifier("eg-ID"), AlphaNumericIdentifier("511"), AlphaNumericIdentifier("4f85-1")),
            version1.buildMetadataIdentifiers
        )

        val version2 = Version(1, 0, 0)
        assertEquals(0, version2.preReleaseIdentifiers.size)
        assertEquals(0, version2.buildMetadataIdentifiers.size)
    }

    @Test
    fun testToString() {
        assertEquals("1.2.3", Version(1, 2, 3).toString())
        assertEquals("0.0.0-alpha.beta", Version(0, 0, 0, preRelease = "alpha.beta").toString())
        assertEquals("1.0.0+20250109-prod", Version(1, 0, 0, buildMetadata = "20250109-prod").toString())
        assertEquals(
            "7.12.345-beta-1.123+eg-ID.511.4f85-1",
            Version(7, 12, 345, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1").toString()
        )
    }

    @Test
    fun testParse_Valid() {
        assertEquals(Version(0, 0, 0), parse("0.0.0"))
        assertEquals(Version(1, 0, 0), parse("1.0.0"))
        assertEquals(Version(0, 0, 1), parse("0.0.1"))
        assertEquals(Version(0, 1, 0), parse("0.1.0"))
        assertEquals(Version(789, 1264, 65430), parse("789.1264.65430"))

        assertEquals(Version(1, 0, 0, preRelease = "20250109.prod-p1.123"), parse("1.0.0-20250109.prod-p1.123"))
        assertEquals(Version(1, 0, 0, preRelease = "-"), parse("1.0.0--"))
        assertEquals(Version(1, 0, 0, preRelease = "-A3"), parse("1.0.0--A3"))
        assertEquals(Version(1, 0, 0, preRelease = "A3-"), parse("1.0.0-A3-"))
        assertEquals(Version(1, 0, 0, preRelease = "a.b.---"), parse("1.0.0-a.b.---"))
        assertEquals(Version(1, 0, 0, preRelease = "abc.120.a1b2c3"), parse("1.0.0-abc.120.a1b2c3"))

        assertEquals(Version(1, 0, 0, buildMetadata = "20250109.prod-p1.123"), parse("1.0.0+20250109.prod-p1.123"))
        assertEquals(Version(1, 0, 0, buildMetadata = "-"), parse("1.0.0+-"))
        assertEquals(Version(1, 0, 0, buildMetadata = "-A3"), parse("1.0.0+-A3"))
        assertEquals(Version(1, 0, 0, buildMetadata = "A3-"), parse("1.0.0+A3-"))
        assertEquals(Version(1, 0, 0, buildMetadata = "a.b.---"), parse("1.0.0+a.b.---"))
        assertEquals(Version(1, 0, 0, buildMetadata = "abc.120.007.a1b2c3"), parse("1.0.0+abc.120.007.a1b2c3"))

        assertEquals(
            Version(7, 12, 345, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1"),
            parse("7.12.345-beta-1.123+eg-ID.511.4f85-1")
        )
    }

    @Test
    fun testParse_Invalid() {
        assertNull(parse("")) // empty
        assertNull(parse(" ")) // blank
        assertNull(parse("    ")) // blank
        assertNull(parse("foo")) // non-numeric
        assertNull(parse("Foo Bar")) // non-numeric
        assertNull(parse("1")) // single digit only
        assertNull(parse("1.")) // single digit and .
        assertNull(parse("1.2")) // two digits only
        assertNull(parse("1.2.")) // two digits and .
        assertNull(parse("1.2.3.")) // three digits and .
        assertNull(parse("1.2.-1")) // negative
        assertNull(parse("1.2.00")) // leading zero
        assertNull(parse("1.2.01")) // leading zero
        assertNull(parse("1.2.0xFF")) // hex
        assertNull(parse("1.2.3.4")) // four digits

        assertNull(parse("1.2.3-çπå")) // non-ascii
        assertNull(parse("1.2.3-Abc_123")) // _ (underscore)
        assertNull(parse("1.2.3-Abc 123")) // space
        assertNull(parse("1.2.3-Abc..123")) // ..
        assertNull(parse("1.0.0-abc.123.0732.a1b2c3")) // 0732 (leading zero)

        assertNull(parse("1.2.3+åçπ")) // non-ascii
        assertNull(parse("1.2.3+Abc_123")) // _ (underscore)
        assertNull(parse("1.2.3+Abc 123")) // space
        assertNull(parse("1.2.3+Abc..123")) // ..
        assertNull(parse("1.2.3+A+B")) // +
        assertNull(parse("1.2.3++Abc-123")) // ++

        assertNull(parse("1.2.3-Ab.023+aa bb")) // both pre-release and build metadata invalid
    }

    @Test
    fun testComparison() {
        // 1. Precedence MUST be calculated by separating the version into major, minor, patch
        // and pre-release identifiers in that order.
        // 2. Precedence is determined by the first difference when comparing each of these identifiers
        // from left to right as follows: Major, minor, and patch versions are always compared numerically.
        //   Example: 2.1.1 > 2.1.0 > 2.0.0 > 1.0.0
        assertGreaterThanChain(Version(2, 1, 1), Version(2, 1, 0), Version(2, 0, 0), Version(1, 0, 0))
        assertGreaterThan(Version(1, 0, 0), Version(0, 9, 9))
        assertGreaterThan(Version(1, 1, 0), Version(1, 0, 9))
        assertGreaterThan(Version(1, 1, 1), Version(1, 1, 1, preRelease = "ab.123"))
        assertEqualsByComparison(Version(1, 2, 3), Version(1, 2, 3))

        // 3. When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version.
        assertGreaterThan(Version(1, 0, 0), Version(1, 0, 0, preRelease = "ab.123"))

        // 4. Precedence for two pre-release versions with the same major, minor, and patch version MUST be determined
        // by comparing each dot separated identifier from left to right until a difference is found as follows:
        //   1. Identifiers consisting of only digits are compared numerically.
        //   2. Identifiers with letters or hyphens are compared lexically in ASCII sort order.
        //   3. Numeric identifiers always have lower precedence than non-numeric identifiers.
        //   4. A larger set of pre-release fields has a higher precedence than a smaller set,
        //      if all the preceding identifiers are equal.
        //      E.g.: 1.0.0 > 1.0.0-rc.1 > 1.0.0-beta.11 > 1.0.0-beta.2 > 1.0.0-beta > 1.0.0-alpha.beta >
        //            1.0.0-alpha.1 > 1.0.0-alpha > 1.0.0-123.xyz > 1.0.0-123 > 1.0.0-122
        assertGreaterThanChain(
            Version(1, 0, 0),
            Version(1, 0, 0, preRelease = "rc.1"),
            Version(1, 0, 0, preRelease = "beta.11"),
            Version(1, 0, 0, preRelease = "beta.2"),
            Version(1, 0, 0, preRelease = "beta"),
            Version(1, 0, 0, preRelease = "alpha.beta"),
            Version(1, 0, 0, preRelease = "alpha.1"),
            Version(1, 0, 0, preRelease = "alpha"),
            Version(1, 0, 0, preRelease = "123.xyz"),
            Version(1, 0, 0, preRelease = "123"),
            Version(1, 0, 0, preRelease = "122"),
        )
        assertGreaterThanChain(
            Version(1, 0, 1),
            Version(1, 0, 1, preRelease = "1.1"),
            Version(1, 0, 0),
            Version(1, 0, 0, preRelease = "1.1"),
            Version(1, 0, 0, preRelease = "1"),
            Version(1, 0, 0, preRelease = "0.alphax"),
            Version(1, 0, 0, preRelease = "0.alpha1"),
            Version(1, 0, 0, preRelease = "0.alpha-x"),
            Version(1, 0, 0, preRelease = "0.alpha.beta.11"),
            Version(1, 0, 0, preRelease = "0.alpha.beta.2"),
            Version(1, 0, 0, preRelease = "0.alpha.beta"),
            Version(1, 0, 0, preRelease = "0.alpha"),
            Version(1, 0, 0, preRelease = "0"),
        )
        assertEqualsByComparison(
            Version(1, 0, 0, preRelease = "0.alpha.beta.11"),
            Version(1, 0, 0, preRelease = "0.alpha.beta.11")
        )

        // Build metadata has no effect on precedence
        assertEqualsByComparison(
            Version(1, 0, 0),
            Version(1, 0, 0, buildMetadata = "abc.120.007.a1b--2c3")
        )
        assertGreaterThanChain(
            Version(1, 0, 1, buildMetadata = "abc"),
            Version(1, 0, 0, buildMetadata = "klm"),
            Version(1, 0, 0, preRelease = "alpha.beta.1", buildMetadata = "qrs"),
            Version(1, 0, 0, preRelease = "alpha.beta", buildMetadata = "xyz"),
        )
    }

    private fun assertGreaterThanChain(vararg versions: Version) {
        for (a in 0..<versions.size - 1) {
            for (b in a + 1..<versions.size) {
                assertGreaterThan(versions[a], versions[b])
            }
        }
    }

    private fun assertGreaterThan(v1: Version, v2: Version) {
        assertTrue(v1 > v2, "Expecting: $v1 > $v2")
        assertTrue(v2 < v1, "Expecting: $v2 < $v1")

        assertFalse(v1 < v2, "Not expecting: $v1 < $v2")
        assertFalse(v2 > v1, "Not expecting: $v2 > $v1")

        assertFalse(v1 == v2, "Not expecting: $v1 == $v2")
    }

    /**
     * Assert that the two versions are neither greater than nor less than each other.
     * This is the same as asserting that the two versions are equal using 'compare-to' method.
     * This is not the same as asserting that the two versions are equal using the 'equals' method.
     * Eg:
     *      1.0.0 == 1.0.0 is true for both 'compare-to' and 'equals'
     *      1.0.0 == 1.0.0-rc.1 is false for both 'compare-to' and 'equals'
     *      1.0.0 != 1.0.0+build.1 is true for 'compare-to' but false for 'equals'
     */
    private fun assertEqualsByComparison(v1: Version, v2: Version) {
        assertFalse(v1 > v2, "Not expecting: $v1 > $v2")
        assertFalse(v1 < v2, "Not expecting: $v1 < $v2")
        assertFalse(v2 < v1, "Not expecting: $v2 < $v1")
        assertFalse(v2 > v1, "Not expecting: $v2 > $v1")
    }

    @Test
    fun testSort() {
        val expectedVersions = listOf(
            "1.0.0-0",
            "1.0.0-0.alpha",
            "1.0.0-0.alpha.beta+build.2",
            "1.0.0-0.alpha.beta.2",
            "1.0.0-0.alpha.beta.11",
            "1.0.0-0.alpha--",
            "1.0.0-0.alpha-x",
            "1.0.0-0.alpha1",
            "1.0.0-0.alphax",
            "1.0.0-1+build.1",
            "1.0.0-1.1",
            "1.0.0",
            "1.0.0",
            "1.0.1-1.0",
            "1.0.1-1.1",
            "1.0.1-1.123",
            "1.0.1-1.0a",
            "1.0.1-1.1a",
            "1.0.1-1.x",
            "1.0.1+build.123",
            "1.9.9",
            "2.0.0",
            "2.1.0",
            "2.1.1",
        ).map { parse(it) ?: throw IllegalArgumentException() }

        val unsortedVersion = expectedVersions.shuffled()

        assertEquals(expectedVersions, unsortedVersion.sorted())
    }

    @Test
    fun testNextMajor() {
        assertEquals(Version(1, 0, 0), Version(0, 0, 0).nextMajor())
        assertEquals(Version(1, 0, 0), Version(0, 9, 0).nextMajor())
        assertEquals(Version(1, 0, 0), Version(0, 9, 9).nextMajor())

        assertEquals(Version(2, 0, 0), Version(1, 0, 0, preRelease = "foo").nextMajor())
        assertEquals(Version(2, 0, 0), Version(1, 0, 0, buildMetadata = "123").nextMajor())
        assertEquals(Version(2, 0, 0), Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextMajor())

        assertEquals(
            Version(2, 0, 0, preRelease = "bar"),
            Version(1, 0, 0, preRelease = "foo").nextMajor(preRelease = "bar")
        )
        assertEquals(
            Version(2, 0, 0, buildMetadata = "456"),
            Version(1, 0, 0, buildMetadata = "123").nextMajor(buildMetadata = "456")
        )
        assertEquals(
            Version(2, 0, 0, buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo").nextMajor(buildMetadata = "456")
        )
        assertEquals(
            Version(2, 0, 0, preRelease = "bar"),
            Version(1, 0, 0, buildMetadata = "123").nextMajor(preRelease = "bar")
        )
        assertEquals(
            Version(2, 0, 0, preRelease = "bar", buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextMajor(
                preRelease = "bar", buildMetadata = "456"
            )
        )
    }

    @Test
    fun testNextMinor() {
        assertEquals(Version(0, 1, 0), Version(0, 0, 0).nextMinor())
        assertEquals(Version(0, 1, 0), Version(0, 0, 9).nextMinor())
        assertEquals(Version(0, 10, 0), Version(0, 9, 9).nextMinor())

        assertEquals(Version(1, 1, 0), Version(1, 0, 0, preRelease = "foo").nextMinor())
        assertEquals(Version(1, 1, 0), Version(1, 0, 0, buildMetadata = "123").nextMinor())
        assertEquals(Version(1, 1, 0), Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextMinor())

        assertEquals(
            Version(1, 1, 0, preRelease = "bar"),
            Version(1, 0, 0, preRelease = "foo").nextMinor(preRelease = "bar")
        )
        assertEquals(
            Version(1, 1, 0, buildMetadata = "456"),
            Version(1, 0, 0, buildMetadata = "123").nextMinor(buildMetadata = "456")
        )
        assertEquals(
            Version(1, 1, 0, buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo").nextMinor(buildMetadata = "456")
        )
        assertEquals(
            Version(1, 1, 0, preRelease = "bar"),
            Version(1, 0, 0, buildMetadata = "123").nextMinor(preRelease = "bar")
        )
        assertEquals(
            Version(1, 1, 0, preRelease = "bar", buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextMinor(
                preRelease = "bar", buildMetadata = "456"
            )
        )
    }

    @Test
    fun testNextPatch() {
        assertEquals(Version(0, 0, 1), Version(0, 0, 0).nextPatch())
        assertEquals(Version(0, 0, 10), Version(0, 0, 9).nextPatch())
        assertEquals(Version(0, 9, 10), Version(0, 9, 9).nextPatch())

        assertEquals(Version(1, 0, 1), Version(1, 0, 0, preRelease = "foo").nextPatch())
        assertEquals(Version(1, 0, 1), Version(1, 0, 0, buildMetadata = "123").nextPatch())
        assertEquals(Version(1, 0, 1), Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextPatch())

        assertEquals(
            Version(1, 0, 1, preRelease = "bar"),
            Version(1, 0, 0, preRelease = "foo").nextPatch(preRelease = "bar")
        )
        assertEquals(
            Version(1, 0, 1, buildMetadata = "456"),
            Version(1, 0, 0, buildMetadata = "123").nextPatch(buildMetadata = "456")
        )
        assertEquals(
            Version(1, 0, 1, buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo").nextPatch(buildMetadata = "456")
        )
        assertEquals(
            Version(1, 0, 1, preRelease = "bar"),
            Version(1, 0, 0, buildMetadata = "123").nextPatch(preRelease = "bar")
        )
        assertEquals(
            Version(1, 0, 1, preRelease = "bar", buildMetadata = "456"),
            Version(1, 0, 0, preRelease = "foo", buildMetadata = "123").nextPatch(
                preRelease = "bar", buildMetadata = "456"
            )
        )
    }
}
