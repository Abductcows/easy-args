# Java easy-args

### Fast and simple, if slightly opinionated, argument parsing library

Created as an alternative to [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/), this library ignores all unrecognized arguments, and will finish parsing every string before throwing an exception. This makes it easier to recover from a faulty argument should you wish to. It also provides a method generating a cookie-cutter help message, displaying all the arguments and a header/footer.

- [Example](#usage-example-for-a-supposed-server-program)
- [Documentation]
- [Release]

# The process

Argument processing consists of 3 phases:

- Declaring the expected arguments
- Parsing the program arguments
- Querying the result for present defined arguments

### Arguments

Arguments have short and/or long names, an optional value and a description (used in help messages). Short names are used with ```-``` (dash) and long ones with ```--``` (double dash). Same short and long name is possible, but not between different Arguments.

### The parser

The parser provides two methods
- parse arguments method
- get result method

The parse method returns the result but can throw a parse exception. If you can recover from that, the result will be available using the get result method. All exceptions are stored and thrown at the end of the parsing stage. 

### The result 

The result provides methods for querying whether an argument was suppplied and to get its value, if applicable. 

# Usage example for a supposed server program

## Argument declaration

```Java

var myArgs = List.of(
    Argument.withLongName("version").build(),
    Argument.withShortName("p").longName("port").needsValue().build(),
    Argument.withLongName("help").build()
);
```

## Argument parsing

```Java
var parser = new ArgumentParser();

try {
    parser.parseForMyArgs(args, myArgs);
} catch (ArgumentParseException e) {
    e.printStackTrace();
}

ArgumentParserResult result = parser.getParserResult();
```

## Result querying

### Version example

```Java
if (result.contains("version")) {

    System.out.println(PROGRAM_VERSION);
    System.exit(0);
}
```

### Port example

```Java
if (result.contains("port")) {

    try {
        serverPort = Integer.parseInt(
            result.getValue("port")
        );
    } catch (NoSuchElementException e) {
        // handle no port value
    } catch (NumberFormatException e) {
        // handle bad port
    }
}
```

### Help example with [ArgumentHelpGenerator](src/main/java/io/github/abductcows/easyargs/ArgumentHelpGenerator.java)
(using previous arguments with an added description for each)

```Java
if (result.contains("help")) {

    System.out.println(ArgumentHelpGenerator
            .builder(myArgs)
            .header("My server")
            .footer("\nVisit us on GitHub")
            .build()
    );
    System.exit(0);
}
```

Output for "program --help":

```
My server
Usage: program [option1] [option2 <value>]..
Where options:
        --version  print the program version an exit
    -p  --port     specify the server port
        --help     show this help message

Visit us on GitHub
```
