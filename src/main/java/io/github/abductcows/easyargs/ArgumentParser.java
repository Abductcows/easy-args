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

import io.github.abductcows.easyargs.ArgumentParseException.BadArgumentUseException;
import io.github.abductcows.easyargs.ArgumentParseException.DuplicateArgumentNameException;
import io.github.abductcows.easyargs.ArgumentParseException.ParsingNotFinishedException;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.abductcows.easyargs.Utils.runForNonEmptyNamesAddingHyphens;

/**
 * Parser object that parses the program arguments, looking for programmer defined ones.
 * <p>
 * Returns a queryable result that can return:
 * <ul>
 *     <li>whether an argument has been supplied</li>
 *     <li>if it is an argument-value pair, the value</li>
 * </ul>
 */
@CustomNonNullAPI
public final class ArgumentParser {

    private Map<String, Argument> argsLookupByName = new HashMap<>();
    private ArgumentParserResult result = new ArgumentParserResult();
    private boolean parsingFinished = false;

    @Nullable
    private RuntimeException parseException = null;

    /**
     * Argument parser constructor
     * <p>
     * Parser objects can be reused as new after a call to {@link #getParserResult}
     */
    public ArgumentParser() {
    }

    /**
     * Parses the program arguments and returns the queryable result used to handle the arguments
     * <p>
     * All exceptions are stored and thrown after parsing has finished. If caught, the remaining valid arguments
     * are available via {@link #getParserResult()}. If multiple exceptions occur, the first one is thrown, with the others
     * suppressed
     * </p>
     *
     * @param programArgs the program arguments
     * @param myArgs      programmer defined arguments
     * @return the result of argument parsing
     * @throws BadArgumentUseException        if an expected argument is supplied (same name) but does not conform to the full
     *                                        specification (e.g. --port without value). The offending argument is discarded.
     * @throws DuplicateArgumentNameException if two arguments are found with the same short or long name. The first argument will be kept.
     */
    public ArgumentParserResult parseForMyArgs(String[] programArgs, List<Argument> myArgs) {

        populateLookupTable(myArgs);

        for (int i = 0; i < programArgs.length; i++) {
            String currentArg = programArgs[i];

            // skip non-specified arguments
            if (!isValidArgumentName(currentArg)) continue;

            Argument currentArgument = argsLookupByName.get(currentArg);
            if (assertProperlyUsed(currentArgument, programArgs, i)) {
                storeArgumentInResult(currentArgument, programArgs, i);
            }

            removeFromLookupTable(currentArgument);
        }

        parsingFinished = true;
        if (parseException != null) {
            throw parseException;
        }
        return result;
    }

    /**
     * Vararg version of {@link #parseForMyArgs(String[], List)}
     *
     * @param programArgs the program arguments
     * @param myArgs      programmer defined arguments
     * @return the result of argument parsing
     * @see #parseForMyArgs(String[], List)
     */
    public ArgumentParserResult parseForMyArgs(String[] programArgs, Argument... myArgs) {
        return parseForMyArgs(programArgs, Arrays.asList(myArgs));
    }

    /**
     * Returns the result of the last argument parsing.
     * <p>
     * The result is returned only once. Subsequent calls without {@link #parseForMyArgs}
     * will return empty {@link ArgumentParserResult} objects.
     * </p>
     *
     * @return the queryable result
     * @throws ParsingNotFinishedException if called before {@link #parseForMyArgs}
     */
    public ArgumentParserResult getParserResult() {
        if (!parsingFinished) throw new ParsingNotFinishedException();
        ArgumentParserResult result = this.result;
        reset();
        return result;
    }

    /**
     * Stores the argument in the current result, including its value where applicable
     *
     * @param argument           the argument
     * @param programArgs        the program arguments
     * @param indexInProgramArgs index of the argument in the program arguments
     */
    private void storeArgumentInResult(Argument argument, String[] programArgs, int indexInProgramArgs) {
        if (argument.getNeedsValue()) {
            result.addArgumentWithValue(argument, programArgs[indexInProgramArgs + 1]);
        } else {
            result.addSimpleArgument(argument);
        }
    }

    /**
     * Adds the argument entries in the lookup table.
     * <p>
     * Skips already defined arguments and stores an exception
     * </p>
     *
     * @param myArgs the programmer-defined argument list
     */
    private void populateLookupTable(List<Argument> myArgs) {

        for (Argument argument : myArgs) {

            runForNonEmptyNamesAddingHyphens(argument, (argumentNameWithHyphens -> {
                if (assertArgumentNotInLookup(argumentNameWithHyphens)) {
                    argsLookupByName.put(argumentNameWithHyphens, argument);
                }
            }));
        }
    }

    private boolean assertArgumentNotInLookup(String command) {

        if (argsLookupByName.containsKey(command)) {
            storeException(new DuplicateArgumentNameException(command));
            return false;
        }

        return true;
    }

    private boolean isValidArgumentName(String currentArg) {
        return argsLookupByName.containsKey(currentArg);
    }

    private boolean assertProperlyUsed(Argument argument, String[] programArgs, int indexInProgramArgs) {

        // fail if it needs a value but there is none
        if (argument.getNeedsValue()) {
            int indexOfValue = indexInProgramArgs + 1;
            if (indexOfValue >= programArgs.length || // end of arguments, no value
                    isValidArgumentName(programArgs[indexOfValue])) { // next is another argument
                String readableName = Utils.returnForFirstNonEmptyName(argument, String::toString);
                String nextValue = indexOfValue >= programArgs.length ? "" : programArgs[indexOfValue];
                storeException(new BadArgumentUseException(readableName + " requires a value, but \"" + nextValue +
                        "\" cannot be used as a value"));
                return false;
            }
        }

        return true;
    }

    private void storeException(RuntimeException e) {
        if (parseException == null) {
            parseException = e;
        } else {
            parseException.addSuppressed(e);
        }
    }

    private void removeFromLookupTable(Argument argument) {
        runForNonEmptyNamesAddingHyphens(argument, argsLookupByName::remove);
    }

    private void reset() {
        argsLookupByName = new HashMap<>();
        result = new ArgumentParserResult();
        parsingFinished = false;
        parseException = null;
    }
}
