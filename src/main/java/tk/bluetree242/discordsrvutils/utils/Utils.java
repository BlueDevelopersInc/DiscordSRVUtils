package tk.bluetree242.discordsrvutils.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

    public static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String fileContents = new String(encoded, StandardCharsets.UTF_8);
        return fileContents;
    }
}
