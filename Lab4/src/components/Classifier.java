package components;

import components.fa.FiniteAutomaton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *  Class used for testing whether a provided string is a reserved word, operator, separator, identifier or constant
 *  Predefined symbols (operators, separators, keywords) are loaded from an input file
 */
public class Classifier {

    private final String tokenFile;

    private final List<String> reservedWords = new ArrayList<>();

    private final List<String> operators = new ArrayList<>();

    private final List<String> separators = new ArrayList<>();

    private final FiniteAutomaton identifierFa = new FiniteAutomaton("resources/input/IdentifierFA.in");

    private final FiniteAutomaton intFA = new FiniteAutomaton("resources/input/IntFA.in");

    public Classifier(String tokenFile) {
        this.tokenFile = tokenFile;
    }


    /**
     * Parses the token input file and populates the lists corresponding to each type of symbol:
     * keyword, operator, separator
     * @throws FileNotFoundException if the path to the token file is invalid
     */
    public void readTokenFile() throws FileNotFoundException {
        File file = new File(tokenFile);
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine().strip();

            if(line.startsWith("@keywords")){

                while(reader.hasNextLine()){
                    line = reader.nextLine().strip();

                    if(line.length() == 0){
                        break;
                    }
                    reservedWords.add(line);
                }
            }

            if(line.startsWith("@operators")){

                while(reader.hasNextLine()){
                    line = reader.nextLine().strip();

                    if(line.length() == 0){
                        break;
                    }
                    operators.add(line);
                }
            }

            if(line.startsWith("@separators")){

                while(reader.hasNextLine()){
                    line = reader.nextLine().strip();

                    if(line.length() == 0){
                        break;
                    }
                    String separator = line;

                    if(line.equals("space")){
                        separator = " ";
                    }

                    if(line.equals("newline")){
                        separator = "\n";
                    }
                    separators.add(separator);
                }
            }
        }
        reader.close();
    }

    public boolean isReservedWord(String token) {
        return reservedWords.contains(token);
    }

    public boolean isOperator(String token) {
        return operators.contains(token);
    }

    public boolean isSeparator(String token) {
        return separators.contains(token);
    }

    public boolean isIdentifier(String token) {
        return identifierFa.isSequenceAccepted(token);
    }

    public boolean isConstant(String token) {
        String charRegex = "^'[a-zA-Z0-9_ ]?'$";
        String stringRegex = "^\"[a-zA-Z0-9_ ]*\"$";
        return intFA.isSequenceAccepted(token) || token.matches(charRegex) ||
                token.matches(stringRegex);
    }

}
