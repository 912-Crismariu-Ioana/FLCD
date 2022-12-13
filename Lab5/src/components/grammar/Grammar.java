package components.grammar;

import components.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<String> nonTerminals = new HashSet<>();
    private final Set<String> terminals = new HashSet<>();
    private String startingSymbol;
    private final List<Production> productions = new ArrayList<>();

    public Grammar(String filename){
        readGrammarFromFile(filename);
    }

    private void readGrammarFromFile(String filename){
        File file = new File(filename);
        Scanner reader;
        try {
            reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine().strip();

                if(line.startsWith("@nonTerminals")){
                    line = reader.nextLine().strip();
                    nonTerminals.addAll(Arrays.asList(line.split(" ")));
                }

                if(line.startsWith("@terminals")){
                    line = reader.nextLine().strip();
                    terminals.addAll(Arrays.asList(line.split(" ")));
                }

                if(line.startsWith("@productions")){
                    int index = 1;
                    while(reader.hasNextLine()){
                        line = reader.nextLine().strip();

                        if(line.length() == 0){
                            break;
                        }

                        String[] tokens = line.split("->");
                        List<String> lhs = Arrays.asList(tokens[0].strip().split(" "));
                        List<List<String>>rhs = Arrays.stream(tokens[1].strip().split("\\|")).map(
                                r -> {
                                    r = r.strip();
                                    return Arrays.asList(r.split(" "));
                                }
                        ).toList();
                        Map<Integer, List<String>> rightHandSide = new HashMap<>();
                        for (List<String> rh : rhs) {
                            rightHandSide.put(index, rh);
                            index++;
                        }
                        Production production = new Production();
                        production.setLHS(lhs);
                        production.setRHS(rightHandSide);
                        if(!productions.contains(production)){
                            productions.add(production);
                        }
                    }
                }

                if(line.startsWith("@startingSymbol")){
                    startingSymbol = reader.nextLine().strip();
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String nonTerminalsToString() {
        StringBuilder sb = new StringBuilder("N = { \n");
        nonTerminals.forEach(nonTerminal->
            sb.append("\t").append(nonTerminal).append("\n"));
        sb.append("}");
        return sb.toString();
    }

    public String terminalsToString() {
        StringBuilder sb = new StringBuilder("E = { \n");
        terminals.forEach(terminal->
            sb.append("\t").append(terminal).append("\n"));
        sb.append("}");
        return sb.toString();
    }

    public String productionsToString() {
        StringBuilder sb = new StringBuilder("P: \n");
        productions.forEach(production -> {
            sb.append("\t").append(production.toString()).append("\n");
        });
        return sb.toString();
    }

    public Set<Production> getProductionsForNonTerminal(String nonTerminal){
        return productions.stream()
                .filter(production -> production.isSymbolInLHS(nonTerminal))
                .collect(Collectors.toSet());
    }

    /**
     * checks if the grammar loaded from the file is context free
     * @return true if the grammar is context free and false otherwise
     */
    public boolean isCFG(){
        // check if the starting symbol is in the set of non-terminals
        if(!nonTerminals.contains(startingSymbol)){
            return false;
        }

        // check if the starting symbol is the LHS of at least one of the productions
        if(!productions.stream()
                .map(Production::getLHS)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).contains(startingSymbol)){
            return false;
        }

        // we loop through each of the production
        for(Production production : productions){
            List<String> lhs = production.getLHS();
            Map<Integer, List<String>> rhs = production.getRHS();

            // LHS contains more than one non-terminal, the grammar is not context-free
            if(lhs.size() > 1)
                return false;

            // all symbols from the LHS of the productions are contained in the set of non-terminals
            else if(!nonTerminals.containsAll(lhs))
                return false;

            for(List<String> rh : rhs.values()) {
                for (String symb : rh) {

                    // the symbols from the RHS are either terminals, non-terminals or epsilon (the empty word)
                    if(!(nonTerminals.contains(symb) || terminals.contains(symb) || symb.equals("epsilon")))
                        return false;
                }
            }
        }
        return true;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public List<Production> getProductions() {
        return productions;
    }
}
