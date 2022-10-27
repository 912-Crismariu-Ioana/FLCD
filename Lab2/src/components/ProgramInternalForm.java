package components;

import utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramInternalForm {

    private final List<Pair<String, Integer>> pif = new ArrayList<>();

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
