@file:Suppress("unused")

package io.github.abductcows.easyargs.arguments

class Argument internal constructor(
    shortName: String = "",
    longName: String = "",
    needsValue: Boolean = false,
    description: String = ""
) {
    /**
     * Short name of the argument e.g. -p or -v
     */
    var shortName: String = shortName
        private set

    /**
     * Full name of the argument e.g. --port or --version
     */
    var longName: String = longName
        private set

    /**
     * Whether the argument needs an additional value e.g. --port 8080
     */
    var needsValue: Boolean = needsValue
        private set

    /**
     * Quick summary of the argument's effect
     */
    var description: String = description
        private set

    companion object {
        @JvmStatic
        fun withShortName(shortName: String) = Builder().apply { shortName(shortName) }

        @JvmStatic
        fun withLongName(longName: String) = Builder().apply { longName(longName) }
    }

    class Builder {
        private var current: Argument = Argument()

        fun shortName(shortName: String) = apply { current.shortName = shortName }
        fun longName(longName: String) = apply { current.longName = longName }
        fun needsValue(needsValue: Boolean) = apply { current.needsValue = needsValue }
        fun description(description: String) = apply { current.description = description }

        fun build(): Argument = current.also { reset() }

        private fun reset() {
            current = Argument()
        }
    }
}