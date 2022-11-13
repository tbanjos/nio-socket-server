package org.numbers.server;

import org.numbers.exception.TerminationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    private final Server server;

    public ExceptionHandler(Server server){
        this.server = server;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof TerminationException){
            server.exit();
        } else {
            logger.error("Unhandled exception", e);
        }
    }
}
