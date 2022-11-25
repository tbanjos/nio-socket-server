package org.numbers.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.numbers.file.NumberFileWriter;
import org.numbers.file.NumberValidator;
import org.numbers.server.Connection;
import org.numbers.server.ConnectionProcessor;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionProcessorTest {
    @Mock
    Connection connection;

    @Mock
    NumberFileWriter numberFileWriter;

    @Mock
    NumberValidator validator;

    ConnectionProcessor connectionProcessor;

    ConnectionProcessorTest(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUp() {
        when(connection.isAlive()).thenReturn(true, true, false);
        connectionProcessor = new ConnectionProcessor(Map.of("test", connection), validator, numberFileWriter, () -> connection.isAlive());
    }

    @Test
    void shouldWriteValidValue() {
        String valueRead = "valueRead";
        when(connection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(anyString())).thenReturn(true);

        connectionProcessor.run();

        verify(numberFileWriter).write(valueRead);
    }

    @Test
    void shouldIgnoreEmptyValue() {
        when(connection.read()).thenReturn(Optional.empty());

        connectionProcessor.run();

        verify(validator, never()).isValid(anyString());
        verify(numberFileWriter, never()).write(anyString());
    }

    @Test
    void shouldCloseConnectionWhenInvalidValue() {
        String valueRead = "somethingNotValid";
        when(connection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(valueRead)).thenReturn(false);

        connectionProcessor.run();

        verify(numberFileWriter, never()).write(valueRead);
        verify(connection).close();
    }

    @Test
    void shouldPropagateException() {
        String exceptionMessage = "something went wrong";
        when(connection.read()).thenThrow(new RuntimeException(exceptionMessage));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> connectionProcessor.run());

        assertEquals(exceptionMessage, thrown.getMessage());
        verify(numberFileWriter, never()).write(anyString());
    }

    @Test
    void shouldNotReadWhenConnectionIsClosed() {
        when(connection.isAlive()).thenReturn( false);

        connectionProcessor.run();

        verify(connection, never()).read();
        verify(numberFileWriter, never()).write(anyString());
    }
}