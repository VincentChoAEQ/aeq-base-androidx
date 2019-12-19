package ca.aequilibrium.base.util

class Version(version: String) : Comparable<Version> {

    private val major: Int
    private val minor: Int
    private val patch: Int

    init {
        val pattern = Regex("""(0|[1-9]\d*)?(?:\.)?(0|[1-9]\d*)?(?:\.)?(0|[1-9]\d*)?(?:-([\dA-z\-]+(?:\.[\dA-z\-]+)*))?(?:\+([\dA-z\-]+(?:\.[\dA-z\-]+)*))?""")
        val result = pattern.matchEntire(version)
            ?: throw IllegalArgumentException("Invalid version string [$version]")
        major = if (result.groupValues[1].isEmpty()) 0 else result.groupValues[1].toInt()
        minor = if (result.groupValues[2].isEmpty()) 0 else result.groupValues[2].toInt()
        patch = if (result.groupValues[3].isEmpty()) 0 else result.groupValues[3].toInt()
    }

    override fun toString(): String = buildString {
        append("$major.$minor.$patch")
    }

    override fun compareTo(other: Version): Int {
        if (major > other.major) return 1
        if (major < other.major) return -1
        if (minor > other.minor) return 1
        if (minor < other.minor) return -1
        if (patch > other.patch) return 1
        if (patch < other.patch) return -1
        return 0
    }
}