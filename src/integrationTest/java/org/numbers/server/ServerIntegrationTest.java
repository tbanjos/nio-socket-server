package org.numbers.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.numbers.file.NumberFileWriter;
import org.numbers.file.NumberValidator;

class ServerIntegrationTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        Server testServer = new Server(3333, new NumberFileWriter(), new NumberValidator());
        testServer.acceptConnections();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        testServer.exit();
    }
}