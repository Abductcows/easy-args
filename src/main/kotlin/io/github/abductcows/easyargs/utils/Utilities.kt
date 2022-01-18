package io.github.abductcows.easyargs.utils

import io.github.abductcows.easyargs.arguments.Argument

internal inline fun runForBothNames(argument: Argument, block: (String) -> Unit) {
    argument.shortName.takeIf { it.isNotEmpty() }?.let(block)
    argument.longName.takeIf { it.isNotEmpty() }?.let(block)
}

internal inline fun <T> returnForFirstNonEmptyName(argument: Argument, block: (String) -> T): T {
    argument.shortName.takeIf { it.isNotEmpty() }?.let { return block(it) }
    argument.longName.takeIf { it.isNotEmpty() }?.let { return block(it) }
    throw IllegalArgumentException("$argument has no names")
}