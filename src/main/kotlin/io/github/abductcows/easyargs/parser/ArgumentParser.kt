@file:Suppress("unused")

package io.github.abductcows.easyargs.parser

import io.github.abductcows.easyargs.arguments.Argument

/**
 * Parses the program arguments, looking for user defined ones.
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
     * does not have a value while [Argument.needsValue] is true
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

class BadArgumentUseException(message: String) : RuntimeException(message)
class ParsingNotFinishedException : RuntimeException(
    "Argument results requested but argument parsing not finished yet. " +
            "Did you call parser.parseForMyArgs(..)?"
)

class DuplicateArgumentNameException(message: String) : RuntimeException(message)
