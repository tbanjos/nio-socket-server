package org.numbers.server;

import org.numbers.client.ClientTask;
import org.numbers.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final List<Connection> connections = new ArrayList<>();
    private final ServerSocket server;
    private boolean signalReceived;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new ApplicationException("Can't create server");
        }
    }

    public void run() {
        try {
            while (serverIsAlive()) {
                Socket socket = server.accept();
                connect(socket);
            }
            server.close();
        } catch (IOException e) {
            logger.warn("Exception when waiting for connections", e);
        }

            //TODO Update statistics

            //TODO print statistics every 10 sec
    }

    private void connect(Socket socket) {
        //TODO Limit thread numbers
        if (serverIsAlive()) {
            Connection connection = new Connection(socket, connections.size());
            connections.add(connection);
            Thread t1 = new Thread(createTask(connection));
            t1.setUncaughtExceptionHandler(new ExceptionHandler(this));
            t1.start();
        }
    }

    private static Runnable createTask(Connection connection) {
        return new ClientTask(connection);
    }

    public void exit() {
        logger.info("Closing server ...");
        signalReceived = true;
        connections.forEach(Connection::close);
        System.exit(0);
    }

    private boolean serverIsAlive() {
        return !signalReceived;
    }
}
