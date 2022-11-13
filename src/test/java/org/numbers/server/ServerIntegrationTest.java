package org.numbers.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.numbers.exception.TerminationException;

import java.io.IOException;

class ServerIntegrationTest {

    Server testServer = new Server(3333);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        testServer.acceptConnections();
    }

    @Test
    void exit() throws IOException {
        testServer.exit();
    }
}