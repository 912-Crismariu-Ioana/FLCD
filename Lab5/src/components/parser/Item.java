package components.parser;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private final String lhs;
    private final List<String> rhs;
    private int dotPosition;
    private int index;

    public Item(String lhs, List<String> rhs, int dotPosition, int index) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.dotPosition = dotPosition;

        this.index = index;
    }

    public String getLhs() {
        return lhs;
    }

    public List<String> getRhs() {
        return rhs;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public String getSymbolAfterTheDot(){
        if(this.getDotPosition() < this.getRhs().size()){
            return this.getRhs().get(this.getDotPosition());
        }
        return null;
    }

    @Override
    public boolean equals(Object another){
        if (another == this) {
            return true;
        }

        if (!(another instanceof Item)) {
            return false;
        }

        Item otherItem = (Item) another;

        if(!lhs.equals(otherItem.getLhs())){
            return false;
        }

        return rhs.size() == otherItem.rhs.size() && otherItem.rhs.containsAll(rhs);
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> rhs1 = new ArrayList<>();
        List<String> rhs2 = new ArrayList<>();
        for (int i = 0; i < dotPosition; ++i)
            rhs1.add(rhs.get(i));
        for (int i = dotPosition; i < rhs.size(); ++i)
            rhs2.add(rhs.get(i));
        stringBuilder.append(lhs).append(" -> ").append(rhs1).append('.').append(rhs2);
        return stringBuilder.toString();
    }

    public int getIndex() {
        return index;
    }
}
