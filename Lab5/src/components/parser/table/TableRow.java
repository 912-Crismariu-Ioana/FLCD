package components.parser.table;

import components.parser.ActionType;

import java.util.Map;

public class TableRow {
    private ActionType action;
    private final Map<String, Integer> goTo;
    private int stateIndex = -1;
    private int productionIndexInList = -1;


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

    public int getProductionIndexInList() {
        return productionIndexInList;
    }

    public void setProductionIndexInList(int productionIndexInList) {
        this.productionIndexInList = productionIndexInList;
    }

    public void setActionType(ActionType actionType) {
        this.action = actionType;
    }
}
