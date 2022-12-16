package components.parser.tree;

import components.grammar.Grammar;
import components.grammar.Production;

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

    public void writeToFile(){
    }

    public void printParseTree(){
        for(ParsingTreeRow parseTreeNode : parseTree){
            System.out.println(parseTreeNode);
        }
    }


}
