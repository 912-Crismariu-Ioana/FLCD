package components;

public class Node {
    private Node father;
    private Node sibling;
    public Node(Node father, Node sibling){
         this.father = father;
         this.sibling = sibling;
    }

    public Node getFather() {
        return father;
    }

    public Node getSibling() {
        return sibling;
    }
}
