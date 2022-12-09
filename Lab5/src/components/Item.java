package components;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String lhs;
    private final List<String> rhs;
    private final int dotPosition;

    public Item(String lhs, List<String> rhs, int dot_position) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.dotPosition = dot_position;
    }

    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public List<String> getRhs() {
        return rhs;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> rhs1 = new ArrayList<>();
        List<String> rhs2 = new ArrayList<>();
        for (int i = 0; i < dotPosition; ++i)
            rhs1.add(rhs.get(i));
        for (int i = dotPosition + 1; i < rhs.size(); ++i)
            rhs2.add(rhs.get(i));
        stringBuilder.append(lhs).append(" -> ").append(rhs1).append('.').append(rhs2);
        return stringBuilder.toString();
    }
}
