package org.numbers.server;

import java.util.Map;
import java.util.function.BooleanSupplier;

public class ConnectionProcessor implements Runnable {

    private final MessageWriter writer;
    private final MessageValidator validator;
    private final Map<String, Connection> connections;
    private final BooleanSupplier shouldProcess;

    public ConnectionProcessor(Map<String, Connection> connections, MessageValidator validator, MessageWriter writer, BooleanSupplier shouldProcess) {
        this.connections = connections;
        this.validator = validator;
        this.writer = writer;
        this.shouldProcess = shouldProcess;
    }

    @Override
    public void run() {
        while (shouldProcess.getAsBoolean()) {
            connections.forEach((key, connection) -> {
                if (connection.isAlive()) {
                    var input = connection.read();
                    if (input.isPresent()) {
                        var value = input.get();
                        if (validator.isValid(value)) {
                            writer.write(value);
                        } else {
                            connection.close();
                        }
                    }
                }
            });
        }
    }
}
