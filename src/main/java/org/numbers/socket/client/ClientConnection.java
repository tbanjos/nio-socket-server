package org.numbers.socket.client;

import org.numbers.socket.exception.ClientConnectionException;
import org.numbers.socket.exception.TerminationException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class ClientConnection {

    private static final Logger applicationLogger = Logger.getLogger(ClientConnection.class.getName());

    //TODO move to properties
    public static final String TERMINATION_SEQUENCE = "terminate";
    private final Socket socket;

    private DataInputStream in = null;

    private final int clientId;

    private boolean alive = true;

    public ClientConnection(Socket socket, int clientId) {
        applicationLogger.info("Connected to client #" + clientId);
        this.clientId = clientId;
        this.socket = socket;
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        applicationLogger.info("Closing connection to client #" + clientId);
        if (alive) {
            try {
                alive = false;
                in.close();
                socket.close();
            } catch (IOException e) {
                //TODO handle ???
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<String> readLine() throws ClientConnectionException {
        if (isAlive()) {
            try {
                String line = in.readLine();
                if (Objects.equals(line, TERMINATION_SEQUENCE)) {
                    throw new TerminationException();
                }
                return Optional.of(line);
            } catch (IOException e) {
                applicationLogger.info("Can't read from #" + clientId);
            }
        }
        return Optional.empty();
    }

    public boolean isAlive() {
        return alive;
    }
}
