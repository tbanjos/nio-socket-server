package org.numbers.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWriter {

    private static final String DEFAULT_LOG_NAME = "numbersLog";

    private final Logger fileLogger;

    public FileWriter() {
        this(DEFAULT_LOG_NAME);
    }

    public FileWriter(String logName) {
        fileLogger = LoggerFactory.getLogger(logName);
    }

    public void write(String line) {
        fileLogger.info(line);
    }
}
