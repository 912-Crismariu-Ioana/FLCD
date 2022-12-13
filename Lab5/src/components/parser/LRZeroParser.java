package components.parser;

import components.grammar.Grammar;
import components.grammar.Production;
import components.utils.Pair;
import components.parser.table.Table;
import components.parser.table.TableRow;
import components.parser.tree.Node;

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
                    for(List<String> rhs : production.getRHS().values()){
                        Item currentItem = new Item(symbol, rhs, 0);
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
                Item newItem = new Item(item.getLhs(), item.getRhs(), item.getDotPosition() + 1);
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
                0
        );
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
                Item toReduce = state.getItems()
                        .stream()
                        .filter(item -> item.getDotPosition() == item.getRhs().size()).findFirst().orElse(null);
                //int index = grammar.getProductions().stream().collect(Collectors.toList());
            }
            table.add(tableRow);
        }
        return new Table(symbols, table);
    }

//    public List<Integer> parse(List<String> word) {
//        List<Node> nodes = new ArrayList<>();
//        List<Pair<String, Integer>> workingStack = new ArrayList<>();
//        List<String> remainingStack = word;
//        List<Integer> productionStack = new ArrayList<>();
//        Table parsingTable = getParsingTable();
//        workingStack.add(new Pair<>("$", 0));
//        int currentIndex = 0;
//        while (!remainingStack.isEmpty() || !workingStack.isEmpty()) {
//            Integer lastItemInWS = workingStack.get(workingStack.size()-1).second;
//            if(lastItemInWS == null || lastItemInWS < 0 || lastItemInWS >= parsingTable.getTableRows().size()){
//                throw new RuntimeException("Invalid last element in working stack!!!");
//            }
//            TableRow tableRow = parsingTable.getTableRows().get(lastItemInWS);
//            switch (tableRow.getAction()) {
//                case ActionType.SHIFT:
//                    if(remainingStack.isEmpty()) {
//                        throw new RuntimeException("Action is shift but nothing else is left in the remaining stack");
//                    }
//                    String token = remainingStack.get(0);
//                    Map<String, Integer> goTo = tableRow.getGoTo();
//                    if(!goTo.containsKey(token)) {
//                        throw new RuntimeException("Invalid symbol \"$token\" for goto of state ${workingStack.last().second}");
//                    }
//
//                    int value = goTo.get(token);
//                    workingStack.add(
//                            new Pair<>(
//                                    token,
//                                    value
//                            )
//                    );
//
//                    remainingStack.remove(0);
//                    break;
//                case ActionType.ACCEPT:
//                    return productionStack;
//
//                case ActionType.REDUCE:
//                    Production productionToReduceTo = grammar.getProductions().stream().filter(production ->
//                            production.getIndex() == tableRow.getProductionIndexInList()).findFirst().orElse(null);
//
//                    String firstElement = productionToReduceTo.getRHS().get(0);
//
//                    int parentIndex = currentIndex++;
//                    var lastIndex = -1;
//                    for (int j = 0; j <= productionToReduceTo.getRHS().size(); ++j) {
//                        workingStack.remove(workingStack.size() - 1);
//                        workingStack.removeLast();
//                        val lastElement = treeStack.removeLast()
//                        parsingTree.add(
//                                ParsingTreeRow(
//                                        lastElement.second,
//                                        lastElement.first,
//                                        parentIndex,
//                                        lastIndex
//                                )
//                        )
//                        lastIndex = lastElement.second
//                    }
//                    treeStack.add(Pair(productionToReduceTo.first, parentIndex))
//                    val previous = workingStack.last()
//                    workingStack.add(
//                            Pair(
//                                    productionToReduceTo.first,
//                                    parsingTable.tableRow[previous.second]!!.goto!![productionToReduceTo.first]!!
//                        )
//                    )
//                    productionStack.add(0, tableValue.reductionIndex)
//
//                }
//                else -> throw Exception(tableValue.action.toString())
//            }
//        }
//        throw Exception("How did you even get here?")
//    }



}
