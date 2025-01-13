package org.lasantha.kotlindemo.semver

/**
 * A simple class to represent a semantic version.
 * See https://semver.org/ for more information.
 */
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: String? = null,
    val buildMetadata: String? = null
) : Comparable<Version> {
    val preReleaseIdentifiers: List<Identifier<Any>>
    val buildMetadataIdentifiers: List<AlphaNumericIdentifier>

    init {
        require(major >= 0) { "Major version must be a non-negative integer" }
        require(minor >= 0) { "Minor version must be a non-negative integer" }
        require(patch >= 0) { "Patch version must be a non-negative integer" }

        if (preRelease == null) {
            preReleaseIdentifiers = emptyList()
        } else {
            preReleaseIdentifiers = VersionUtil.parsePreReleaseIdentifiers(preRelease)
            require(preReleaseIdentifiers.isNotEmpty()) { "Build metadata is invalid" }
        }

        if (buildMetadata == null) {
            buildMetadataIdentifiers = emptyList()
        } else {
            buildMetadataIdentifiers = VersionUtil.parseBuildMetadataIdentifiers(buildMetadata)
            require(buildMetadataIdentifiers.isNotEmpty()) { "Build metadata is invalid" }
        }
    }

    companion object {
        fun parse(versionString: String) = VersionUtil.parse(versionString)
    }

    override fun toString(): String = when {
        preRelease == null && buildMetadata == null -> "$major.$minor.$patch"
        else -> "$major.$minor.$patch${preRelease?.let { "-$it" } ?: ""}${buildMetadata?.let { "+$it" } ?: ""}"
    }

    override fun compareTo(other: Version): Int {
        if (major != other.major) return major.compareTo(other.major)
        if (minor != other.minor) return minor.compareTo(other.minor)
        if (patch != other.patch) return patch.compareTo(other.patch)

        val size = preReleaseIdentifiers.size
        val otherSize = other.preReleaseIdentifiers.size
        if (size == 0 && otherSize == 0) return 0
        else if (size == 0) return 1
        else if (otherSize == 0) return -1
        else {
            for (i in 0..<minOf(size, otherSize)) {
                val cmp = preReleaseIdentifiers[i].compareTo(other.preReleaseIdentifiers[i])
                if (cmp != 0) return cmp
            }
            return size.compareTo(otherSize)
        }
        // build metadata is ignored when comparing versions
    }

    fun nextMajor(preRelease: String? = null, buildMetadata: String? = null) =
        Version(major + 1, 0, 0, preRelease, buildMetadata)

    fun nextMinor(preRelease: String? = null, buildMetadata: String? = null) =
        Version(major, minor + 1, 0, preRelease, buildMetadata)

    fun nextPatch(preRelease: String? = null, buildMetadata: String? = null) =
        Version(major, minor, patch + 1, preRelease, buildMetadata)
}

sealed interface Identifier<out T> : Comparable<Identifier<Any>> {
    val value: T

    override fun compareTo(other: Identifier<Any>): Int = when (this) {
        is NumericIdentifier -> when (other) {
            is NumericIdentifier -> value.compareTo(other.value)
            is AlphaNumericIdentifier -> -1
        }

        is AlphaNumericIdentifier -> when (other) {
            is NumericIdentifier -> 1
            is AlphaNumericIdentifier -> value.compareTo(other.value)
        }
    }
}

/**
 * A numeric identifier. Consists of one or more digits that can be
 * converted to an integer. Leading zeros are not allowed.
 */
data class NumericIdentifier(override val value: Int) : Identifier<Int>

/**
 * An alphanumeric identifier. Consists of one or more characters from `[0-9A-Za-z-]`.
 */
data class AlphaNumericIdentifier(override val value: String) : Identifier<String>
