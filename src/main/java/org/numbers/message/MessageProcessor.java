package org.numbers.message;

import java.util.Queue;
import java.util.function.BooleanSupplier;

public class MessageProcessor implements Runnable {

    private final MessageConsumer writer;
    private final MessageValidator validator;
    private final Queue<MessageProducer> channels;
    private final BooleanSupplier shouldProcess;

    public MessageProcessor(Queue<MessageProducer> channels,
                            MessageValidator validator,
                            MessageConsumer writer,
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
