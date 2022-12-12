package components.parser;

import components.grammar.Grammar;
import components.grammar.Production;
import components.parser.table.Table;
import components.parser.table.TableRow;

import java.util.*;

public class LRZeroParser {
    private final Grammar grammar;
    private final List<Production> enrichedProductions = new ArrayList<>();

    public LRZeroParser(Grammar grammar) {
        this.grammar = grammar;
        List<Production> productions = grammar.getProductions();

        Production enrichedGrammarProduction = new Production();
        List<String> lhs = List.of("S'");
        List<List<String>> rhs = List.of(Collections.singletonList(grammar.getStartingSymbol()));

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
                    for(List<String> rhs : production.getRHS()){
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
                        } else if(canonicalCollection.contains(newState)){
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
            table.add(tableRow);
        }
        return new Table(symbols, table);
    }

    public void parse(){
        System.out.println(getParsingTable());
    }

}