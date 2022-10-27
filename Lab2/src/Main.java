import components.ProgramScanner;
import components.SymbolTable;

public class Main {
    public static void main(String[] args) {
        ProgramScanner ps = new ProgramScanner("resources/p3.mj", "resources/PIF3.out", "resources/ST3.out");
        ps.scan();
    }
}
