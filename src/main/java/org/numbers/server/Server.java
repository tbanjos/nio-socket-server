package org.numbers.server;

import org.numbers.client.ClientTask;
import org.numbers.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final MessageWriter writer;
    private final MessageValidator validator;
    private final List<Connection> connections = new ArrayList<>();
    private final ServerSocketChannel server;
    private boolean signalReceived;

    public Server(int port, MessageWriter writer, MessageValidator validator) {
        this.validator = validator;
        try {
            this.writer = writer;
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            //TODO recreate file
        } catch (IOException e) {
            throw new ApplicationException("Can't create server", e);
        }
    }

    public void acceptConnections() {
        try {
            connect(server.accept());
        } catch (ClosedChannelException e) {
            logger.debug("Exception trying to connect to closed server");
        } catch (IOException e) {
            logger.warn("Exception when waiting for connections", e);
        }

        //TODO Update statistics

        //TODO print statistics every 10 sec
    }

    private void connect(SocketChannel channel) {
        //TODO Limit thread numbers
        if (isAlive()) {
            Connection connection = new Connection(channel, connections.size());
            connections.add(connection);
            Thread t1 = new Thread(createTask(connection));
            t1.setUncaughtExceptionHandler(new ExceptionHandler(this));
            t1.start();
        }
    }

    private Runnable createTask(Connection connection) {
        return new ClientTask(connection, validator, writer);
    }

    public void exit() {
        logger.info("Closing server ... ");
        signalReceived = true;
        connections.forEach(Connection::close);
        try {
            server.close();
        } catch (IOException e) {
            logger.warn("Exception when closing the server");
        }
        System.exit(0);
    }

    public boolean isAlive() {
        return !signalReceived;
    }
}
