package org.numbers.message;

import java.util.Optional;

public interface MessageProducer {
    boolean isOpen();

    Optional<String> read();

    void close();
}
