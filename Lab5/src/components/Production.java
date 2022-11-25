package components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Production {
    private List<String> LHS = new ArrayList<>();
    private Set<List<String>> RHS = new HashSet<>();


    public List<String> getLHS() {
        return LHS;
    }

    public void setLHS(List<String> LHS) {
        this.LHS = LHS;
    }

    public Set<List<String>> getRHS() {
        return RHS;
    }

    public void setRHS(Set<List<String>> RHS) {
        this.RHS = RHS;
    }

    public boolean isSymbolInLHS(String symbol){
        return LHS.contains(symbol);
    }

    @Override
    public String toString(){
        return String.join("",LHS) + " -> " + RHS.stream()
                .map(r -> String.join(" ", r))
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

        if(!LHS.equals(((Production) another).getLHS())){
            return false;
        }

        return RHS.size() == otherProd.RHS.size() && otherProd.RHS.containsAll(RHS);
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }
}