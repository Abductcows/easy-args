@file:Suppress("unused")

package io.github.abductcows.easyargs.parser

import io.github.abductcows.easyargs.arguments.Argument
import java.util.*

class ArgumentCollection {

    private val values: Collection<Argument> = LinkedHashSet(10)

    operator fun contains(argument: Argument) = values.contains(argument)
    operator fun iterator() = values.iterator()
}

object Parser {

    @JvmStatic
    fun parseForSpecificArgs(argArray: Array<String>, specificArgs: ArgumentCollection): ExistingArgs {
        val res = ExistingArgs()




        return ExistingArgs()
    }
}



class ExistingArgs {

    val values = mutableMapOf<Argument, String?>()
}