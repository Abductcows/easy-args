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
     * {@link ShortNameBuilder#longName(String)} method
     * </p>
     * <p>
     * Short names correspond to -&lt;argument&gt; usage
     * </p>
     *
     * @param shortName the short name of the new argument
     * @return builder for an argument with the given short name
     */
    public static ShortNameBuilder withShortName(String shortName) {
        return new ShortNameBuilder(shortName);
    }

    /**
     * Constructs an {@link Argument} instance with a non-empty long name through the builder.
     * <p>
     * The argument can optionally be aliased with a short name using the
     * {@link LongNameBuilder#shortName(String)} method
     * </p>
     * <p>
     * Long names correspond to --&lt;argument&gt; usage
     * </p>
     *
     * @param longName the long name of the new argument
     * @return builder for an argument with the given long name
     */
    public static LongNameBuilder withLongName(String longName) {
        return new LongNameBuilder(longName);
    }


    private static class Builder {
        protected Argument argument = new Argument();

        /**
         * Sets the argument to require a value
         *
         * @return the builder instance
         */
        public Builder needsValue() {
            return needsValue(true);
        }

        /**
         * Sets the argument to require a value depending on the boolean expression
         * <p>
         * Convenience method for using whenever there is a computed value for this. Manual creation of few arguments
         * is easier using {@link #needsValue()}, or lack thereof, to signify no value at all
         *
         * @param needsValue whether the argument will accept a value
         * @return the builder instance
         */
        public Builder needsValue(boolean needsValue) {
            argument.needsValue = needsValue;
            return this;
        }

        /**
         * Sets a description for the argument. Default is empty
         *
         * @param description a description string
         * @return the builder instance
         */
        public Builder description(String description) {
            argument.description = description;
            return this;
        }

        /**
         * Returns the {@link Argument} instance being built
         *
         * @return the builder result object
         */
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

        /**
         * Sets a long name alias for the argument (used with --&lt;name&gt;)
         *
         * @param longName the long name
         * @return the builder instance
         */
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

        /**
         * Sets a short name alias for the argument (used with -&lt;name&gt;)
         *
         * @param shortName the short name
         * @return the builder instance
         */
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

    /**
     * A long short name (called with -&lt;name&gt;, default = empty String)
     *
     * @return the short name for this argument
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * A long name (called with --&lt;name&gt;, default = empty String)
     *
     * @return the short name for this argument
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Whether it needs a value  (default = false)
     *
     * @return whether this argument needs a value
     */
    public boolean getNeedsValue() {
        return needsValue;
    }

    /**
     * Argument description (default = empty String)
     *
     * @return a short description of the argument
     */
    public String getDescription() {
        return description;
    }

    /**
     * String concatenation of all members for human-readable output/debugging
     *
     * @return string representation of the object
     */
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