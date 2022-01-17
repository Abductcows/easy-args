/*
   Copyright 2022 George Bouroutzoglou

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
@file:Suppress("unused")

package io.github.abductcows.easyargs.arguments

/**
 * Argument domain class. Currently supports the following properties:
 *  - a short name (specified with -`[`name`]` in the command line)
 *  - a long name (specified with --`[`name`]` in the command line)
 *  - whether it accepts a value e.g. --port 8080
 *  - a description; brief summary of the argument's utility
 */
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
        fun needsValue() = apply { current.needsValue = true }
        fun needsValue(needsValue: Boolean) = apply { current.needsValue = needsValue }
        fun description(description: String) = apply { current.description = description }

        fun build(): Argument = current.also { reset() }

        private fun reset() {
            current = Argument()
        }
    }

    override fun toString(): String {
        return "Argument(shortName='$shortName', longName='$longName', needsValue=$needsValue, description='$description')"
    }
}