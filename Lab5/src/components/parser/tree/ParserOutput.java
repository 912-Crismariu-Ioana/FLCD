package components.parser.tree;

import components.grammar.Grammar;
import components.grammar.Production;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserOutput {
    private Grammar grammar;

    public List<ParsingTreeRow> getParseTree() {
        return parseTree;
    }

    private List<ParsingTreeRow> parseTree = new ArrayList<>();

    public ParserOutput(List<Integer> productionString, Grammar grammar){
        this.grammar = grammar;
        transforParserOutput(productionString);
    }

    public void transforParserOutput(List<Integer> productionString){
        int index = 0;
        int productionIndex = 0;
        List<Integer> nonTerminalsStack = new ArrayList<>();
        parseTree.add(new ParsingTreeRow(index, grammar.getStartingSymbol(), -1, -1));
        nonTerminalsStack.add(index);
        index++;
        while(!nonTerminalsStack.isEmpty()){
            int parentIndex = nonTerminalsStack.remove(nonTerminalsStack.size() - 1);
            int productionNumber = productionString.get(productionIndex++);
            Production production = grammar.getProductionByIndex(productionNumber);
            List<String> rhs  = production.getRHS().get(productionNumber);
            int leftSiblingIndex = 0;
            List<Integer> nonTerminals = new ArrayList<>();
            for(String rh : rhs){
                parseTree.add(new ParsingTreeRow(index, rh, parentIndex, leftSiblingIndex));
                leftSiblingIndex = index;
                if(grammar.getNonTerminals().contains(rh)){
                    nonTerminals.add(index);
                }
                index++;
            }
            List<Integer> newStack = new ArrayList<>();
            newStack.addAll(nonTerminalsStack);
            newStack.addAll(nonTerminals);
            nonTerminalsStack = newStack;
        }
    }

    public void writeToFile(File outputFile) {
        FileWriter outputWriter = null;
        try {
            outputWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String leftAlignFormatIndex = "| %-5s |";
        String leftAlignFormatInformation = "| %-20s |";
        String leftAlignFormatParent = "| %-6s |";
        String leftAlignFormatRightSibling = "| %-10s ";
        try {
            assert outputWriter != null;
            outputWriter.write(String.format(leftAlignFormatIndex, "Index"));
            outputWriter.write(String.format(leftAlignFormatInformation, "Information"));
            outputWriter.write(String.format(leftAlignFormatParent, "Parent"));
            outputWriter.write(String.format(leftAlignFormatRightSibling, "Right Sibling"));
            outputWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(ParsingTreeRow parseTreeNode : parseTree){
            try {
                outputWriter.write(parseTreeNode.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printParseTree(){
        for(ParsingTreeRow parseTreeNode : parseTree){
            System.out.println(parseTreeNode);
        }
    }
}
