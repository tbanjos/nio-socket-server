package org.numbers.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.numbers.file.FileWriter;
import org.numbers.file.InputValidator;
import org.numbers.server.Connection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientTaskTest {

    @Mock
    Connection connection;

    @Mock
    FileWriter fileWriter;

    @Mock
    InputValidator validator;

    @InjectMocks
    ClientTask clientTask;

    @BeforeEach
    void setUp(){
        when(connection.isAlive()).thenReturn(true, true, false);
    }

    @Test
    void shouldWriteValidValue() {
        String valueRead = "valueRead";
        when(connection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(anyString())).thenReturn(true);

        clientTask.run();

        verify(fileWriter, times(2)).write(valueRead);
    }

    @Test
    void shouldIgnoreEmptyValue() {
        when(connection.read()).thenReturn(Optional.empty());

        clientTask.run();

        verify(validator, never()).isValid(anyString());
        verify(fileWriter, never()).write(anyString());
    }

    @Test
    void shouldCloseConnectionWhenInvalidValue() {
        String valueRead = "somethingNotValid";
        when(connection.read()).thenReturn(Optional.of(valueRead));
        when(validator.isValid(valueRead)).thenReturn(false);

        clientTask.run();

        verify(fileWriter, never()).write(valueRead);
        verify(connection).close();
    }

    @Test
    void shouldPropagateException() {
        String exceptionMessage = "something went wrong";
        when(connection.read()).thenThrow(new RuntimeException(exceptionMessage));

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> clientTask.run());

        assertEquals(exceptionMessage, thrown.getMessage());
        verify(fileWriter, never()).write(anyString());
    }

    @Test
    void shouldNotReadWhenConnectionIsClosed() {
        when(connection.isAlive()).thenReturn( false);

        clientTask.run();

        verify(connection, never()).read();
        verify(fileWriter, never()).write(anyString());
    }
}