package org.lasantha.kotlindemo.semver

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.lasantha.kotlindemo.semver.Version.Companion.parse

class VersionTest {

    @Test
    fun testCreateValidation() {
        // Valid versions
        Version(7, 12, 345, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1")

        Version(1, 0, 0, preRelease = "20250109.prod-p1.123")
        Version(1, 0, 0, preRelease = "-")
        Version(1, 0, 0, preRelease = "-A3")
        Version(1, 0, 0, preRelease = "A3-")
        Version(1, 0, 0, preRelease = "a.b.---")
        Version(1, 0, 0, preRelease = "abc.120.a1b2c3")

        Version(1, 0, 0, buildMetadata = "20250109.prod-p1.123")
        Version(1, 0, 0, buildMetadata = "-")
        Version(1, 0, 0, buildMetadata = "-A3")
        Version(1, 0, 0, buildMetadata = "A3-")
        Version(1, 0, 0, buildMetadata = "a.b.---")
        Version(1, 0, 0, buildMetadata = "abc.120.007.a1b2c3")


        // Invalid versions
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

        // both pre-release and build metadata invalid
        assertThrowsExactly(errorClass) { Version(1, 0, 0, preRelease = "Ab.023", buildMetadata = "aa bb") }
    }

    @Test
    fun testCreateIdentifiers() {
        val version1 = Version(7, 12, 345, preRelease = "beta-1.123", buildMetadata = "eg-ID.511.4f85-1")
        assertEquals(
            listOf(AlphaNumericIdentifier("beta-1"), NumericIdentifier(123)),
            version1.preReleaseIdentifiers
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
    fun testParse() {
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
}
