package components;

import java.util.Arrays;
import java.util.List;

public class Classifier {

    private final List<String> reservedWords = Arrays.asList("my", "int", "char",
                                                        "string", "arr", "if", "else",
                                                        "print", "stdin", "while", "do");
    private final List<String> operators = Arrays.asList("+", "-", "*", "/", "%", "=",
                                                         "==", "!=", "<", "<=", ">=", ">");
    private final List<String> separators = Arrays.asList("(", ")", "[", "]", "{", "}", ";",
                                                          ":", " ", "\n", ",");

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
        String regExp = "^\\$[a-zA-Z0-9_]+$";
        return token.matches(regExp);
    }

    public boolean isConstant(String token) {
        String intRegEx = "^([+-]?[1-9]\\d*|0)$";
        String charRegex = "^'[a-zA-Z0-9_ ]?'$";
        String stringRegex = "^\"[a-zA-Z0-9_ ]*\"$";
        return token.matches(intRegEx) || token.matches(charRegex) ||
                token.matches(stringRegex);
    }

}
