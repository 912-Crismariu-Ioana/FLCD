package components;

import java.util.*;

public class LR {
    private Grammar grammar;
    private List<Production> enrichedProductions = new ArrayList<>();

    public LR(Grammar grammar) {
        Set<Production> productions = grammar.getProductions();

        Production enrichedGrammarProduction = new Production();
        List<String> lhs = new ArrayList<>();
        Set<List<String>> rhs = new HashSet<>();
        lhs.add("S'");
        rhs.add(Collections.singletonList(grammar.getStartingSymbol()));

        enrichedGrammarProduction.setLHS(lhs);
        enrichedGrammarProduction.setRHS(rhs);

        // we add the first element to the new productions (S' -> first symbol)
        this.enrichedProductions.add(enrichedGrammarProduction);

        // we add the rest of the productions to the new productions list
        this.enrichedProductions.addAll(productions);
    }

    public String getNonTerminalB4Dot(Item item){
        String term = item.getRhs().get(item.getDotPosition());
        for (int i = 0; i < grammar.getNonTerminals().size(); ++i)
            if (!grammar.getNonTerminals().contains(term))
                return null;
        return term;
    }

    private State closure(Item item){
        Set<Item> oldClosure;
        Set<Item> currentClosure = new HashSet<>();
        String nonTerminal;
        Item currentItem;
        currentClosure.add(item);

        do {
            oldClosure = currentClosure;
            Set<Item> newClosure = new HashSet<>(currentClosure);
            for (Item it : currentClosure) {
                nonTerminal = getNonTerminalB4Dot(it);
                if(nonTerminal == null){
                    continue;
                }
                for (Production production : grammar.getProductionsForNonTerminal(nonTerminal)) {
                    for(List<String> rhs : production.getRHS()){
                        currentItem = new Item(nonTerminal, rhs, 0);
                        newClosure.add(currentItem);
                    }
                }
            }
            currentClosure = newClosure;
        } while (oldClosure != currentClosure);

        return new State(currentClosure);
    }

    private State goTo(State state, String symbol) {
        Set<Item> result = new HashSet<>();
        for (Item item :state.getItems()) {
            String nonTerminal = item.getRhs().get(item.getDotPosition());
            if (symbol.equals(nonTerminal)) {
                Item nextItem = new Item(item.getLhs(), item.getRhs(), item.getDotPosition() + 1);
                result.addAll(closure(nextItem).getItems());
            }
        }
        return new State(result);
    }

    private Map<Integer,State> getCanonicalCollection(){
        Integer index = 0;
        Map<Integer, State> canonicalCollection = new HashMap<>();
        canonicalCollection.put(index,
                closure(
                        new Item(
                                enrichedProductions.get(0).getLHS().get(0),
                                enrichedProductions.get(0).getRHS().stream().findFirst().get(),
                                0
                        )
                )
        );
        index++;
        Set<String> symbols = new HashSet<>();
        symbols.addAll(grammar.getNonTerminals());
        symbols.addAll(grammar.getTerminals());
        boolean changed = true;
        while(changed){
            changed = false;
            for(State state: canonicalCollection.values()){
                for(String symbol: symbols){
                    State newState = goTo(state, symbol);
                    if(newState.getItems().size() > 0 && canonicalCollection.containsValue(newState)){
                        canonicalCollection.put(index, newState);
                        index++;
                        changed = true;
                    }
                }
            }
        }
        return canonicalCollection;
    }

}
