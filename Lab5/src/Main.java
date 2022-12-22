import components.grammar.Grammar;
import components.parser.LRZeroParser;
import components.parser.tree.ParserOutput;
import components.utils.PIFTokenizer;

import java.io.File;
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
        //List<Integer> outputBand1 = lrZero.parserOutputBand(List.of("a", "c", "b", "c"));
        //System.out.println(outputBand1);


        List<Integer> outputBand1 = lrZero.parse(PIFTokenizer.tokenizePIFFile("resources/PIF1.out"));
        for (var element : outputBand1)
            System.out.print(element + " ");

        System.out.println();

        ParserOutput parserOutput1 = new ParserOutput(outputBand1, grammar);
        parserOutput1.printParseTree();

        String fileName = "output.out";
        File dir = new File ("resources");
        File actualFile = new File (dir, fileName);
        parserOutput1.writeToFile(actualFile);

        List<Integer> outputBand2 = lrZero.parse(PIFTokenizer.tokenizePIFFile("resources/PIF2.out"));
        for (var element : outputBand2)
            System.out.print(element + " ");

        System.out.println();

        ParserOutput parserOutput2 = new ParserOutput(outputBand2, grammar);
        parserOutput2.printParseTree();

        List<Integer> outputBand3 = lrZero.parse(PIFTokenizer.tokenizePIFFile("resources/PIF3.out"));
        for (var element : outputBand3)
            System.out.print(element + " ");

        System.out.println();

        ParserOutput parserOutput3 = new ParserOutput(outputBand3, grammar);
        parserOutput3.printParseTree();

        List<Integer> outputBand4 = lrZero.parse(PIFTokenizer.tokenizePIFFile("resources/PIF4.out"));
        for (var element : outputBand4)
            System.out.print(element + " ");

        System.out.println();

        ParserOutput parserOutput4 = new ParserOutput(outputBand4, grammar);
        parserOutput4.printParseTree();

//        List<ParsingTreeRow> parseTree = lrZero.parserParseTree(PIFTokenizer.tokenizePIFFile("resources/PIF1.out"));
//        for (var row : parseTree)
//        {
//            StringBuilder printRow = new StringBuilder();
//            printRow.append(row.getIndex()).append(": ").append(row.getInfo()).append(", ")
//                            .append(row.getParent()).append(", ").append(row.getRightSibling());
//            System.out.println(printRow);
//        }

        //lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF3.out"));
        //lrZero.parser(PIFTokenizer.tokenizePIFFile("resources/PIF4.out"));
    }
}
