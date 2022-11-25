package org.numbers.server;

import org.numbers.exception.ApplicationException;
import org.numbers.exception.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final MessageWriter writer;
    private final MessageValidator validator;
    private final ServerSocketChannel server;
    private final Queue<SocketChannel> connectionQueue = new ArrayBlockingQueue<>(5);
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private boolean signalReceived;

    public Server(int port, MessageWriter writer, MessageValidator validator) {
        this.writer = writer;
        this.validator = validator;
        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
        } catch (IOException e) {
            throw new ApplicationException("Can't create server", e);
        }
    }

    public void exit() {
        logger.info("Closing server ... ");
        signalReceived = true;
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

    public SocketChannel accept() throws IOException {
        return server.accept();
    }

    public void run() {
        var acceptanceTask = new Thread(new ConnectionAcceptor(this));
        acceptanceTask.setUncaughtExceptionHandler(new ExceptionHandler(this));

        var processingTask = new Thread(new ConnectionProcessor(connections, validator, writer, () -> true));
        processingTask.setUncaughtExceptionHandler(new ExceptionHandler(this));

        acceptanceTask.start();
        processingTask.start();
    }

    public void connect(SocketChannel channel) throws IOException {
        if (connectionQueue.offer(channel)) {
            if (channel != null) {
                try {
                    channel.configureBlocking(false);
                    var id = "Client #" + (connections.size() + 1);
                    Connection connection = new Connection(channel, id);
                    connections.put(id, connection);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            channel.close();
        }
    }
}
