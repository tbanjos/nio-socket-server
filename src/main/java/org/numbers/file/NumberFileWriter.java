package org.numbers.file;

import org.numbers.server.MessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberFileWriter implements MessageWriter {

    private static final String DEFAULT_LOG_NAME = "NUMBERS";

    private final Logger fileLogger;

    public NumberFileWriter() {
        this(DEFAULT_LOG_NAME);
    }

    public NumberFileWriter(String logName) {
        fileLogger = LoggerFactory.getLogger(logName);
    }

    public void write(String message) {
        fileLogger.info(message);
    }
}
