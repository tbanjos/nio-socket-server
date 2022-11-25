package org.numbers.message;

import java.util.Queue;
import java.util.function.BooleanSupplier;

public class MessageProcessor implements Runnable {

    private final MessageWriter writer;
    private final MessageValidator validator;
    private final Queue<MessageChannel> channels;
    private final BooleanSupplier shouldProcess;

    public MessageProcessor(Queue<MessageChannel> channels,
                            MessageValidator validator,
                            MessageWriter writer,
                            BooleanSupplier shouldProcess) {
        this.channels = channels;
        this.validator = validator;
        this.writer = writer;
        this.shouldProcess = shouldProcess;
    }

    @Override
    public void run() {
        while (shouldProcess.getAsBoolean()) {
            channels.forEach(channel -> {
                if (channel.isOpen()) {
                    var input = channel.read();
                    if (input.isPresent()) {
                        var value = input.get();
                        if (validator.isValid(value)) {
                            writer.write(value);
                        } else {
                            channel.close();
                        }
                    }
                }
            });
        }
    }
}
