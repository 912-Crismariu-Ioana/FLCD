package components.parser;

import components.grammar.Grammar;
import components.grammar.Production;
import components.parser.tree.ParsingTreeRow;
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


    /**
     * Computes the closure for a given item
     * @param item parsing item
     * @return a new state (set of parsing items)
     */
    private State closure(Item item){
        Set<Item> closure = new HashSet<>();
        // Add starting item
        closure.add(item);

        // Boolean variable used to track changes to the set of items
        boolean changed = true;

        do {
            changed = false;
            Set<Item> oldClosure = new HashSet<>(closure);
            for (Item it : oldClosure) {
                String symbol = it.getSymbolAfterTheDot();
                if(!grammar.getNonTerminals().contains(symbol)){
                    continue;
                }

                // We have a non-terminal after the dot => create new parsing items for each of its productions
                // Add them to the canonical collection and mark the change
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

    /**
     * @param state the initial state before the transition
     * @param startingSymbol the symbol used in the transition
     * @return the generated state after the transition
     */
    private State goTo(State state, String startingSymbol) {
        Set<Item> result = new HashSet<>();
        for (Item item :state.getItems()) {
            // Generate a new item for each item in the original state, but advance the dot position
            // Compute its closure and add the result to the set of items of the new generated state
            String symbol = item.getSymbolAfterTheDot();
            if (startingSymbol.equals(symbol)) {
                Item newItem = new Item(item.getLhs(),
                        item.getRhs(),
                        item.getDotPosition() + 1,
                        item.getIndex());
                result.addAll(closure(newItem).getItems());
            }
        }
        return new State(result);
    }

    /**
     * Retrieve the entire canonical collection used in constructing the parsing table
     * @return set of states making up the canonical collection
     */
    private List<State> getCanonicalCollection(){
        // States are indexed so that we're able to refer to them in the parsing table
        int index = 0;
        List<State> canonicalCollection = new ArrayList<>();
        // First item is based on the first production in the enhanced grammar
        Item firstItem = new Item("S'",
                List.of(grammar.getStartingSymbol()),
                0,
                index);
        State firstState = closure(firstItem);
        firstState.setIndex(index);
        canonicalCollection.add(firstState);
        index++;

        // Set containing all the symbols in the grammar
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());

        // Boolean variable used to track changes to the collection
        boolean changed = true;
        while(changed) {
            changed = false;
            List<State> states = new ArrayList<>(canonicalCollection);
            for (State state : states) {
                for (String symbol : symbols) {
                    State newState = goTo(state, symbol);
                    // Filter out states for which performing goto doesn't yield meaningful results
                    if (newState.getItems().size() > 0) {
                        if (!canonicalCollection.contains(newState)) {
                            newState.setIndex(index);
                            canonicalCollection.add(newState);
                            index++;
                            changed = true;
                        } else {
                            // We use this to link already generated states to their initial state
                            // e.g. the state before performing goto
                            int stateIndex = canonicalCollection.indexOf(newState);
                            state.addReachableState(symbol, stateIndex);
                        }
                    }
                }
            }
        }
        return canonicalCollection;
    }

    /**
     * @return the parsing table
     */
    public Table getParsingTable(){
        List<State> canonicalCollection = getCanonicalCollection();
        // Store final result
        List<TableRow> table = new ArrayList<>();
        // Set containing all the symbols in the grammar
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());
        for (State state : canonicalCollection) {
            // Action column
            ActionType actionType = state.getNextAction();
            // GoTo columns, initialize with error values
            Map<String, Integer> goTo = new HashMap<>();
            symbols.forEach(symbol -> goTo.put(symbol, -1));
            // For each symbol, fill in the next state we can reach from the current state
            goTo.putAll(state.getReachableStates());
            TableRow tableRow = new TableRow(actionType, goTo);
            tableRow.setStateIndex(state.getIndex());
            if(actionType.equals(ActionType.REDUCE)){
                // Solve REDUCE-REDUCE conflicts by choosing the first production
                // i.e. the production with the smallest index
                List<Item> triggerItemsReduce = state.getItems().stream().filter(item -> item.getDotPosition() == item.getRhs().size()).collect(Collectors.toList());
                if(triggerItemsReduce.size() > 0){
                    Item toReduce = triggerItemsReduce.stream().min(Comparator.comparingInt(Item::getIndex)).get();
                    for(Production production: grammar.getProductions()){
                        if(production.getLHS().get(0).equals(toReduce.getLhs())){
                            for(var prodRhs: production.getRHS().entrySet()){
                                if(prodRhs.getValue().equals(toReduce.getRhs())){
                                    // Extract the index of the production to be used in the reduce action
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

    private String formatSyntaxErrorInfo(Integer state, String symbol){
        return state >= 0 ? "Last state was " + state + " and last symbol was " + symbol : "";
    }

    /**
     * Function performing the analysis for a given input sequence
     * The sequence is accepted if it is part of the language generated by our grammar
     * @param inputSequence list of symbols to be tested
     * @return the output band, a list of the production numbers used in performing the analysis, in order
     */
    public List<Integer> parse(List<String> inputSequence) {
        // Working stack contains pairs of symbol & state number
        List<Pair<String, Integer>> workingStack = new ArrayList<>();
        List<String> inputStack = new ArrayList<>(inputSequence);
        List<Integer> outputBand = new ArrayList<>();
        Table parsingTable = getParsingTable();
        int oldState = -1;
        workingStack.add(new Pair<>("$", 0));
        while (!inputStack.isEmpty() || !workingStack.isEmpty()) {
            // Extract last symbol in ws for printing error messages
            String lastSymbolInWs = workingStack.get(workingStack.size() - 1).first;
            // Extract the state number from the last entry in the working stack
            Integer lastStateInWS = workingStack.get(workingStack.size() - 1).second;
            if (lastStateInWS == null || lastStateInWS < 0 || lastStateInWS >= parsingTable.getTableRows().size()) {
                // Invalid state number
                throw new RuntimeException("Invalid state number in working stack"
                        + formatSyntaxErrorInfo(oldState, lastSymbolInWs));
            }
            // Row corresponding to the current state
            TableRow tableRow = parsingTable.getTableRows().get(lastStateInWS);
            switch (tableRow.getAction()) {
                case SHIFT:
                    if (inputStack.isEmpty()) {
                        throw new RuntimeException("Action is shift but nothing else is left in the remaining stack. " +
                                "Last state was " + lastStateInWS);
                    }
                    String token = inputStack.get(0);
                    Map<String, Integer> goTo = tableRow.getGoTo();

                    if (!goTo.containsKey(token)) {
                        throw new RuntimeException("Invalid symbol " + token + " for goto of state " + lastStateInWS);
                    }
                    // Get the next state that we can reach from the current state using the current input symbol
                    int nextState = goTo.get(token);
                    workingStack.add(new Pair<>(token, nextState));

                    inputStack.remove(0);
                    oldState = tableRow.getStateIndex();
                    break;
                case ACCEPT:
                    if(!inputStack.isEmpty()){
                        // Part of the sequence might be acceptable, but we have not finished analysing it
                        throw new RuntimeException("There are unprocessed symbols" + formatSyntaxErrorInfo(lastStateInWS,
                                lastSymbolInWs));
                    }
                    Collections.reverse(outputBand);
                    return outputBand;

                case REDUCE:
                    for (Production prods : grammar.getProductions()) {
                        for (var prod : prods.getRHS().entrySet()) {
                            if (prod.getKey().equals(tableRow.getProductionIndexInList())) {
                                // Find the production that we need to apply
                                // Make sure that we pop the entire rhs of the production from the working stack
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
                                // Replace the rhs with lhs
                                // Get next state using the previous state and newly replaced symbol
                                Integer goToResult = parsingTable.getTableRows()
                                        .get(prevState)
                                        .getGoTo()
                                        .get(reductionResult);
                                workingStack.add(new Pair<>(reductionResult, goToResult));
                                oldState = tableRow.getStateIndex();
                                // Add production number to output band
                                outputBand.add(tableRow.getProductionIndexInList());
                            }
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("Wrong action type");
            }
        }
        return null;
    }
}
