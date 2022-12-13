package components.parser.table;

import components.parser.ActionType;

import java.util.List;
import java.util.Set;

public class Table {
    private final Set<String> headerSymbols;
    private final List<TableRow> tableRows;


    public Table(Set<String> header, List<TableRow> tableRows) {
        this.headerSymbols = header;
        this.tableRows = tableRows;
    }

    public List<TableRow> getTableRows() {
        return tableRows;
    }

    @Override
    public String toString(){
        if(tableRows.size() < 1){
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        String leftAlignFormatNumber = "| %-3d |";
        String leftAlignFormatText = "| %-10s |";
        sb.append(String.format("| %-3s |", ""));
        sb.append(String.format(leftAlignFormatText, "ACTION"));
        headerSymbols.forEach(elem -> sb.append(String.format(leftAlignFormatText, elem)));
        sb.append("\n");
        tableRows.forEach(tableRow ->
                {
                    sb.append(String.format(leftAlignFormatNumber, tableRow.getStateIndex()));
                    String action = tableRow.getAction().equals(ActionType.REDUCE) ? tableRow.getAction()
                            + " " + tableRow.getProductionIndexInList() : tableRow.getAction().toString();
                    sb.append(String.format(leftAlignFormatText, action));
                    tableRow.getGoTo().forEach((k, v)->
                            {
                                String val = v == -1 ? "err" : v.toString();
                                sb.append(String.format(leftAlignFormatText, val));
                            }
                    );
                    sb.append("\n");
                }
        );
        return sb.toString();
    }
}
