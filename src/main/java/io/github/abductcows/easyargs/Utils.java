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

package io.github.abductcows.easyargs;

import java.util.function.Consumer;
import java.util.function.Function;

class Utils {

    /**
     * Runs the block of code for every non-empty names of the argument
     * <p>
     * This will probably reduce boilerplate
     *
     * @param argument the argument
     * @param block    the function to be called on each non-empty name
     */
    static void runForNonEmptyNames(Argument argument, Consumer<String> block) {
        if (!argument.getShortName().isEmpty()) {
            block.accept(argument.getShortName());
        }
        if (!argument.getLongName().isEmpty()) {
            block.accept(argument.getLongName());
        }
    }

    /**
     * Runs the block of code for every non-empty names of the argument, adding hyphens to turn
     * them into command strings first (- for short name and -- for long name)
     * <p>
     * Gotta love declarative
     *
     * @param argument the argument
     * @param block    the function to be called on each non-empty name
     */
    static void runForNonEmptyNamesAddingHyphens(Argument argument, Consumer<String> block) {
        if (!argument.getShortName().isEmpty()) {
            block.accept("-" + argument.getShortName());
        }
        if (!argument.getLongName().isEmpty()) {
            block.accept("--" + argument.getLongName());
        }
    }

    /**
     * Runs the transform for the first non-empty name of the argument and returns the result.
     * <p>
     * Probably reduces boilerplate
     *
     * @param argument the argument
     * @param function the function to be applied to the name
     * @param <T>      generic result parameter for reusability
     * @return the result of the function
     */
    static <T> T returnForFirstNonEmptyName(Argument argument, Function<String, T> function) {
        if (!argument.getLongName().isEmpty()) {
            return function.apply(argument.getLongName());
        }
        if (!argument.getShortName().isEmpty()) {
            return function.apply(argument.getShortName());
        }
        throw new IllegalArgumentException(argument + " has no non-empty names");
    }
}
