package org.numbers.file;

import org.numbers.message.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberFileConsumer implements MessageConsumer {

    private static final String DEFAULT_LOG_NAME = "NUMBERS";

    private final Logger fileLogger;

    public NumberFileConsumer() {
        this(DEFAULT_LOG_NAME);
    }

    public NumberFileConsumer(String logName) {
        fileLogger = LoggerFactory.getLogger(logName);
    }

    public void write(String message) {
        fileLogger.info(message);
    }
}
