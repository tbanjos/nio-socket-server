package org.numbers.file;

import java.io.IOException;
import java.util.logging.*;

public class FileWriter {
    private static final Logger fileLogger = Logger.getLogger("org.numbers.file.FileWriter");

    public FileWriter(String fileName) {
        FileHandler fh;
        try {
            fh = new FileHandler(fileName, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fh.setFormatter(getFormatter());
        fileLogger.setUseParentHandlers(false);
        fileLogger.addHandler(fh);
    }

    private static Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        };
    }

    public void write(String line) {
        fileLogger.log(Level.INFO, line);
    }
}
