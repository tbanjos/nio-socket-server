package org.numbers.client;

import org.numbers.file.FileWriter;
import org.numbers.server.Connection;

public class ClientTask implements Runnable {

    private static final String DEFAULT_FILE_NAME = "numbers.log";

    private final FileWriter fileWriter;

    private final Connection connection;

    public ClientTask(Connection connection) {
        this (connection, new FileWriter(DEFAULT_FILE_NAME));
    }

    public ClientTask(Connection connection, FileWriter fileWriter) {
        this.connection = connection;
        this.fileWriter = fileWriter;
    }

    @Override
    public void run() {
        while (connection.isAlive()) {
            connection.read().ifPresent(fileWriter::write);

            //TODO validate input

            //TODO Deduplicate

        }
    }
}
