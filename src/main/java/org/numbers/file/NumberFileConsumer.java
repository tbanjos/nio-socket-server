package org.numbers.file;

import org.numbers.statistics.StatisticsAccumulator;
import org.numbers.message.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberFileConsumer implements MessageConsumer {

    private static final StringBuffer memoryBuffer = new StringBuffer();

    private static final String DEFAULT_LOG_NAME = "NUMBERS";

    private final Logger fileLogger;

    public NumberFileConsumer() {
        this(DEFAULT_LOG_NAME);
    }

    public NumberFileConsumer(String logName) {
        fileLogger = LoggerFactory.getLogger(logName);
    }

    public void write(String message) {
        if (isDuplicate(message)) {
            StatisticsAccumulator.incrementDuplicate();
        } else {
            StatisticsAccumulator.incrementUnique();
            memoryBuffer.append(message).append("\n");
            //TODO periodically write from buffer to file
            fileLogger.info(message);
        }
    }

    private static boolean isDuplicate(String message) {
        return memoryBuffer.indexOf(message) > -1;
    }
}
