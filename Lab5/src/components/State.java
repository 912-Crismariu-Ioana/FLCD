package components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State {
    private Set<Item> items;

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

    List<String> getSymbolsAfterTheDot(){
        ArrayList<String> symbols = new ArrayList<>();
        for (Item item : items)
            if(item.getDotPosition() < item.getRhs().size())
                symbols.add(item.getRhs().get(item.getDotPosition()));
        return symbols;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "State{" + items + '}';
    }
}
