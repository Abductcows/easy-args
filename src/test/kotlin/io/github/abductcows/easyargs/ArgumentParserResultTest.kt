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

package io.github.abductcows.easyargs

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as whenever

@ExtendWith(MockitoExtension::class)
internal class ArgumentParserResultTest {

    private lateinit var result: ArgumentParserResult

    @BeforeEach
    fun setup() {

        result = ArgumentParserResult()
    }

    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserResultTest#randomSimpleArgs")
    @DisplayName("should contain the simple arg put into it")
    fun `should contain the simple args put into it`(argument: Argument) {

        // when
        result.addSimpleArgument(argument)

        // then
        assertThat(result.contains(argument)).isTrue
    }

    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserResultTest#randomSimpleArgs")
    @DisplayName("should retrieve the simple arg put into it by name")
    fun `should retrieve the simple args put into it by name`(argument: Argument) {

        // when
        result.addSimpleArgument(argument)

        // then
        argument.shortName.let {
            if (it.isNotEmpty()) {
                assertThat(result.contains(it)).isTrue
            }
        }

        argument.longName.let {
            if (it.isNotEmpty()) {
                assertThat(result.contains(it)).isTrue
            }
        }
    }

    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserResultTest#randomArgsWithValue")
    @DisplayName("should retrieve the arg and value put into it by name")
    fun `should retrieve the arg and value put into it by name`(argument: Argument) {

        // when
        result.addArgumentWithValue(argument, "asdf")

        // then
        argument.shortName.takeIf { it.isNotEmpty() }?.let {
            assertThat(result.contains(it)).isTrue
            assertDoesNotThrow {
                result.getValue(it)
            }
        }

        argument.longName.takeIf { it.isNotEmpty() }?.let {
            assertThat(result.contains(it)).isTrue
            assertDoesNotThrow {
                result.getValue(it)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserResultTest#randomArgsWithValue")
    @DisplayName("should contain the arg with value put into it and retrieve its value")
    fun `should contain the args with value put into it`(argument: Argument) {

        // when
        result.addArgumentWithValue(argument, "asdf") // dont care about value

        // then
        assertThat(result.contains(argument)).isTrue
        assertDoesNotThrow {
            result.getValue(argument)
        }
    }

    companion object {

        @JvmStatic
        fun randomSimpleArgs(): Iterable<Argument> {
            val simpleArgBothNames = mockArgument("h", "help", false)
            val simpleArgShortName = mockArgument("v", "", false)
            val simpleArgLongName = mockArgument("", "noinline", false)
            val simpleArgWithSameNames = mockArgument("arg", "arg", false)


            return listOf(simpleArgBothNames, simpleArgShortName, simpleArgLongName, simpleArgWithSameNames)
        }

        @JvmStatic
        fun randomArgsWithValue(): Iterable<Argument> {
            val argWithValueBothNames = mockArgument("p", "port", true)
            val argWithValueShortName = mockArgument("n", "", true)
            val argWithValueLongName = mockArgument("", "timeout", true)
            val argWithValueAndSameNames = mockArgument("y", "y", true)

            return listOf(argWithValueBothNames, argWithValueShortName, argWithValueLongName, argWithValueAndSameNames)
        }

        private fun mockArgument(short: String, long: String, value: Boolean): Argument {
            val mock = mock(Argument::class.java)

            whenever(mock.shortName).thenReturn(short)
            whenever(mock.longName).thenReturn(long)
            whenever(mock.needsValue).thenReturn(value)
            whenever(mock.toString()).thenReturn(
                "Argument(shortName='$short', longName='$long', needsValue=$value')"
            )

            return mock
        }
    }
}
