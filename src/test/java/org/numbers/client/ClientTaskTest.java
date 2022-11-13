package org.numbers.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.numbers.file.FileWriter;
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

    @InjectMocks
    ClientTask clientTask;

    @BeforeEach
    void setUp(){
        when(connection.isAlive()).thenReturn(true, true, false);
    }

    @Test
    void shouldReadAndWriteValue() {
        String valueRead = "valueRead";
        when(connection.read()).thenReturn(Optional.of(valueRead));

        clientTask.run();

        verify(fileWriter, times(2)).write(valueRead);
    }

    @Test
    void shouldNotWriteEmptyValue() {
        when(connection.read()).thenReturn(Optional.empty());

        clientTask.run();

        verify(fileWriter, never()).write(anyString());
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