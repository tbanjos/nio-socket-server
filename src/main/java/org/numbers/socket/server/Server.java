package org.numbers.socket.server;

import org.numbers.file.FileWriter;
import org.numbers.socket.client.ClientConnection;
import org.numbers.socket.client.ClientTask;
import org.numbers.socket.exception.ApplicationException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server {

    private static final Logger applicationLogger = Logger.getLogger(Server.class.getName());
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
        while (shouldKeepRunning()) {
            try {
                Socket socket = server.accept();
                connectToClient(socket);
            } catch (IOException e) {
                throw new ApplicationException("Exception when waiting for connections");
            }

            //TODO Update statistics

            //TODO print statistics every 10 sec
        }
    }

    private void connectToClient(Socket socket) {
        //TODO Limit thread numbers
        ClientConnection connection = new ClientConnection(socket, connections.size());
        connections.add(connection);
        Thread t1 = new Thread(createTask(connection));
        t1.setUncaughtExceptionHandler(new ExceptionHandler(this));
        t1.start();
    }

    private static Runnable createTask(ClientConnection connection) {
        return new ClientTask(connection, fileWriter);
    }

    public void exit() {
        signalReceived = true;
        try {
            connections.forEach(ClientConnection::close);
            server.close();
        } catch (IOException e) {
            applicationLogger.warning("Exception when trying to close server");
        }
    }

    private boolean shouldKeepRunning() {
        return !signalReceived;
    }
}
