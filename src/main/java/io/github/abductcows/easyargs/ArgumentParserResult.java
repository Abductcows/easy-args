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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import static io.github.abductcows.easyargs.Utils.returnForFirstNonEmptyName;
import static io.github.abductcows.easyargs.Utils.runForNonEmptyNames;

/**
 * Result of program arguments parsing based on the programmer's expected arguments.
 * <p>It can be queried to find if an argument was supplied or not. Or to get its value, if applicable</p>
 */
@CustomNonNullAPI
public final class ArgumentParserResult {

    /**
     * Package private constructor
     *
     * <p>Instances of this class are returned after argument parsing using {@link ArgumentParser#parseForMyArgs}</p>
     */
    ArgumentParserResult() {
    }

    /**
     * Argument names to potential values mapping
     * <p>
     * Currently, args with no value map to the empty string ("")
     */
    private final Map<String, String> resultArguments = new HashMap<>();

    void addSimpleArgument(Argument argument) {
        runForNonEmptyNames(argument, (name) -> resultArguments.put(name, ""));
    }

    void addArgumentWithValue(Argument argument, String value) {
        runForNonEmptyNames(argument, (name) -> resultArguments.put(name, value));
    }

    /**
     * Queries the result for the existence of a specific argument
     *
     * @param argument the argument
     * @return whether the argument was supplied or not
     */
    public boolean contains(Argument argument) {
        return returnForFirstNonEmptyName(argument, resultArguments::containsKey);
    }

    /**
     * Alias for {@link #contains(Argument)}, using argument name instead
     *
     * @param argumentName the argument name
     * @return whether the argument was supplied or not
     */
    public boolean contains(String argumentName) {
        return resultArguments.containsKey(argumentName);
    }

    /**
     * Gets the value from an argument that supports values by name.
     *
     * <p>Can be used in conjunction with {@link #contains} and {@link Argument#getNeedsValue}</p>
     *
     * @param argument the argument with the value to be retrieved
     * @return the value
     * @throws NoSuchElementException if the argument does not exist or does not support a value
     */
    public String getValue(Argument argument) {
        return returnForFirstNonEmptyName(argument, this::getValue);
    }

    /**
     * Alias for {@link #getValue(Argument)}, using argument name instead
     *
     * @param argumentName the name of the argument
     * @return the value
     */
    public String getValue(String argumentName) {
        String getResult = resultArguments.get(argumentName);
        if (getResult == null) {
            throw new NoSuchElementException("Argument \"" + argumentName + "\" does not exist");
        }
        if (getResult.isEmpty()) {
            throw new NoSuchElementException("Argument \"" + argumentName + "\" exists but does not support a value");
        }

        return getResult;
    }

    /**
     * String concatenation of all members for human-readable output/debugging
     *
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", ArgumentParserResult.class.getSimpleName() + "[", "]")
                .add("resultArguments=" + resultArguments)
                .toString();
    }
}
