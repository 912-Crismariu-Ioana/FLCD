package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class used in lexical analysis. Implements the scanning algorithm
 */
public class ProgramScanner {

    private final SymbolTable symbolTable = new SymbolTable(26);

    private final Classifier classifier = new Classifier("resources/input/token.in");

    private final ProgramInternalForm pif = new ProgramInternalForm();

    // Path to the input (program) file
    private final String programPath;

    // Path to the output file for Program Internal Form data
    private final String PIFPath;

    // Path to the output file for Symbol Table data
    private final String STPath;

    public ProgramScanner(String programPath, String PIFPath, String STPath) {
        this.programPath = programPath;
        this.PIFPath = PIFPath;
        this.STPath = STPath;
    }

    /**
     * Encodes tokens that correspond to valid identifiers or constants
     * @param token token to be codified
     * @return if the token is a valid identifier or constant, it returns the proper encoding.
     *   Otherwise, it returns the token
     */
    private String codify(String token){
        if(classifier.isIdentifier(token)){
            return "id";
        }
        if(classifier.isConstant(token)){
            return "const";
        }
        return token;
    }

    /**
     * Function implementing the scanning algorithm
     * The input file is parsed and tokenized, and afterwards lexical analysis is performed.
     * If the input program contains lexical errors, the scanner is stopped and a message containing
     * the line number and the symbol that caused the error is displayed
     * The generated Symbol Table and Program Internal Form are written to the output files
     */
    public void scan() {
        try {
            classifier.readTokenFile();
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
            reader.close();

            if(!lexicalError){
                System.out.println("Program is lexically correct");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        symbolTable.writeToFile(STPath);
        pif.writeToFile(PIFPath);
    }

    /**
     * Function that attempts to classify a provided token
     * and update the Symbol Table and Program Internal Form accordingly
     * @param token token to be classified
     * @return true if the token can be classified, false otherwise
     */
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

    /**
     * Utility function for splitting a given line into tokens
     * @param line line from the input file
     * @return list of tokens
     */
    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        String result = null;
        int i = 0;
        while(i < line.length()){
            char character = line.charAt(i);
            if(character == ' ') {
                // Skip empty characters
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

    /**
     * Extract a yet unidentified token from a line of the input file
     * @param line substring of a line from the input file beginning with the position
     *             from which we want to start extracting the token
     * @return the token
     */
    private String extractToken(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            // Stop upon encountering operators, separators, markers of string and char constants
            // and the exclamation sign (part of the inequality operator)
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

    /**
     * Extract an uninterrupted string of digits from a line of the input file
     * @param line substring of a line from the input file beginning with the position
     *             from which we want to start extracting the integer constant
     * @return the token
     */
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

    /**
     * Extract an operator from a line of the input file
     * @param line substring of a line from the input file beginning with the position
     *             from which we want to start extracting the operator
     * @return the token
     */
    private String extractOperator(String line) {
        // Test if two-character operator
        if(classifier.isOperator(line.substring(0,2))){
            return line.substring(0,2);
        }
        //Single-character operator
        return line.substring(0,1);
    }

    /**
     * Extract a token representing an unvalidated string constant from a line of the input file
     * @param line substring of a line from the input file beginning with the position
     *             from which we want to start extracting the token
     * @return the token
     */
    private String extractStringConst(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            // Stop upon encountering operators, separators that are not empty characters (allowed in string constants),
            // markers of char constants and the exclamation sign (part of the inequality operator)
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

    /**
     * Extract a token representing an unvalidated character constant from a line of the input file
     * @param line substring of a line from the input file beginning with the position
     *             from which we want to start extracting the token
     * @return the token
     */
    private String extractCharConst(String line) {
        StringBuilder result = new StringBuilder();
        char character;
        for(int i = 0; i < line.length();i++){
            character = line.charAt(i);
            // Stop upon encountering operators, separators that are not empty characters (allowed in string constants),
            // markers of string constants and the exclamation sign (part of the inequality operator)
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
