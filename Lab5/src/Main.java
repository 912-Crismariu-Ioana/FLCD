import components.grammar.Grammar;
import components.parser.LRZeroParser;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("resources/g1.txt");
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
        lrZero.parse();
    }
}
