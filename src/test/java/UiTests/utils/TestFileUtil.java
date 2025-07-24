package UiTests.utils;
import java.io.*;

public class TestFileUtil {
    public static File writeTempFile(String name, String content) {
        try {
            File f = File.createTempFile(name, null);
            try (FileWriter w = new FileWriter(f)) {
                w.write(content);
            }
            f.deleteOnExit();
            return f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File writeLargeFile(String name, long bytes) throws IOException {
        File f = File.createTempFile(name, ".dat");
        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            raf.setLength(bytes); // быстро создаёт пустой файл нужного размера
        }
        f.deleteOnExit();
        return f;
    }
}