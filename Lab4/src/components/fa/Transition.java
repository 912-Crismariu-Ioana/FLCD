package components.fa;

import java.util.HashSet;
import java.util.Set;

public class Transition {
    private final String currentState;
    private final String inputSymbol;
    private final Set<String> nextStates = new HashSet<>();

    public Transition(String currentState, String inputSymbol) {
        this.currentState = currentState;
        this.inputSymbol = inputSymbol;
    }

    public void addNextState(String state){
        nextStates.add(state);
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getInputSymbol() {
        return inputSymbol;
    }

    public Set<String> getNextStates() {
        return nextStates;
    }

    @Override
    public String toString(){
        return currentState + "--" + inputSymbol + "-->" + nextStates;
    }
}
