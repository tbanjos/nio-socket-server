package org.numbers.client;

import org.numbers.server.Connection;
import org.numbers.server.MessageValidator;
import org.numbers.server.MessageWriter;

import java.util.Optional;

public class ClientTask implements Runnable {

    private static MessageWriter writer;

    private final Connection connection;

    private final MessageValidator validator;

    public ClientTask(Connection connection, MessageValidator validator, MessageWriter writer) {
        this.connection = connection;
        this.validator = validator;
        ClientTask.writer = writer;
    }

    @Override
    public void run() {
        while (connection.isAlive()) {
            Optional<String> input = connection.read();
            if (input.isPresent()){
                String value = input.get();
                if (validator.isValid(value)) {
                    writer.write(value);
                } else {
                    connection.close();
                    break;
                }
            }
        }
    }
}
