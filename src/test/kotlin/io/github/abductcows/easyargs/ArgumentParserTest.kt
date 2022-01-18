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

import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource


internal class ArgumentParserTest {

    private lateinit var parser: ArgumentParser

    @BeforeEach
    fun setUp() {
        parser = ArgumentParser()
    }

    @Test
    @DisplayName("should parse any program args with no defined args successfully")
    fun `parse random args with empty list`() {

        // given
        val anyProgramArguments = arrayOf("hello", "world", "dontcare")

        // when/then
        assertDoesNotThrow {
            parser.parseForMyArgs(anyProgramArguments, listOf())
        }
    }

    @Test
    @DisplayName("should parse empty program args with any defined args successfully")
    fun `parse empty args with random list`() {

        // given
        val anyDefinedArguments = listOf(
            Argument.withLongName("foo").build(),
            Argument.withLongName("bar").needsValue().build(),
            Argument.withShortName("h").build(),
        )

        // when/then
        assertDoesNotThrow {
            parser.parseForMyArgs(arrayOf(), anyDefinedArguments)
        }
    }


    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserTest#programArgsThatViolateDefinedArgs")
    @DisplayName("should throw on improper defined argument use")
    fun `parse program args with some violation`(programArgs: Array<String>, definedArgs: List<Argument>) {

        // when/then
        assertThrows<ArgumentParseException> {
            parser.parseForMyArgs(programArgs, definedArgs)
        }
    }

    @ParameterizedTest
    @MethodSource("io.github.abductcows.easyargs.ArgumentParserTest#programArgsThatViolateDefinedArgs")
    @DisplayName("result should be available even after throwing parse exception")
    fun `parse exceptions should allow result retrieval`(programArgs: Array<String>, definedArgs: List<Argument>) {

        // when/then
        assertThrows<ArgumentParseException> {
            parser.parseForMyArgs(programArgs, definedArgs)
        }

        assertDoesNotThrow {
            parser.parserResult
        }
    }

    @Test
    @DisplayName("should throw exception when parse result requested without parsing first")
    fun `getParseResult unhappy path test`() {

        // when/then
        assertThrows<ArgumentParseException.ParsingNotFinishedException> {
            parser.parserResult
        }
    }

    companion object {

        /**
         *  Returns pairs of ```String[], List<Argument>``` (program args, defined args)
         */
        @JvmStatic
        private fun programArgsThatViolateDefinedArgs(): Iterable<org.junit.jupiter.params.provider.Arguments> {
            return listOf(
                arguments(
                    arrayOf("--port"), listOf(
                        Argument.withLongName("port").needsValue().build()
                    )
                ),
                arguments(
                    arrayOf("-h", "-v"), listOf(
                        Argument.withShortName("v").needsValue().build()
                    )
                )
            )
        }
    }
}
