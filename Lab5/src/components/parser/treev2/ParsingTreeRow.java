package components.parser.treev2;

public class ParsingTreeRow {
    private int index;
    private String info;
    private int parent;
    private int rightSibling;

    public ParsingTreeRow(int index, String info, int parent, int rightSibling) {
        this.index = index;
        this.info = info;
        this.parent = parent;
        this.rightSibling = rightSibling;
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

    public int getRightSibling() {
        return rightSibling;
    }

    public void setRightSibling(int rightSibling) {
        this.rightSibling = rightSibling;
    }
}
