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

package io.github.abductcows.easyargs.parser

import io.github.abductcows.easyargs.arguments.Argument

/**
 * Parses the program arguments, looking for programmer defined ones.
 *
 * Returns a queryable result that can return:
 * - whether an argument has been supplied
 * - if it is an argument-value pair, the value
 */
class ArgumentParser {

    private var argsLookupByName: MutableMap<String, Argument> = HashMap()
    private var result: ArgumentParserResult = ArgumentParserResult()
    private var parsingFinished: Boolean = false

    /**
     * Parses the program arguments String array, looking for occurrences of the arguments provided
     *
     * Any other arguments are left intact and will not throw an exception. The parser finishes parsing and then
     * throws any exceptions related to the arguments specified. If you recover from this exception, the valid arguments
     * will still be available by calling [getParseResult]
     *
     * @throws BadArgumentUseException if a String matching an [argument][Argument] violates its specification. E.g.
     * does not have a value while [Argument.getNeedsValue] is true
     */
    fun parseForMyArgs(programArgs: Array<String>, myArgs: List<Argument>): ArgumentParserResult {
        var parseException: Exception? = null

        // populate the lookup table
        populateLookupTable(myArgs)

        // parse the arguments
        for (i in programArgs.indices) {
            val currentString = programArgs[i]

            // skip non-specified arguments
            if (!isValidArgumentName(currentString)) continue

            // valid arg name, add if used properly or throw on bad use
            val currentArgument = argsLookupByName.getValue(currentString)
            if (!isProperlyUsed(currentArgument, i, programArgs)) {
                val message = "Sample message" // todo add a proper message depending on argument
                if (parseException == null) {
                    parseException = BadArgumentUseException(message)
                } else {
                    parseException.addSuppressed(BadArgumentUseException(message))
                }
                continue
            }

            // add it
            if (currentArgument.needsValue) {
                result.addArgWithValue(currentArgument, programArgs[i + 1])
            } else {
                result.addSimpleArgument(currentArgument)
            }
        }

        parsingFinished = true
        parseException?.let { throw it }
        return result
    }

    /**
     * Vararg overload of [parseForMyArgs]
     */
    fun parseForMyArgs(programArgs: Array<String>, vararg definedArgs: Argument) =
        parseForMyArgs(programArgs, definedArgs.toList())

    /**
     * Returns the results of the argument parsing. Called after [parseForMyArgs]
     * @throws ParsingNotFinishedException if called before [parseForMyArgs]
     */
    fun getParseResult(): ArgumentParserResult {
        if (!parsingFinished) throw ParsingNotFinishedException()
        return result
    }

    private fun populateLookupTable(myArgs: List<Argument>) {
        for (argument in myArgs) {
            if (argument.shortName.isNotEmpty()) {
                argsLookupByName["-${argument.shortName}"] = argument
            }
            if (argument.longName.isNotEmpty()) {
                argsLookupByName["--${argument.longName}"] = argument
            }
        }
    }

    private fun isValidArgumentName(arg: String) = argsLookupByName.contains(arg)
    private fun isProperlyUsed(argument: Argument, argumentIndex: Int, programArgs: Array<String>): Boolean {

        // check if it needs a value
        if (argument.needsValue) {
            if (argumentIndex + 1 !in programArgs.indices || // no more args
                isValidArgumentName(programArgs[argumentIndex + 1]) // or next one is another argument
            ) {
                return false
            }
        }

        return true
    }

}

