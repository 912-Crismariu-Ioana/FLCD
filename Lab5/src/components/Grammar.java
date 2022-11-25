package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private Set<String> nonTerminals = new HashSet<>();
    private Set<String> terminals = new HashSet<>();
    private String startingSymbol;
    private Set<Production> productions = new HashSet<>();

    public Grammar(String filename){
        readGrammarFromFile(filename);
    }

    private void readGrammarFromFile(String filename){
        File file = new File(filename);
        Scanner reader = null;
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

                    while(reader.hasNextLine()){
                        line = reader.nextLine().strip();

                        if(line.length() == 0){
                            break;
                        }

                        String[] tokens = line.split("->");
                        List<String> lhs = Arrays.asList(tokens[0].strip().split(" "));
                        Set<List<String>> rhs = Arrays.stream(tokens[1].strip().split("\\|")).map(
                                r -> {
                                    r = r.strip();
                                    return Arrays.asList(r.split(" "));
                                }
                        ).collect(Collectors.toSet());
                        Production production = new Production();
                        production.setLHS(lhs);
                        production.setRHS(rhs);
                        productions.add(production);
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

    public String printProductionsForNonTerminal(String nonTerminal){
        StringBuilder sb = new StringBuilder();
        productions.stream().filter(production -> production.isSymbolInLHS(nonTerminal))
                .forEach(production -> sb.append(production).append("\n"));
        return sb.toString();
    }

    public boolean isCFG(){
        if(!nonTerminals.contains(startingSymbol)){
            return false;
        }

        if(!productions.stream()
                .map(Production::getLHS)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).contains(startingSymbol)){
            return false;
        }

        for(Production production : productions){
            List<String> lhs = production.getLHS();
            Set<List<String>> rhs = production.getRHS();
            if(lhs.size() > 1)
                return false;
            else if(!nonTerminals.containsAll(lhs))
                return false;

            for(List<String> rh : rhs) {
                for (String symb : rh) {
                    if(!(nonTerminals.contains(symb) || terminals.contains(symb) || symb.equals("epsilon")))
                        return false;
                }
            }
        }
        return true;
    }

}