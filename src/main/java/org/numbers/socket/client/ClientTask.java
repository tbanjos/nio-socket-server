package org.numbers.socket.client;

import org.numbers.file.FileWriter;

public class ClientTask implements Runnable{

    private final ClientConnection client;
    private final FileWriter fileWriter;


    public ClientTask(ClientConnection client, FileWriter fileWriter){
        this.client = client;
        this.fileWriter = fileWriter;;
    }

    @Override
    public void run() {
        while (client.isAlive()) {
            client.readLine().ifPresent(fileWriter::write);

            //TODO validate input

            //TODO Deduplicate

        }
    }
}
