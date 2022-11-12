package org.numbers;

import org.numbers.socket.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationRunner {

    private static final Logger applicationLogger = Logger.getLogger(ApplicationRunner.class.getName());        //TODO Application logger should be a bean

    private static final int DEFAULT_PORT = 4000;                                                               //TODO move to properties file

    public static void main(String[] args)
    {
        applicationLogger.setLevel(Level.WARNING);
        Server server = new Server(resolvePortNumber(args));
        server.run();
        System.exit(0);
    }

    private static int resolvePortNumber(String[] args) {
        try {
            if (args.length > 0) {
                return Integer.parseInt(args[0]);
            }
            logInfo("No port number provided, using default");
        } catch (NumberFormatException ex){
            logInfo("Invalid port argument provided, using default");
        }
        return DEFAULT_PORT;
    }

    private static void logInfo(String msg) {
        applicationLogger.info(msg);
    }
}
