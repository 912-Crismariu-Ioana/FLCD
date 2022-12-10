import components.Grammar;
import components.Production;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar("resources/g2.txt");
        System.out.println(grammar.nonTerminalsToString());
        System.out.println(grammar.terminalsToString());
        System.out.println(grammar.productionsToString());
        System.out.println("Productions for the non-terminal 'constant':");
        System.out.println(grammar.getProductionsForNonTerminal("constant"));
        if(grammar.isCFG()){
            System.out.println("The grammar is context-free.");
        }
        else{
            System.out.println("The grammar is not context-free.");
        }
    }
}
