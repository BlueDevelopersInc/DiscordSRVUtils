package tk.bluetree242.discordsrvutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileWriter extends OutputStreamWriter {

    public FileWriter(File file, Charset charset) throws FileNotFoundException {
        super(new FileOutputStream(file), charset);
    }

    public FileWriter(File file) throws FileNotFoundException {
        super(new FileOutputStream(file), StandardCharsets.UTF_8);
    }
}
