@file:Suppress("unused")

package io.github.abductcows.easyargs.arguments

class Argument @JvmOverloads constructor(
    /**
     * Short name of the argument e.g. -p or -v
     */
    var shortName: String = "",
    /**
     * Full name of the argument e.g. --port or --version
     */
    var longName: String = "",
    /**
     * Whether the argument needs an additional value e.g. --port 8080
     */
    var needsValue: Boolean = false,
    /**
     * Quick summary of the argument's effect
     */
    var description: String = "",
    /**
     * Whether the argument is required or not
     * // TODO decide how to report this on error
     */
    var required: Boolean = false
) {

    companion object {

        fun builder() = Builder.instance.apply { reset() }
    }

    class Builder private constructor() {

        companion object {
            val instance = Builder()
        }

        private var current: Argument? = null

        fun shortName(shortName: String) = apply { current?.shortName = shortName }
        fun longName(longName: String) = apply { current?.longName = longName }
        fun needsValue(needsValue: Boolean) = apply { current?.needsValue = needsValue }
        fun description(description: String) = apply { current?.description = description }
        fun required(required: Boolean) = apply { current?.required = required }

        fun build() = current.also { current = null }
        fun reset() { current = Argument() }
    }
}