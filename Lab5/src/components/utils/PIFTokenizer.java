package components.utils;

import components.grammar.Production;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class PIFTokenizer {
    public static List<String> tokenizePIFFile(String pathToPIF){
        List<String> pif = new ArrayList<>();
        File file = new File(pathToPIF);
        Scanner reader;
        try {
            reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine().strip();
                line = line.substring(1, line.length() - 1);
                String[] tokens = line.split("->");
                pif.add(tokens[0].strip());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pif;
    }

}
