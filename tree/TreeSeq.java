package org.example.multi.tree;

import java.util.*;

public class TreeSeq {

    private final SeqNode root;

    public TreeSeq(Integer initialVal) {
        this.root = new SeqNode();
        this.root.setValue(initialVal);
        this.root.setState("data");
    }

    private List<SeqNode> traversal(Integer v) {
        SeqNode gPrev = new SeqNode();
        SeqNode prev = new SeqNode();
        SeqNode curr = this.root;

        while (curr != null) {
            if (curr.getValue().equals(v)) {
                break;
            } else {
                gPrev = prev;
                prev = curr;
                if (curr.getValue().compareTo(v) > 0) {
                    curr = curr.getLeft();
                } else {
                    curr = curr.getRight();
                }
            }
        }

        return Arrays.asList(gPrev, prev, curr);
    }

    public boolean contains(Integer v) {
        List<SeqNode> traversal = traversal(v);
        SeqNode curr = traversal.get(2);
        return curr != null && curr.getState().equals("data");
    }

    public boolean insert(Integer v) {
        List<SeqNode> traversal = traversal(v);
        SeqNode prev = traversal.get(1);
        SeqNode curr = traversal.get(2);

        if (curr != null) {
            if (curr.getState().equals("data")) {
                return false;
            }

            curr.setState("data");
        } else {
            SeqNode newNodeSeq = new SeqNode();
            newNodeSeq.setValue(v);
            newNodeSeq.setState("data");
            if (prev.getValue().compareTo(v) > 0) {
                prev.setLeft(newNodeSeq);
            } else {
                prev.setRight(newNodeSeq);
            }
        }

        return true;
    }

    public boolean delete(Integer v) {
        List<SeqNode> traversal = traversal(v);
        SeqNode gPrev = traversal.get(0);
        SeqNode prev = traversal.get(1);
        SeqNode curr = traversal.get(2);

        if (Objects.isNull(curr) || !curr.getState().equals("data")) {
            return false;
        }

        if (curr.getLeft() != null && curr.getRight() != null) {
            curr.setState("route");
        } else if (curr.getLeft() != null || curr.getRight() != null) {
            SeqNode child = curr.getLeft() != null ? curr.getLeft() : curr.getRight();

            if (curr.getValue().compareTo(prev.getValue()) < 0) {
                prev.setLeft(child);
            } else {
                prev.setRight(child);
            }
        } else {
            if (prev.getState().equals("data")) {
                if (curr == prev.getLeft()) {
                    prev.setLeft(null);
                } else {
                    prev.setRight(null);
                }
            } else {
                SeqNode child;
                if (curr == prev.getLeft()) {
                    child = prev.getRight();
                } else {
                    child = prev.getLeft();
                }

                if (prev == gPrev.getLeft()) {
                    gPrev.setLeft(child);
                } else {
                    gPrev.setRight(child);
                }
            }
        }

        return true;
    }

    public List<Integer> inorderTraversal() {
        List<Integer> list = new ArrayList<>();
        Stack<SeqNode> stack = new Stack<>();
        SeqNode curr = root;
        while (curr != null || !stack.empty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.getLeft();
            }
            curr = stack.pop();
            if (curr.getState().equals("data")) {
                list.add(curr.getValue());
            }
            curr = curr.getRight();
        }
        return list;
    }
}