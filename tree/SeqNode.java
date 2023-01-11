package org.example.multi.tree;

public class SeqNode {
    int value;
    SeqNode left;
    SeqNode right;

    String state;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public SeqNode getLeft() {
        return left;
    }

    public void setLeft(SeqNode left) {
        this.left = left;
    }

    public SeqNode getRight() {
        return right;
    }

    public void setRight(SeqNode right) {
        this.right = right;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
