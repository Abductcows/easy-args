package io.github.abductcows.easyargs;

import io.github.abductcows.easyargs.arguments.Argument;
import io.github.abductcows.easyargs.parser.ArgumentParser;
import io.github.abductcows.easyargs.parser.BadArgumentUseException;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        var parser = new ArgumentParser();
        var fakeArgs = new String[]{"-u", "-i"};
        var myArgs = List.of(
                Argument.withShortName("u").build(),
                Argument.withShortName("i").needsValue().build()
        );
        try {
            parser.parseForMyArgs(fakeArgs, myArgs);
        } catch (BadArgumentUseException ignored)  {

        }

        var result = parser.getParseResult();
        System.out.println(result);
    }
}
