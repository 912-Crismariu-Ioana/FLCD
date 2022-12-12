package components.parser.table;

import components.parser.ActionType;

import java.util.Map;

public class TableRow {
    private final ActionType action;
    private final Map<String, Integer> goTo;
    private int stateIndex = -1;

    public TableRow(ActionType action, Map<String, Integer> goTo) {
        this.action = action;
        this.goTo = goTo;
    }

    public Map<String, Integer> getGoTo() {
        return goTo;
    }

    public ActionType getAction() {
        return action;
    }

    public void setStateIndex(int stateIndex) {
        this.stateIndex = stateIndex;
    }

    public int getStateIndex() {
        return stateIndex;
    }
}