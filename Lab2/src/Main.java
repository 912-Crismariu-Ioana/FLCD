import components.SymbolTable;

public class Main {
    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable(13);
        symbolTable.add("i");
        symbolTable.add("2");
        symbolTable.printTable();
    }
}
