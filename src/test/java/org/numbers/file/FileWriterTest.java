package org.numbers.file;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileWriterTest {

    static final String TEST_FILE_NAME = "testFile.txt";
    static Path testFilePath = Paths.get(TEST_FILE_NAME);

    static FileWriter fileWriter = new FileWriter(TEST_FILE_NAME);

    @AfterAll
    static void tearDown(){
        try {
            Files.deleteIfExists(testFilePath);
        } catch (IOException ignored) {}
    }

    @Test
    void shouldAppendToFile() throws IOException {
        fileWriter.write("line1");
        fileWriter.write("line2");
        fileWriter.write("line3");

        List<String> strings = Files.readAllLines(testFilePath);
        assertEquals(strings.get(0), "line1");
        assertEquals(strings.get(1), "line2");
        assertEquals(strings.get(2), "line3");
    }

    @Test
    void shouldRecreateFile() throws IOException {
        Path file = Files.createFile(Paths.get("testTileToRecreate"));

        Files.delete(file);

        FileWriter fileWriter = new FileWriter("testTileToRecreate");

        fileWriter.write("recreated");

        String result = Files.readAllLines(file).get(0);

        assertEquals("recreated", result);

        Files.delete(file);
    }
}