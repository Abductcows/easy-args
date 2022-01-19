package io.github.abductcows.easyargs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used to quickly generate a standard help message for your program
 *
 * <p>
 * Use the {@link #builder builder} methods to create a help String
 * </p>
 * <p>
 * Format:
 * </p>
 * <ul>
 *     <li>Header</li>
 *     <li>Usage</li>
 *     <li>Options</li>
 *     <li>Footer</li>
 * </ul>
 * <p>
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
 * <p>Produces:</p>
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
 * <p>
 * Header and Footer are blank by default
 * </p>
 */
public final class ArgumentHelpGenerator {

    public static void main(String[] args) {

        List<Argument> myArgs = List.of(
                Argument.withLongName("port").shortName("p").needsValue()
                        .description("specify the server port").build(),
                Argument.withLongName("debug").build(),
                Argument.withShortName("q").description("suppress program output").build(),
                Argument.withLongName("help").shortName("h")
                        .description("display this help message").build()
        );

        System.out.println(ArgumentHelpGenerator
                .builder(myArgs)
                .header("Simple FTP Server")
                .footer("\nThis project is licensed under the Apache 2.0 License")
                .build()
        );
    }

    /**
     * Creates a new help message builder
     *
     * @param args the arguments to be included for the help message
     * @return the help message builder
     */
    public static Builder builder(List<Argument> args) {
        return new Builder(args);
    }

    /**
     * Vararg version of {@link #builder(List)}
     *
     * @param args the arguments to be included for the help message
     * @return the help message builder
     */
    public static Builder builder(Argument... args) {
        return new Builder(Arrays.asList(args));
    }

    /**
     * Generates a generically formatted help message using the arguments provided
     *
     * @param args   the arguments to be displayed
     * @param header the header of the message
     * @param usage  a short usage example for the program
     * @param footer the footer of the message
     * @return the help message
     */
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
        List<Integer> maxWidths = getMaxWidths(args); // get max arg name widths
        String maxShortNameFormat = String.format("%%-%ds", maxWidths.get(0));
        String maxLongNameFormat = String.format("%%-%ds", maxWidths.get(1));
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
            builder.append("  ");
            builder.append(arg.getDescription());
            if (i != argsSize - 1) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static List<Integer> getMaxWidths(List<Argument> args) {

        List<Integer> result = new ArrayList<>();
        // max short name width
        result.add(args.stream()
                .map(Argument::getShortName)
                .filter((name) -> !name.isEmpty())
                .mapToInt((name) -> name.length() + "-".length())
                .max()
                .orElse(0)
        );
        // max long name width
        result.add(args.stream()
                .map(Argument::getLongName)
                .filter((name) -> !name.isEmpty())
                .mapToInt((name) -> name.length() + "--".length())
                .max()
                .orElse(0)
        );
        return result;
    }

    /**
     * Builder class for a help message instance
     * <p>
     * Use the static factory method {@link #builder} for an instance
     * </p>
     * <p>
     * See {@link ArgumentHelpGenerator} for the general format of the help message
     */
    public static class Builder {

        private final List<Argument> args;
        private String header = "";
        private String usage = "Usage: program [option1] [option2 <value>]..\nWhere options:";
        private String footer = "";

        private Builder(List<Argument> args) {
            this.args = args;
        }

        /**
         * Set the header for the help message (default = empty String)
         *
         * @param header the new header
         * @return the builder instance
         */
        public Builder header(String header) {
            this.header = header;
            return this;
        }

        /**
         * Set the usage string for the program e.g. program [option1] [option2 &lt;value&gt;] etc
         *
         * @param usage usage example
         * @return the builder instance
         */
        public Builder usage(String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Set the help message footer (default = empty String)
         *
         * @param footer the new footer
         * @return the builder instance
         */
        public Builder footer(String footer) {
            this.footer = footer;
            return this;
        }

        /**
         * Build the help message
         *
         * @return the help message
         */
        public String build() {
            return ArgumentHelpGenerator.generateHelpForArgs(args, header, usage, footer);
        }
    }
}
