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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Use to quickly generate a standard help message for your program
 * <p>
 * Format:
 * <pre>
 *      (header)
 *      (usage)
 *      (options)
 *      (footer)
 * </pre>
 * Example:
 * <pre>{@code
 *         List<Argument> myArgs = List.of(
 *                 Argument.withLongName("port").shortName("p").needsValue()
 *                         .description("specify the server port").build(),
 *                 Argument.withLongName("debug").build(),
 *                 Argument.withShortName("q").description("suppress program output").build(),
 *                 Argument.withLongName("help").shortName("h")
 *                         .description("display this help message").build()
 *         );
 *
 *         System.out.println(ArgumentHelpGenerator
 *                 .builder(myArgs)
 *                 .header("Simple FTP Server")
 *                 .footer("\nThis project is licensed under the Apache 2.0 License")
 *                 .build()
 *         );
 * }</pre>
 * Produces:
 * <pre>
 * Simple FTP Server
 * Usage: program [option1] [option2 &lt;value&gt;]..
 * Where options:
 *     -p  --port   specify the server port
 *         --debug
 *     -q           suppress program output
 *     -h  --help   display this help message
 *
 * This project is licensed under the Apache 2.0 License
 * </pre>
 * Header and Footer are blank by default
 */
public final class ArgumentHelpGenerator {

    /**
     * Quickly generate a help message. Use the {@link Builder} for default values
     */
    public static String generate(String header, String usage, String footer, List<Argument> args) {
        return generateHelpForArgs(args, header, usage, footer);
    }

    /**
     * Vararg version of {@link #generate(String, String, String, List)}
     */
    public static String generate(String header, String usage, String footer, Argument... args) {
        return generateHelpForArgs(Arrays.asList(args), header, usage, footer);
    }

    /**
     * Builds a help message with specific components
     *
     * @param args the program arguments to be displayed
     * @return the builder instance
     */
    public static Builder builder(List<Argument> args) {
        return new Builder(args);
    }

    /**
     * Vararg version of {@link #builder(List)}
     */
    public static Builder builder(Argument... args) {
        return new Builder(Arrays.asList(args));
    }

    private static String generateHelpForArgs(List<Argument> args, String header, String usage, String footer) {
        String options = transformToString(args);
        List<String> stringsInOrder = Stream.of(header, usage, options, footer)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        StringJoiner joiner = new StringJoiner("\n");
        stringsInOrder.forEach(joiner::add);
        return joiner.toString();
    }

    private static String transformToString(List<Argument> args) {
        List<Integer> maxWidths = getMaxNameWidths(args);
        String maxShortNameFormat = String.format("%%-%ds", maxWidths.get(0) + 1); // + length of -
        String maxLongNameFormat = String.format("%%-%ds", maxWidths.get(1) + 2); // + length of --
        String tab = " ".repeat(4);
        StringBuilder builder = new StringBuilder();

        for (int i = 0, argsSize = args.size(); i < argsSize; i++) {

            Argument arg = args.get(i);
            String shortName = arg.getShortName();
            String longName = arg.getLongName();

            builder.append(tab);
            builder.append(String.format(maxShortNameFormat,
                    shortName.isEmpty() ? "" : "-" + shortName));
            builder.append("  ");
            builder.append(String.format(maxLongNameFormat,
                    longName.isEmpty() ? "" : "--" + longName));
            builder.append(tab);
            builder.append(arg.getDescription());
            if (i != argsSize - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static List<Integer> getMaxNameWidths(List<Argument> args) {

        List<Integer> result = new ArrayList<>();
        result.add(getMaxNameLength(args, Argument::getShortName));
        result.add(getMaxNameLength(args, Argument::getLongName));
        return result;
    }

    private static int getMaxNameLength(List<Argument> args, Function<Argument, String> nameSelector) {
        return args.stream()
                .filter((arg) -> !nameSelector.apply(arg).isEmpty())
                .mapToInt((arg) -> nameSelector.apply(arg).length())
                .max()
                .orElse(0);
    }

    /**
     * Help message builder
     */
    public static class Builder {

        private final List<Argument> args;
        private String header = "";
        private String usage = "Usage: program [option1] [option2 <value>]..\nWhere options:";
        private String footer = "";

        private Builder(List<Argument> args) {
            this.args = Collections.unmodifiableList(args);
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder usage(String usage) {
            this.usage = usage;
            return this;
        }

        public Builder footer(String footer) {
            this.footer = footer;
            return this;
        }

        public String build() {
            return ArgumentHelpGenerator.generateHelpForArgs(args, header, usage, footer);
        }
    }
}
