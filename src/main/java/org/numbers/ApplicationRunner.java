package org.numbers;


import org.numbers.file.NumberFileWriter;
import org.numbers.file.NumberValidator;
import org.numbers.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    private static final int DEFAULT_PORT = 4000;   //TODO move to properties file

    public static void main(String[] args)
    {
        NumberFileWriter writer = new NumberFileWriter();
        NumberValidator validator = new NumberValidator();
        Server server = new Server(resolvePortNumber(args), writer, validator);
        server.run();
    }

    private static int resolvePortNumber(String[] args) {
        try {
            if (args.length > 0) {
                return Integer.parseInt(args[0]);
            }
            logger.info("No port number provided, using default");
        } catch (NumberFormatException ex){
            logger.info("Invalid port argument provided, using default");
        }
        return DEFAULT_PORT;
    }
}
