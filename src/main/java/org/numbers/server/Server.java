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
    private static final int MAX_CONCURRENT_CLIENTS = 5;
    private final ServerSocketChannel server;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_CONCURRENT_CLIENTS);

    public Server(int port) {
        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
        } catch (IOException e) {
            throw new ApplicationException("Can't create server", e);
        }
    }

    public void run() {
        try {
          while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                executor.submit(new ClientHandler(clientSocket));
          }
       } catch (Exception e) {
            e.printStackTrace();
       }
   }
}
