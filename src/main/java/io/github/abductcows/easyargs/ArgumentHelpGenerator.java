package io.github.abductcows.easyargs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class ArgumentHelpGenerator {

    public static void main(String[] args) {

        var myArgs = List.of(
                Argument.withLongName("port").shortName("p").needsValue().description("the server port").build(),
                Argument.withLongName("help").shortName("h").description("display this help message").build(),
                Argument.withLongName("verbose").build(),
                Argument.withShortName("u").build()
        );

        System.out.println(ArgumentHelpGenerator.builder(myArgs)
                        .header("Hello World")
                        .footer("sad")
                .build()
        );
        System.out.println("life continues");
    }

    public static Builder builder(List<Argument> args) {
        return new Builder(args);
    }

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
        return (header.isEmpty() ? "" : header + "\n") +
                (usage.isEmpty() ? "" : usage + "\n") +
                (options.isEmpty() ? "" : footer.isEmpty()? options : options + "\n") +
                footer;
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

    public static class Builder {

        private final List<Argument> args;
        private String header = "";
        private String usage = "Usage: program [options]\nWhere options:";
        private String footer = "";

        public Builder(List<Argument> args) {
            this.args = args;
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
