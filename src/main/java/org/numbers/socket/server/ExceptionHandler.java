package org.numbers.socket.server;

import org.numbers.socket.exception.TerminationException;

import java.util.logging.Logger;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger applicationLogger = Logger.getLogger(ExceptionHandler.class.getName());

    private final Server server;

    public ExceptionHandler(Server server){
        this.server = server;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof TerminationException){
            server.exit();
        } else {
            //TODO handle????
            applicationLogger.severe("Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
