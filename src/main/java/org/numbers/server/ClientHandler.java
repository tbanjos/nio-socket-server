package org.numbers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final Server server;

    public ClientHandler(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (server.isAlive()) {
            try {
                var channel = server.accept();
                if (channel != null) {
                    server.connect(channel);
                }
            } catch (Exception e) {
                logger.warn("Exception when waiting for connections", e);
            }
        }

        //TODO Update statistics

        //TODO print statistics every 10 sec
    }
}
