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

    //TODO move to properties
    public static final String TERMINATION_SEQUENCE = "terminate";
    private final Socket socket;
    private final DataInputStream inputStream;

    private final int clientId;

    public Connection(Socket socket, int clientId) {
        logger.debug("Connected to client #" + clientId);
        this.clientId = clientId;
        this.socket = socket;
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        logger.debug("Closing connection to client #" + clientId);
        if (isAlive()) {
            try {
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                logger.error("Exception when closing client #" + clientId, e);
            }
        }
        logger.debug("closed #" + clientId);
    }

    public Optional<String> read() {
        logger.info("Reading from client #" + clientId);
        if (isAlive()) {
            try {
                String line = inputStream.readLine();
                if (Objects.equals(line, TERMINATION_SEQUENCE)) {
                    logger.info("Termination sequence received from client #" + clientId);
                    throw new TerminationException();
                }
                return Optional.ofNullable(line);
            } catch (SocketException e) {
               logger.debug("Connection with #" + clientId + " closed while waiting for input");
            } catch (IOException e) {
               logger.error("Can't read from #" + clientId, e);
            }
        }
        return Optional.empty();
    }

    public boolean isAlive() {
        return !socket.isClosed();
    }
}
