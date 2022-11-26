package org.numbers.statistics;

import java.util.concurrent.atomic.AtomicInteger;

public class StatisticsAccumulator {

    private StatisticsAccumulator(){}

    static final AtomicInteger unique = new AtomicInteger();
    static final AtomicInteger duplicate = new AtomicInteger();
    static final AtomicInteger total = new AtomicInteger();

    public static int consumeDuplicateCount() {
        return getAndReset(duplicate);
    }

    public static int consumeUniqueCount() {
        int uniqueCount = getAndReset(unique);
        total.getAndAdd(uniqueCount);
        return uniqueCount;
    }

    public static void incrementUnique(){
        unique.getAndIncrement();
    }

    public static void incrementDuplicate(){
        duplicate.getAndIncrement();
    }

    public static int getTotal() {
        return total.get();
    }

    private static int getAndReset(AtomicInteger count) {
        return count.getAndSet(0);
    }
}
