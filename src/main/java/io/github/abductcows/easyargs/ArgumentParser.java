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

import io.github.abductcows.easyargs.parser.ArgumentParserResult;
import io.github.abductcows.easyargs.parser.BadArgumentUseException;
import io.github.abductcows.easyargs.parser.ParsingNotFinishedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the program arguments, looking for programmer defined ones.
 * <p>
 * Returns a queryable result that can return:
 * <ul>
 *     <li>whether an argument has been supplied</li>
 *     <li>if it is an argument-value pair, the value</li>
 * </ul>
 */
public class ArgumentParser {

    private Map<String, Argument> argsLookupByName;
    private ArgumentParserResult result;
    private boolean parsingFinished;

    public ArgumentParser() {
        reset();
    }


    public ArgumentParserResult parseForMyArgs(String[] programArgs, List<Argument> myArgs) {

        RuntimeException parseException = null;

        populateLookupTable(myArgs);

        for (int i = 0; i < programArgs.length; i++) {
            String currentArg = programArgs[i];
            // skip non-specified arguments
            if (!isValidArgumentName(currentArg)) continue;

            Argument currentArgument = argsLookupByName.get(currentArg);
            if (!isProperlyUsed(currentArgument, programArgs, i)) {
                String message = "Sample message"; // todo add a proper message depending on argument

                if (parseException == null) {
                    parseException = new BadArgumentUseException(message);
                } else {
                    parseException.addSuppressed(new BadArgumentUseException(message));
                }
                continue;
            }
            if (currentArgument.getNeedsValue()) {
                result.addArgWithValue$easy_args(currentArgument, programArgs[i + 1]);
            } else {
                result.addSimpleArgument$easy_args(currentArgument);
            }
        }

        parsingFinished = true;
        if (parseException != null) {
            throw parseException;
        }
        return result;
    }

    public ArgumentParserResult getParserResult() {
        if (!parsingFinished) throw new ParsingNotFinishedException();
        ArgumentParserResult result = this.result;
        reset();
        return result;
    }

    private void populateLookupTable(List<Argument> myArgs) {

        for (Argument argument : myArgs) {
            if (!argument.getShortName().isEmpty()) {
                argsLookupByName.put("-" + argument.getShortName(), argument);
            }
            if (!argument.getLongName().isEmpty()) {
                argsLookupByName.put("--" + argument.getLongName(), argument);
            }
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isProperlyUsed(Argument argument, String[] programArgs, int indexInProgramArgs) {

        // fail if it needs a value but there is none
        if (argument.getNeedsValue()) {
            int indexOfValue = indexInProgramArgs + 1;
            if (indexOfValue >= programArgs.length || // end of arguments, no value
                    isValidArgumentName(programArgs[indexOfValue])) { // next is another argument
                return false;
            }
        }

        return true;
    }

    private boolean isValidArgumentName(String currentArg) {
        return argsLookupByName.containsKey(currentArg);
    }

    private void reset() {
        argsLookupByName = new HashMap<>();
        result = new ArgumentParserResult();
        parsingFinished = false;
    }
}
