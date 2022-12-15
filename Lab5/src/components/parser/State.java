package components.parser;

import java.util.*;
import java.util.stream.Collectors;

public class State {
    private int index = -1;

    private final Set<Item> items;

    private final Map<String, Integer> reachableStates = new HashMap<>();

    public State(Set<Item> items) {
        this.items = items;
    }

    public ActionType getNextAction(){
        ActionType actionType = ActionType.ERROR;


        if(items.stream().filter(item->item.getLhs().equals("S'") &&
           item.getDotPosition() == item.getRhs().size()).count() == 1){
            return ActionType.ACCEPT;
        }

        Item triggerItemShift = items.stream().filter(item -> item.getDotPosition() < item.getRhs().size()).findFirst().orElse(null);

        if(triggerItemShift != null){
             actionType = ActionType.SHIFT;
        }

        List<Item> triggerItemsReduce = items.stream().filter(item -> item.getDotPosition() == item.getRhs().size()).collect(Collectors.toList());

        if(triggerItemsReduce.size() > 0){
            actionType = ActionType.REDUCE;
        }

        if(triggerItemShift != null && triggerItemsReduce.size() > 0){
            String commonLHS = triggerItemShift.getLhs();
            for(Item item: triggerItemsReduce){
                if(item.getLhs().equals(commonLHS)){
                    return ActionType.SHIFT;
                }
            }
        }

        return actionType;
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
