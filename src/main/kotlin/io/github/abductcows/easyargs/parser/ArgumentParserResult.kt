package io.github.abductcows.easyargs.parser

import io.github.abductcows.easyargs.arguments.Argument

/**
 * Result of program arguments parsing based on the programmer's expected arguments.
 *
 * It can be queried to find if an argument was supplied or not. Or to get its value if applicable
 */
class ArgumentParserResult {

    // this implementation is based on the fact that no two arguments can have any same name (both short or long)
    private val simpleArgs = LinkedHashSet<String>()
    private val argsWithValue = LinkedHashMap<String, String>()

    internal fun addSimpleArgument(argument: Argument) = runForBothNames(argument, simpleArgs::add)

    internal fun addArgWithValue(argument: Argument, value: String) = runForBothNames(argument) { name ->
        argsWithValue[name] = value
    }

    /**
     * Returns whether the argument exists (short or long name without the - or --)
     */
    operator fun contains(argumentName: String): Boolean {
        return simpleArgs.contains(argumentName) || argsWithValue.contains(argumentName)
    }

    /**
     * Returns whether the argument was supplied to the program or not
     */
    operator fun contains(argument: Argument): Boolean = returnForFirstNonEmptyName(argument, ::contains)

    /**
     * Returns the value string of the argument
     */
    fun getValue(argumentName: String): String {
        val getResult = argsWithValue[argumentName]
        if (getResult != null) return getResult

        // else throw an appropriate exception
        throw NoSuchElementException("Argument \"$argumentName\" does not have a value")
    }

    fun getValue(argument: Argument): String = returnForFirstNonEmptyName(argument, ::getValue)

    private inline fun runForBothNames(argument: Argument, block: (String) -> Unit) {
        argument.shortName.takeIf { it.isNotEmpty() }?.let(block)
        argument.longName.takeIf { it.isNotEmpty() }?.let(block)
    }

    private inline fun <T> returnForFirstNonEmptyName(argument: Argument, block: (String) -> T): T {
        argument.shortName.takeIf { it.isNotEmpty() }?.let { return block(it) }
        argument.longName.takeIf { it.isNotEmpty() }?.let { return block(it) }
        throw IllegalArgumentException("$argument has no names")
    }

    override fun toString(): String {
        return "ArgumentParserResult(simpleArgs=$simpleArgs, argsWithValue=$argsWithValue)"
    }
}