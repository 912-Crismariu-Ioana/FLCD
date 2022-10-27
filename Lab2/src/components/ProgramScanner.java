package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProgramScanner {

    private final SymbolTable symbolTable = new SymbolTable(26);

    private final Classifier classifier = new Classifier();

    private final ProgramInternalForm pif = new ProgramInternalForm();

    private final String programPath;

    private final String PIFPath;

    private final String STPath;

    public ProgramScanner(String programPath, String PIFPath, String STPath) {
        this.programPath = programPath;
        this.PIFPath = PIFPath;
        this.STPath = STPath;
    }

    private String codify(String token){
        if(classifier.isIdentifier(token)){
            return "id";
        }
        if(classifier.isConstant(token)){
            return "const";
        }
        return token;
    }

    public void scan() {
        try {
            File file = new File(programPath);
            Scanner reader = new Scanner(file);
            int lineNr = 1;
            boolean lexicalError = false;
            while (reader.hasNextLine() && !lexicalError) {
                String line = reader.nextLine();
                if(line.length() == 0 || line.charAt(0) == '#'){
                    ++lineNr;
                    continue;
                }
                List<String> tokens = tokenize(line);
                for(String token: tokens){
                    if(!classify(token)){
                        System.out.format("Lexical error detected on line %d: Unidentifiable token %s", lineNr, token);
                        lexicalError = true;
                        break;
                    }
                }
                ++lineNr;
            }

            if(!lexicalError){
                System.out.println("Program is lexically correct");
            }
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        symbolTable.writeToFile(STPath);
        pif.writeToFile(PIFPath);
    }

    private boolean classify(String token) {
        if(classifier.isReservedWord(token) || classifier.isOperator(token) || classifier.isSeparator(token)){
            pif.genPIF(token, 0);
            return true;
        }
        if(classifier.isIdentifier(token) || classifier.isConstant(token)){
            int index = symbolTable.position(token);
            if(index == -1){
                symbolTable.add(token);
                index = symbolTable.position(token);
            }
            pif.genPIF(codify(token), index);
            return true;
        }
        return false;
    }

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        String result = null;
        int i = 0;
        while(i < line.length()){
            char character = line.charAt(i);
            if(character == ' ') {
                i++;
                continue;
            }
            if (classifier.isSeparator(String.valueOf(character))) {
                result = String.valueOf(character);
            } else if(character == '\''){
                // extract character constant
                result = extractCharConst(line.substring(i));
            } else if(character == '\"'){
                // extract string constant
                result = extractStringConst(line.substring(i));
            } else if(character == '+' || character == '-'){
                // extract operator or integer constant
                if(classifier.isIdentifier(tokens.get(tokens.size() - 1))
                        || classifier.isConstant(tokens.get(tokens.size() - 1))){
                    result = extractOperator(line.substring(i));
                } else {
                    result = character + extractIntegerConst(line.substring(i));
                }
            } else if(character == '!' || classifier.isOperator(String.valueOf(character))){
                // extract operator
                result = extractOperator(line.substring(i));
            } else {
                // extract token
                result  = extractToken(line.substring(i));
            }
            tokens.add(result);
            i += result.length();
        }
        return tokens;
    }

    private String extractToken(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            if(classifier.isOperator(String.valueOf(character)) ||
                    (classifier.isSeparator(String.valueOf(character))) ||
                    character == '\'' ||
                    character == '"' ||
                    character == '!'){
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    private String extractIntegerConst(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            if(!Character.isDigit(character)){
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    private String extractOperator(String line) {
        if(classifier.isOperator(line.substring(0,2))){
            return line.substring(0,2);
        }
        return line.substring(0,1);
    }

    private String extractStringConst(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            if(classifier.isOperator(String.valueOf(character)) ||
                    (classifier.isSeparator(String.valueOf(character)) && character != ' ') ||
                 character == '\'' ||
                 character == '!'){
                break;
            }
            result.append(character);
            if(i != 0 && character == '"'){
                break;
            }
        }
        return result.toString();
    }

    private String extractCharConst(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            if(classifier.isOperator(String.valueOf(character)) ||
                    (classifier.isSeparator(String.valueOf(character)) && character != ' ') ||
                    character == '\"' ||
                    character == '!'){
                break;
            }
            result.append(character);
            if(i != 0 && character == '\''){
                break;
            }
        }
        return result.toString();
    }

}
