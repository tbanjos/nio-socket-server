package org.numbers.client;

import org.numbers.file.FileWriter;
import org.numbers.file.InputValidator;
import org.numbers.server.Connection;

import java.util.Optional;

public class ClientTask implements Runnable {

    private static FileWriter fileWriter;

    private final Connection connection;

    private final InputValidator validator;

    public ClientTask(Connection connection, InputValidator validator, FileWriter fileWriter) {
        this.connection = connection;
        this.validator = validator;
        ClientTask.fileWriter = fileWriter;
    }

    @Override
    public void run() {
        while (connection.isAlive()) {
            Optional<String> input = connection.read();
            if (input.isPresent()){
                String value = input.get();
                if (validator.isValid(value)) {
                    fileWriter.write(value);
                } else {
                    connection.close();
                    break;
                }
            }
        }
    }
}
