package org.numbers.file;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterTest {

    static final String TEST_FILE_NAME = "test_file.txt";
    static Path testFilePath = Paths.get(TEST_FILE_NAME);

    static FileWriter fileWriter = new FileWriter("TEST");

    @AfterAll
    static void tearDown(){
        try {
            Files.deleteIfExists(testFilePath);
        } catch (IOException ignored) {}
    }

    @Test
    void shouldWriteToFile() throws IOException {
        fileWriter.write("line1");
        fileWriter.write("line2");
        fileWriter.write("line3");

        List<String> strings = Files.readAllLines(testFilePath);
        assertTrue(strings.contains("line1"));
        assertTrue(strings.contains("line2"));
        assertTrue(strings.contains("line3"));
    }

    @Test
    void shouldNotWriteToFileWhenUsingDifferentLog() throws IOException {
        Logger logger = LoggerFactory.getLogger(FileWriterTest.class);

        logger.info("somethingElse");

        List<String> strings = Files.readAllLines(testFilePath);
        assertFalse(strings.contains("somethingElse"));
    }
}