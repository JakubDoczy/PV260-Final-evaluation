import jdk.internal.util.xml.impl.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Global options of the program.
 *
 * TODO: split this class into parser and data holder
 */
public class ProgramOptions {

    private final static Logger log = LoggerFactory.getLogger(ProgramOptions.class);

    private enum CommandLineOptions {
        DATASET_LOCATION("-d"),
        MANIPULATION_METHODS("-m"),
        OUTPUT_TYPE("-o");

        private CommandLineOptions(String str) {
            this.str = str;
        }

        public String str;
    }

    private int parsedIndex;
    private String[] args;

    private String outputPath;
    private String inputPath;
    private List<String> manipulationMethods;
    private OutputType outputType;

    public ProgramOptions(String[] commandLineArgs) {
        parsedIndex = 0;
        args = commandLineArgs;
        manipulationMethods = new ArrayList<>();
        parseArgs();
    }

    private Map<String, Runnable> createParseMap() {
        Map<String, Runnable> parseMap = new HashMap<>();
        parseMap.put(CommandLineOptions.DATASET_LOCATION.str, new Runnable() {
            @Override
            public void run() {
                log.debug("Input URL: " + args[parsedIndex]);
                inputPath = args[parsedIndex];
                parsedIndex++;
            }
        });
        parseMap.put(CommandLineOptions.MANIPULATION_METHODS.str, new Runnable() {
            @Override
            public void run() {
                log.debug("Parsing manipulation methods from index " + parsedIndex);
                parseManipulationMethods();
            }
        });
        parseMap.put(CommandLineOptions.OUTPUT_TYPE.str, new Runnable() {
            @Override
            public void run() {
                outputType = OutputType.parse(args[parsedIndex++]);
                log.debug("Output type: " + outputType.str);
                outputPath = args[parsedIndex++];
                log.debug("Output path: " + outputPath);
            }
        });

        return parseMap;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    public List<String> getManipulationMethods() {
        return manipulationMethods;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    private void parseManipulationMethods() {
        if (args[parsedIndex].isEmpty() || args[parsedIndex].startsWith("-")) {
            log.error("Found no data manipulation methods.");
            throw new RuntimeException("No manipulation methods selected.");
        }

        while (parsedIndex < args.length && !args[parsedIndex].startsWith("-")) {
            manipulationMethods.add(args[parsedIndex]);
            parsedIndex++;
        }
    }

    private void parseArgs() {
        Map<String, Runnable> parseMap = createParseMap();
        log.debug("Parsing arguments " + Arrays.toString(args));

        while (args.length > parsedIndex) {
            log.debug("Parsing argument " + args[parsedIndex]);
            if (parseMap.containsKey(args[parsedIndex])) {
                parseMap.get(args[parsedIndex++]).run();
            } else {
                log.error("Failed to parse argument " + args[parsedIndex] + ", Expected one of options.");
                throw new RuntimeException("Unrecognised option: " + args[parsedIndex]);
            }
        }

        log.debug("Successfully parsed all arguments.");
    }

}
