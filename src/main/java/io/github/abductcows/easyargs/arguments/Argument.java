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

package io.github.abductcows.easyargs.arguments;

import io.github.abductcows.easyargs.CustomNonNullAPI;

import java.util.StringJoiner;


/**
 * <h3>Models a programmer-defined program argument</h3>
 * <p>Use the static builder methods {@link #withShortName(String)} and {@link #withLongName(String)} to create an instance<br><p>
 * Currently supports the following properties:
 * <ul>
 *     <li> a short name (specified with -&lt;argument&gt; in the command line) </li>
 *     <li> a long name (specified with --&lt;argument&gt; in the command line) </li>
 *     <li> whether it accepts a value e.g. --port 8080 (default: false) </li>
 *     <li> a description; brief summary of the argument's utility </li>
 * </ul>
 */
@CustomNonNullAPI
public final class Argument {

    /**
     * Private constructor
     *
     * <p>Use the static builder methods {@link #withShortName(String)} and {@link #withLongName(String)} to create an instance</p>
     */
    private Argument() {
    }

    /**
     * Short name of the argument e.g. -p or -v
     */
    private String shortName = "";

    /**
     * Full name of the argument e.g. --port or --version
     */
    private String longName = "";

    /**
     * Whether the argument needs an additional value e.g. --port 8080
     */
    private boolean needsValue = false;

    /**
     * Quick summary of the argument's effect
     */
    private String description = "";

    /**
     * Constructs an {@link Argument} instance with a non-empty short name through the builder.
     * <p>
     * The argument can optionally be aliased with a long name using the
     * {@link #withLongName(String)} method
     * <p>
     * Short names correspond to {@code -&lt;argument&gt;} usage
     */
    public static ShortNameBuilder withShortName(String shortName) {
        return new ShortNameBuilder(shortName);
    }

    /**
     * Constructs an {@link Argument} instance with a non-empty long name through the builder.
     * <p>
     * The argument can optionally be aliased with a short name using the
     * {@link #withShortName(String)} method
     * <p>
     * Long names correspond to {@code --&lt;argument&gt;} usage
     */
    public static LongNameBuilder withLongName(String longName) {
        return new LongNameBuilder(longName);
    }


    private static class Builder {
        protected Argument argument = new Argument();

        public Builder needsValue() {
            return needsValue(true);
        }

        public Builder needsValue(boolean needsValue) {
            argument.needsValue = needsValue;
            return this;
        }

        public Builder description(String description) {
            argument.description = description;
            return this;
        }

        public Argument build() {
            return argument;
        }
    }

    /**
     * Builder class for an {@link Argument} with at least a short name (prefixed with - when used)
     */
    public static final class ShortNameBuilder extends Builder {

        private ShortNameBuilder(String shortName) {
            if (shortName.isEmpty()) {
                throw new IllegalArgumentException("Short name cannot be empty");
            }
            argument.shortName = shortName;
        }

        public ShortNameBuilder longName(String longName) {
            argument.longName = longName;
            return this;
        }

        // Overrides

        @Override
        public ShortNameBuilder needsValue() {
            super.needsValue();
            return this;
        }

        @Override
        public ShortNameBuilder needsValue(boolean needsValue) {
            super.needsValue(needsValue);
            return this;
        }

        @Override
        public ShortNameBuilder description(String description) {
            super.description(description);
            return this;
        }
    }

    /**
     * Builder class for an {@link Argument} with at least a long name (prefixed with -- when used)
     */
    public static final class LongNameBuilder extends Builder {

        private LongNameBuilder(String longName) {
            if (longName.isEmpty()) {
                throw new IllegalArgumentException("Long name cannot be empty");
            }
            argument.longName = longName;
        }

        public LongNameBuilder shortName(String shortName) {
            argument.shortName = shortName;
            return this;
        }

        // Overrides

        @Override
        public LongNameBuilder needsValue() {
            super.needsValue();
            return this;
        }

        @Override
        public LongNameBuilder needsValue(boolean needsValue) {
            super.needsValue(needsValue);
            return this;
        }

        @Override
        public LongNameBuilder description(String description) {
            super.description(description);
            return this;
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public boolean getNeedsValue() {
        return needsValue;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Argument.class.getSimpleName() + "[", "]")
                .add("shortName='" + shortName + "'")
                .add("longName='" + longName + "'")
                .add("needsValue=" + needsValue)
                .add("description='" + description + "'")
                .toString();
    }

}