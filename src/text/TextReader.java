package text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextReader {


    public static List<String> readLines(String path) throws IOException { //Relative path
        List<String> lines = new ArrayList<>();

        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}
