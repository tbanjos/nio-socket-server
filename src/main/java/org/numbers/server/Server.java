package org.numbers.server;

import org.numbers.exception.ApplicationException;
import org.numbers.exception.ExceptionHandler;
import org.numbers.message.MessageProcessor;
import org.numbers.message.MessageProducer;
import org.numbers.message.MessageValidator;
import org.numbers.message.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final MessageConsumer writer;
    private final MessageValidator validator;
    private final ServerSocketChannel server;
    private final Queue<MessageProducer> connectionQueue = new ArrayBlockingQueue<>(5);
    private boolean signalReceived;

    private static int idCounter;

    public Server(int port, MessageConsumer writer, MessageValidator validator) {
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
        var acceptanceTask = new Thread(new ClientHandler(this));
        acceptanceTask.setUncaughtExceptionHandler(new ExceptionHandler(this));

        var processingTask = new Thread(new MessageProcessor(connectionQueue, validator, writer, () -> true));
        processingTask.setUncaughtExceptionHandler(new ExceptionHandler(this));

        acceptanceTask.start();
        processingTask.start();
    }

    public void connect(SocketChannel channel) throws IOException {
        connectionQueue.removeIf(entry -> !entry.isOpen());
        if (channel != null) {
            channel.configureBlocking(false);
            var id = "Client #" + (idCounter++);
            ClientConnection clientConnection = new ClientConnection(channel, id);
            if (!connectionQueue.offer(clientConnection)) {
                clientConnection.close();
            }
        }
    }
}
