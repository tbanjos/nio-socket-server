package org.numbers.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.numbers.file.NumberFileWriter;
import org.numbers.file.NumberValidator;
import org.numbers.message.MessageProcessor;
import org.numbers.message.MessageChannel;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {
    @Mock
    ClientConnection clientConnection;

    @Mock
    NumberFileWriter numberFileWriter;

    @Mock
    NumberValidator validator;

    MessageProcessor messageProcessor;

    MessageProcessorTest(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        when(clientConnection.isOpen()).thenReturn(true, true, false);
        var queue = new ArrayBlockingQueue<MessageChannel>(1);
        queue.add(clientConnection);
        messageProcessor = new MessageProcessor(queue, validator, numberFileWriter, () -> clientConnection.isOpen());
    }

    @Test
    void shouldWriteValidValue() {
        String valueRead = "valueRead";
        when(clientConnection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(anyString())).thenReturn(true);

        messageProcessor.run();

        verify(numberFileWriter).write(valueRead);
    }

    @Test
    void shouldIgnoreEmptyValue() {
        when(clientConnection.read()).thenReturn(Optional.empty());

        messageProcessor.run();

        verify(validator, never()).isValid(anyString());
        verify(numberFileWriter, never()).write(anyString());
    }

    @Test
    void shouldCloseConnectionWhenInvalidValue() {
        String valueRead = "somethingNotValid";
        when(clientConnection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(valueRead)).thenReturn(false);

        messageProcessor.run();

        verify(numberFileWriter, never()).write(valueRead);
        verify(clientConnection).close();
    }

    @Test
    void shouldPropagateException() {
        String exceptionMessage = "something went wrong";
        when(clientConnection.read()).thenThrow(new RuntimeException(exceptionMessage));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> messageProcessor.run());

        assertEquals(exceptionMessage, thrown.getMessage());
        verify(numberFileWriter, never()).write(anyString());
    }

    @Test
    void shouldNotReadWhenConnectionIsClosed() {
        when(clientConnection.isOpen()).thenReturn( false);

        messageProcessor.run();

        verify(clientConnection, never()).read();
        verify(numberFileWriter, never()).write(anyString());
    }
}