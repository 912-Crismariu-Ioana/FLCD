package components.parser.tree;

import components.parser.ActionType;

public class ParsingTreeRow {
    private int index;
    private String info;
    private int parent;
    private int leftSibling;

    public ParsingTreeRow(int index, String info, int parent, int leftSibling) {
        this.index = index;
        this.info = info;
        this.parent = parent;
        this.leftSibling = leftSibling;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getLeftSibling() {
        return leftSibling;
    }

    public void setLeftSibling(int leftSibling) {
        this.leftSibling = leftSibling;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        String leftAlignFormatIndex = "| %-5s |";
        String leftAlignFormatInformation = "| %-20s |";
        String leftAlignFormatParent = "| %-6s |";
        String leftAlignFormatRightSibling = "| %-10s ";

        sb.append(String.format(leftAlignFormatIndex, this.index));
        sb.append(String.format(leftAlignFormatInformation, this.info));
        sb.append(String.format(leftAlignFormatParent, this.parent));
        sb.append(String.format(leftAlignFormatRightSibling, this.leftSibling));
        sb.append("\n");

        return sb.toString();
    }
}
