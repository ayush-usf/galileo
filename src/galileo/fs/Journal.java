
package galileo.fs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public final class Journal {

    private static final Journal instance = new Journal();

    private File journalFile;
    private FileReader reader;
    private BufferedReader bufferedReader;
    private FileWriter writer;
    private PrintWriter printWriter;


    public static Journal getInstance() {
        return instance;
    }

    public BufferedReader getReader() {
        return bufferedReader;
    }

    public void writeEntry(String entry)
    throws IOException {
        printWriter.println(entry);
        writer.flush();
    }

    public void close()
    throws IOException {
        printWriter.close();
    }

    public void setJournalPath(String path)
    throws IOException {
        journalFile = new File(path);

        writer = new FileWriter(journalFile, true);
        printWriter = new PrintWriter(writer);

        reader = new FileReader(journalFile);
        bufferedReader = new BufferedReader(reader);
    }
}
