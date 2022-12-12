package components.parser.table;

import java.util.List;
import java.util.Set;

public class Table {
    private final Set<String> headerSymbols;
    private final List<TableRow> tableRows;


    public Table(Set<String> header, List<TableRow> tableRows) {
        this.headerSymbols = header;
        this.tableRows = tableRows;
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
                    sb.append(String.format(leftAlignFormatText, tableRow.getAction()));
                    tableRow.getGoTo().forEach((k, v)-> sb.append(String.format(leftAlignFormatText, v)));
                    sb.append("\n");
                }
        );
        return sb.toString();
    }
}
