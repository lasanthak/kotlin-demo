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
) {
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

    override fun toString(): String = when {
        preRelease == null && buildMetadata == null -> "$major.$minor.$patch"
        else -> "$major.$minor.$patch${preRelease?.let { "-$it" } ?: ""}${buildMetadata?.let { "+$it" } ?: ""}"
    }

    // Todo - Implement the comparison operators

    companion object {
        fun parse(versionString: String) = VersionUtil.parse(versionString)
    }
}

sealed interface Identifier<out T> {
    val value: T
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
