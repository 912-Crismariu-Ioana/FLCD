package components.grammar;

import components.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Production {
    private List<String> LHS = new ArrayList<>();
    private Map<Integer, List<String>> RHS = new HashMap<>();


    public List<String> getLHS() {
        return LHS;
    }

    public void setLHS(List<String> LHS) {
        this.LHS = LHS;
    }

    public Map<Integer, List<String>> getRHS() {
        return RHS;
    }

    public void setRHS(Map<Integer, List<String>> RHS) {
        this.RHS = RHS;
    }

    public boolean isSymbolInLHS(String symbol){
        return LHS.contains(symbol);
    }


    @Override
    public String toString(){
        return  String.join("",LHS) + " -> " + RHS.values().stream()
                .map(strings -> String.join(" ", strings) + "(" + strings + ")")
                .reduce((partialString, element) -> partialString + " | " + element)
                .orElse("epsilon");
    }

    @Override
    public boolean equals(Object another){
        if (another == this) {
            return true;
        }

        if (!(another instanceof Production)) {
            return false;
        }

        Production otherProd = (Production) another;

        if(!LHS.equals(otherProd.getLHS())){
            return false;
        }

        return RHS.size() == otherProd.RHS.size() && otherProd.RHS.entrySet().containsAll(RHS.entrySet());
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }
}
