import components.ProgramScanner;
import components.SymbolTable;

public class Main {
    public static void main(String[] args) {
        ProgramScanner ps1 = new ProgramScanner("resources/input/p1.mj",
                "resources/output/PIF1.out",
                "resources/output/ST1.out");
        ps1.scan();
        ProgramScanner ps2 = new ProgramScanner("resources/input/p2.mj",
                "resources/output/PIF2.out",
                "resources/output/ST2.out");
        ps2.scan();
        ProgramScanner ps3 = new ProgramScanner("resources/input/p3.mj",
                "resources/output/PIF3.out",
                "resources/output/ST3.out");
        ps3.scan();
        ProgramScanner psErr = new ProgramScanner("resources/input/p1err.mj",
                "resources/output/PIF1err.out",
                "resources/output/ST1err.out");
        psErr.scan();
    }
}
