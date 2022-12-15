import components.grammar.Grammar;
import components.parser.LRZeroParser;
import components.utils.PIFTokenizer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("resources/g2.txt");
        System.out.println(grammar.nonTerminalsToString());
        System.out.println(grammar.terminalsToString());
        System.out.println(grammar.productionsToString());
        System.out.println("Productions for the non-terminal 'A':");
        System.out.println(grammar.getProductionsForNonTerminal("A"));
        if(grammar.isCFG()){
            System.out.println("The grammar is context-free.");
        }
        else{
            System.out.println("The grammar is not context-free.");
        }
        LRZeroParser lrZero = new LRZeroParser(grammar);
        lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF1.out"));
        lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF2.out"));
        lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF3.out"));
        lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF4.out"));
    }
}
