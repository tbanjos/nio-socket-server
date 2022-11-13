package org.numbers.server;

import org.numbers.exception.TerminationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    private BufferedReader reader;

    public Connection(Socket socket, int clientId) {
        //TODO add prefix to logger
        LOG_PREFIX = "Client #".concat(String.valueOf(clientId)).concat(" | ");
        logger.debug(LOG_PREFIX + "Connected");
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        logger.debug(LOG_PREFIX + "Closing connection");
        if (isAlive()) {
            try {
                reader.close();
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
                String line = reader.readLine();
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
