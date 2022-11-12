package org.numbers.socket.server;

import org.numbers.file.FileWriter;
import org.numbers.socket.client.ClientConnection;
import org.numbers.socket.client.ClientTask;
import org.numbers.socket.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final String DEFAULT_FILE_NAME = "numbers.log";
    private static final FileWriter fileWriter = new FileWriter(DEFAULT_FILE_NAME);
    private final List<ClientConnection> connections = new ArrayList<>();
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
                connectToClient(socket);
            }
            server.close();
        } catch (IOException e) {
            logger.warn("Exception when waiting for connections", e);
        }

            //TODO Update statistics

            //TODO print statistics every 10 sec
    }

    private void connectToClient(Socket socket) {
        //TODO Limit thread numbers
        if (serverIsAlive()) {
            ClientConnection connection = new ClientConnection(socket, connections.size());
            connections.add(connection);
            Thread t1 = new Thread(createTask(connection));
            t1.setUncaughtExceptionHandler(new ExceptionHandler(this));
            t1.start();
        }
    }

    private static Runnable createTask(ClientConnection connection) {
        return new ClientTask(connection, fileWriter);
    }

    public void exit() {
        logger.info("Closing server ...");
        signalReceived = true;
        connections.forEach(ClientConnection::close);
        System.exit(0);
    }

    private boolean serverIsAlive() {
        return !signalReceived;
    }
}
