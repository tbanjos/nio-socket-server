package org.numbers.server;

import org.numbers.exception.TerminationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;
import java.util.Optional;

public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    private static String LOG_PREFIX;

    //TODO move to properties
    public static final String TERMINATION_SEQUENCE = "terminate";
    private final Socket socket;
    private final DataInputStream inputStream;

    private final int clientId;

    public Connection(Socket socket, int clientId) {
        LOG_PREFIX = "Client #".concat(String.valueOf(clientId)).concat(" | ");
        logger.debug(LOG_PREFIX + "Connected");
        this.clientId = clientId;
        this.socket = socket;
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        logger.debug(LOG_PREFIX + "Closing connection");
        if (isAlive()) {
            try {
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "Exception", e);
            }
        }
        logger.debug(LOG_PREFIX + "Closed");
    }

    public Optional<String> read() {
        logger.debug(LOG_PREFIX + "Waiting from input");
        if (isAlive()) {
            try {
                String line = inputStream.readLine();
                logger.debug(LOG_PREFIX + "Input received: " + line);
                if (Objects.equals(line, TERMINATION_SEQUENCE)) {
                    logger.info(LOG_PREFIX + "Termination sequence received");
                    throw new TerminationException();
                }
                return Optional.ofNullable(line);
            } catch (SocketException e) {
               logger.debug(LOG_PREFIX + "Connection closed while waiting for input");
            } catch (IOException e) {
               logger.error(LOG_PREFIX + "Exception when reading", e);
            }
        }
        return Optional.empty();
    }

    public boolean isAlive() {
        return !socket.isClosed();
    }
}
