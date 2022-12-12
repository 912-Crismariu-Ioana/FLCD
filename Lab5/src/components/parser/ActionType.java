package components.parser;

public enum ActionType {
    REDUCE,
    SHIFT,
    ACCEPT,
    ERROR,
    SHIFT_REDUCE_CONFLICT,
    REDUCE_REDUCE_CONFLICT
}
