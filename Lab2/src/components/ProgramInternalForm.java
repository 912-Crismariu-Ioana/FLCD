package components;

import utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing the Program Internal Form
 * Takes care of a list of pairs containing a codified token
 * and its position/unique identifier within the Symbol Table
 */
public class ProgramInternalForm {

    private final List<Pair<String, Integer>> pif = new ArrayList<>();

    /**
     * Adds a new pair to the PIF
     * @param code codification of the token
     * @param position position/unique identifier in the Symbol Table
     */
    public void genPIF(String code, Integer position){
        pif.add(new Pair<>(code, position));
    }

    public void writeToFile(String pifPath) {
        try {
            File file = new File(pifPath);
            FileWriter writer = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(this.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return pif.stream().map(Pair::toString).collect(Collectors.joining("\n"));
    }

}
