package org.numbers.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class StatisticsReportTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsReportTask.class);

    @Override
    public void run() {
        logger.info("Received {} unique numbers, {} duplicates during last iteration. Unique global total: {}",
                StatisticsAccumulator.consumeUniqueCount(),
                StatisticsAccumulator.consumeDuplicateCount(),
                StatisticsAccumulator.getTotal());
    }
}
