package components;

import java.util.*;

public class LR {
    private Grammar grammar;

    public LR(Grammar grammar) {
        Set<Production> productions = grammar.getProductions();
        Set<Production> newProductions = new HashSet<>();

        Production firstProduction = productions.iterator().next();
        Production enrichedGrammarProduction = new Production();
        List<String> lhs = new ArrayList<>();
        Set<List<String>> rhs = new HashSet<>();
        lhs.add("S'");
        rhs.add(Collections.singletonList(firstProduction.getLHS().get(0)));

        enrichedGrammarProduction.setLHS(lhs);
        enrichedGrammarProduction.setRHS(rhs);

        // we add the first element to the new productions (S' -> first symbol)
        newProductions.add(enrichedGrammarProduction);

        // we add the rest of the productions to the new productions list
        Iterator<Production> iterator = productions.iterator();
        while (iterator.hasNext())
            newProductions.add(productions.iterator().next());

        this.grammar = grammar;
    }

    public String getDotPrecededNonTerminal(Item item){
        String term = item.getRhs().get(item.getDotPosition());
        for (int i = 0; i < grammar.getNonTerminals().size(); ++i)
            if (!grammar.getNonTerminals().contains(term))
                return null;
        return term;
    }

}
