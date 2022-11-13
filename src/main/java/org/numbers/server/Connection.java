package org.numbers.server;

import org.numbers.exception.TerminationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;

public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    private static String LOG_PREFIX;

    //TODO move to properties
    public static final String TERMINATION_SEQUENCE = "terminate";
    private final SocketChannel channel;

    //private final BufferedReader reader;

    public Connection(SocketChannel channel, int clientId) {
        //TODO add prefix to logger
        LOG_PREFIX = "Client #".concat(String.valueOf(clientId)).concat(" | ");
        logger.debug(LOG_PREFIX + "Connected");
        this.channel = channel;
    }

    public void close() {
        logger.debug(LOG_PREFIX + "Closing connection");
        if (isAlive()) {
            try {
                channel.close();
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "Exception", e);
            }
            logger.debug(LOG_PREFIX + "Closed");
        } else{
            logger.debug(LOG_PREFIX + "Already closed");
        }
    }

    public Optional<String> read() {
        logger.debug(LOG_PREFIX + "Waiting from input");
        if (isAlive()) {
            try {
                String line = readLine();
                checkTermination(line);
                return Optional.of(line);
            } catch (ClosedChannelException e) {
               logger.debug(LOG_PREFIX + "Connection closed while waiting for input");
            } catch (IOException e) {
               logger.error(LOG_PREFIX + "Exception when reading", e);
            }
        }
        return Optional.empty();
    }

    private void checkTermination(String line) throws IOException {
        if (Objects.equals(line, TERMINATION_SEQUENCE)) {
            channel.close();
            logger.info(LOG_PREFIX + "Termination sequence received");
            throw new TerminationException();
        }
    }

    private String readLine() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = channel.read(buffer);
        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        String line = new String(data).trim();
        logger.debug(LOG_PREFIX + "Input received: " + line);
        return line;
    }

    public boolean isAlive() {
        return channel.isConnected();
    }
}
