package components.parser.tree;

public class Node {
    private int index;
    private String symbol;
    private Node father;
    private Node sibling;
    public Node(int index, String symbol, Node father, Node sibling){
        this.index = index;
        this.symbol = symbol;
        this.father = father;
         this.sibling = sibling;
    }

    public Node getFather() {
        return father;
    }

    public Node getSibling() {
        return sibling;
    }

    public int getIndex() {
        return index;
    }

    public String getSymbol() {
        return symbol;
    }
}
