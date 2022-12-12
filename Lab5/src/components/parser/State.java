package components.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State {
    private int index = -1;

    private final Set<Item> items;

    private final Map<String, Integer> reachableStates = new HashMap<>();

    public State(Set<Item> items) {
        this.items = items;
    }

    public ActionType getNextAction(){
        if(items.stream().filter(item->item.getLhs().equals("S'") &&
           item.getDotPosition() == item.getRhs().size()).count() == 1){
            return ActionType.ACCEPT;
        }

        if(items.stream().anyMatch(item -> item.getDotPosition() == item.getRhs().size())){
            return ActionType.REDUCE;
        }

        if(items.stream().anyMatch(item -> item.getDotPosition() < item.getRhs().size())){
            return ActionType.SHIFT;
        }

        return ActionType.ERROR;
    }

    @Override
    public boolean equals(Object another){
        if (another == this) {
            return true;
        }

        if (!(another instanceof State)) {
            return false;
        }

        State otherState = (State) another;

        return items.size() == otherState.items.size() && otherState.items.containsAll(items);
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    public Set<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "State " + index + " {" + items + '}';
    }

    public void addReachableState(String symbol, Integer stateIndex){
        reachableStates.put(symbol, stateIndex);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<String, Integer> getReachableStates() {
        return reachableStates;
    }
}
