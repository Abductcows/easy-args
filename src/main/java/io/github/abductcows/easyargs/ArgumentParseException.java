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

/**
 * Base class for exceptions thrown during the parsing phase.
 * <p>
 * Can be caught to recover from a parsing error due to the {@link ArgumentParser} always finishing parsing before throwing.
 */
public class ArgumentParseException extends RuntimeException {

    public ArgumentParseException(String message) {
        super(message);
    }

    /**
     * Exception signaling argument string matching an {@link Argument}, but failing to comply with its specification.
     * <p>
     * Example: Program called with arguments "--port --quiet", but port requiring a value
     */
    public static class BadArgumentUseException extends ArgumentParseException {

        public BadArgumentUseException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when parser result is called before parsing.
     */
    public static class ParsingNotFinishedException extends ArgumentParseException {

        public ParsingNotFinishedException() {
            super("Argument results requested but argument parsing not finished yet. " +
                    "Did you call parser.parseForMyArgs(..)?");
        }
    }

    /**
     * Thrown in the initial parsing stage when there are multiple arguments with the same name. You should generally not
     * recover from this. Just design arguments with different names.
     */
    public static class DuplicateArgumentNameException extends ArgumentParseException {

        public DuplicateArgumentNameException(String argumentString) {
            super("Argument " + argumentString + " has been defined more than once");
        }
    }
}
