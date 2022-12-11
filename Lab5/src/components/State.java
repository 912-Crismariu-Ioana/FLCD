package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State {
    private final Set<Item> items;

    public State(Set<Item> items) {
        this.items = items;
    }

    public ActionType getNextActionType(){
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
        return "State{" + items + '}';
    }
}
