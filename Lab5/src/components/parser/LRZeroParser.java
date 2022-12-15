package components.parser;

import components.grammar.Grammar;
import components.grammar.Production;
import components.utils.Pair;
import components.parser.table.Table;
import components.parser.table.TableRow;

import java.util.*;
import java.util.stream.Collectors;

public class LRZeroParser {
    private final Grammar grammar;
    private final List<Production> enrichedProductions = new ArrayList<>();

    public LRZeroParser(Grammar grammar) {
        this.grammar = grammar;
        List<Production> productions = grammar.getProductions();

        Production enrichedGrammarProduction = new Production();
        List<String> lhs = List.of("S'");
        Map<Integer, List<String>> rhs = new HashMap<>();
        rhs.put(-1, Collections.singletonList(grammar.getStartingSymbol()));

        enrichedGrammarProduction.setLHS(lhs);
        enrichedGrammarProduction.setRHS(rhs);

        // we add the first element to the new productions (S' -> first symbol)
        this.enrichedProductions.add(enrichedGrammarProduction);

        // we add the rest of the productions to the new productions list
        this.enrichedProductions.addAll(productions);
    }


    private State closure(Item item){
        Set<Item> closure = new HashSet<>();
        closure.add(item);

        boolean changed = true;

        do {
            changed = false;
            Set<Item> oldClosure = new HashSet<>(closure);
            for (Item it : oldClosure) {
                String symbol = it.getSymbolAfterTheDot();
                if(!grammar.getNonTerminals().contains(symbol)){
                    continue;
                }
                for (Production production : grammar.getProductionsForNonTerminal(symbol)) {
                    for(var rhs : production.getRHS().entrySet()){
                        Item currentItem = new Item(symbol, rhs.getValue(), 0, rhs.getKey());
                        if(closure.add(currentItem)){
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        return new State(closure);
    }

    private State goTo(State state, String symbol) {
        Set<Item> result = new HashSet<>();
        for (Item item :state.getItems()) {
            String nonTerminal = item.getSymbolAfterTheDot();
            if (symbol.equals(nonTerminal)) {
                Item newItem = new Item(item.getLhs(), item.getRhs(), item.getDotPosition() + 1, item.getIndex());
                result.addAll(closure(newItem).getItems());
            }
        }
        return new State(result);
    }

    private List<State> getCanonicalCollection(){
        int index = 0;
        List<State> canonicalCollection = new ArrayList<>();
        Item firstItem = new Item("S'",
                List.of(grammar.getStartingSymbol()),
                0,
                index);
        State firstState = closure(firstItem);
        firstState.setIndex(index);
        canonicalCollection.add(firstState);
        index++;
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());
        boolean changed = true;
        while(changed) {
            changed = false;
            List<State> states = new ArrayList<>(canonicalCollection);
            for (State state : states) {
                for (String symbol : symbols) {
                    State newState = goTo(state, symbol);
                    if (newState.getItems().size() > 0) {
                        if (!canonicalCollection.contains(newState)) {
                            newState.setIndex(index);
                            canonicalCollection.add(newState);
                            index++;
                            changed = true;
                        } else {
                            int stateIndex = canonicalCollection.indexOf(newState);
                            state.addReachableState(symbol, stateIndex);
                        }
                    }
                }
            }
        }
        return canonicalCollection;
    }

    public Table getParsingTable(){
        List<State> canonicalCollection = getCanonicalCollection();
        System.out.println(canonicalCollection);
        List<TableRow> table = new ArrayList<>();
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());
        for (State state : canonicalCollection) {
            ActionType actionType = state.getNextAction();
            Map<String, Integer> goTo = new HashMap<>();
            symbols.forEach(symbol -> goTo.put(symbol, -1));
            goTo.putAll(state.getReachableStates());
            TableRow tableRow = new TableRow(actionType, goTo);
            tableRow.setStateIndex(state.getIndex());
            if(actionType.equals(ActionType.REDUCE)){
                List<Item> triggerItemsReduce = state.getItems().stream().filter(item -> item.getDotPosition() == item.getRhs().size()).collect(Collectors.toList());
                if(triggerItemsReduce.size() > 0){
                    Item toReduce = triggerItemsReduce.stream().min(Comparator.comparingInt(Item::getIndex)).get();
                    for(Production production: grammar.getProductions()){
                        if(production.getLHS().get(0).equals(toReduce.getLhs())){
                            for(var prodRhs: production.getRHS().entrySet()){
                                if(prodRhs.getValue().equals(toReduce.getRhs())){
                                    tableRow.setProductionIndexInList(prodRhs.getKey());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            table.add(tableRow);
        }
        return new Table(symbols, table);
    }

    public void parse() {
        System.out.println(getParsingTable());
    }

    public List<Integer> parser(List<String> pif) {
        parse();
        List<Pair<String, Integer>> workingStack = new ArrayList<>();
        List<String> inputStack = new ArrayList<>(pif);
        List<Integer> outputBand = new ArrayList<>();
        Table parsingTable = getParsingTable();
        workingStack.add(new Pair<>("$", 0));
        while (!inputStack.isEmpty() || !workingStack.isEmpty()) {
            Integer lastItemInWS = workingStack.get(workingStack.size() - 1).second;
            if (lastItemInWS == null || lastItemInWS < 0 || lastItemInWS >= parsingTable.getTableRows().size()) {
                throw new RuntimeException("Invalid last element in working stack!!!");
            }
            TableRow tableRow = parsingTable.getTableRows().get(lastItemInWS);
            switch (tableRow.getAction()) {
                case SHIFT:
                    if (inputStack.isEmpty()) {
                        throw new RuntimeException("Action is shift but nothing else is left in the remaining stack");
                    }
                    String token = inputStack.get(0);
                    Map<String, Integer> goTo = tableRow.getGoTo();
                    if (!goTo.containsKey(token)) {
                        throw new RuntimeException("Invalid symbol " + token + " for goto of state " + lastItemInWS);
                    }

                    int value = goTo.get(token);
                    workingStack.add(
                            new Pair<>(
                                    token,
                                    value
                            )
                    );

                    inputStack.remove(0);
                    break;
                case ACCEPT:
                    Collections.reverse(outputBand);
                    return outputBand;

                case REDUCE:
                    for (Production prods : grammar.getProductions()) {
                        for (var prod : prods.getRHS().entrySet()) {
                            if (prod.getKey().equals(tableRow.getProductionIndexInList())) {
                                List<String> rhs = prod.getValue();
                                String firstSymbol = rhs.get(0);
                                int nrOccurences = Collections.frequency(rhs, firstSymbol);
                                int index = -1;
                                for (int i = workingStack.size() - 1; i >= 0; i--) {
                                    if (workingStack.get(i).first.equals(firstSymbol)) {
                                        nrOccurences--;
                                        if(nrOccurences == 0){
                                            index = i;
                                            break;
                                        }
                                    }
                                }
                                Integer prevState = workingStack.get(index - 1).second;
                                workingStack = workingStack.subList(0, index);
                                String reductionResult = prods.getLHS().get(0);
                                Integer goToResult = parsingTable.getTableRows().get(prevState).getGoTo().get(reductionResult);
                                workingStack.add(new Pair<>(reductionResult, goToResult));
                                outputBand.add(tableRow.getProductionIndexInList());
                            }
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("How did you even get here?");
            }
        }
        return null;
    }
}
