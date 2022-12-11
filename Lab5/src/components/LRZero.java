package components;

import java.util.*;

public class LRZero {
    private final Grammar grammar;
    private final List<Production> enrichedProductions = new ArrayList<>();

    public LRZero(Grammar grammar) {
        this.grammar = grammar;
        Set<Production> productions = grammar.getProductions();

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

    private Map<Integer,State> getCanonicalCollection(){
        Integer index = 0;
        Map<Integer, State> canonicalCollection = new HashMap<>();
        Item firstItem = new Item("S'",
                List.of(grammar.getStartingSymbol()),
                0
        );
        canonicalCollection.put(index, closure(firstItem));
        index++;
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());
        boolean changed = true;
        while(changed){
            changed = false;
            Set<State> states = new HashSet<>(canonicalCollection.values());
            for(State state: states){
                for(String symbol: symbols){
                    State newState = goTo(state, symbol);
                    if(newState.getItems().size() > 0 && !canonicalCollection.containsValue(newState)){
                        canonicalCollection.put(index, newState);
                        index++;
                        changed = true;
                    }
                }
            }
        }
        return canonicalCollection;
    }

    public void parse(){
        System.out.println(getCanonicalCollection());
    }

}
