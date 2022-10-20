import components.SymbolTable;

public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(13);
        symbolTable.add("ulll");
        symbolTable.remove("bbbbb");
        symbolTable.size();
        symbolTable.remove("ulll");
    }
}
