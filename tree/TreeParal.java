package org.example.multi.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Lock;

public class TreeParal {
    private final TreeNode root;
    private static final ThreadLocal<Stack<Lock>> localLocks = ThreadLocal.withInitial(Stack::new);

    public TreeParal(Integer initialVal) {
        this.root = new TreeNode();
        this.root.setValue(initialVal);
        this.root.setState("data");
    }

    private List<TreeNode> traversal(Integer v) {
        while (true) {
            TreeNode gPrev = new TreeNode();
            TreeNode prev = new TreeNode();
            TreeNode curr = this.root;

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

                if (checkDeleted(gPrev)) break;
                if (checkDeleted(prev)) break;
                if (checkDeleted(curr)) break;
            }

            if (checkDeleted(gPrev)) continue;
            if (checkDeleted(prev)) continue;
            if (checkDeleted(curr)) continue;

            return Arrays.asList(gPrev, prev, curr);
        }
    }

    private boolean checkDeleted(TreeNode node) {
        return node != null && node.getDeleted().get();
    }

    public boolean contains(Integer v) {
        List<TreeNode> traversal = traversal(v);
        TreeNode curr = traversal.get(2);
        return curr != null && curr.getState().equals("data");
    }

    public boolean insert(Integer v) {
        while (true) {
            try {
                List<TreeNode> traversal = traversal(v);
                TreeNode prev = traversal.get(1);
                TreeNode curr = traversal.get(2);

                if (curr != null) {
                    if (curr.getState().equals("data")) {
                        return false;
                    }
                    curr.tryWriteLockState("route", localLocks);
                    curr.setState("data");
                } else {
                    TreeNode newNodePar = new TreeNode();
                    newNodePar.setValue(v);
                    newNodePar.setState("data");
                    if (prev.getValue().compareTo(v) > 0) {
                        prev.tryReadLockState(localLocks);
                        prev.tryLockLeftEdgeRef(null, localLocks);
                        prev.setLeft(newNodePar);
                    } else {
                        prev.tryReadLockState(localLocks);
                        prev.tryLockRightEdgeRef(null, localLocks);
                        prev.setRight(newNodePar);
                    }
                }

                return true;
            } catch (Exception ignore) {
            } finally {
                unlockAllLocks();
            }
        }
    }

    private void unlockAllLocks() {
        while (!localLocks.get().empty()) {
            localLocks.get().pop().unlock();
        }
    }

    public boolean delete(Integer v) {
        while (true) {
            try {
                List<TreeNode> traversal = traversal(v);
                TreeNode gPrev = traversal.get(0);
                TreeNode prev = traversal.get(1);
                TreeNode curr = traversal.get(2);

                if (curr == null || !curr.getState().equals("data")) {
                    return false;
                }

                if (curr.getLeft() != null && curr.getRight() != null) {
                    curr.tryWriteLockState("data", localLocks);

                    if (curr.getLeft() == null || curr.getRight() == null) {
                        throw new RuntimeException("DELETE: not have children");
                    }
                    curr.setState("route");
                } else if (curr.getLeft() != null || curr.getRight() != null) {
                    TreeNode child = curr.getLeft() != null ? curr.getLeft() : curr.getRight();

                    if (curr.getValue().compareTo(prev.getValue()) < 0) {
                        lockVertexWithOneChild(prev, curr, child);
                        curr.getDeleted().set(true);
                        prev.setLeft(child);
                    } else {
                        lockVertexWithOneChild(prev, curr, child);
                        curr.getDeleted().set(true);
                        prev.setRight(child);
                    }
                } else {
                    if (prev.getState().equals("data")) {
                        if (curr.getValue().compareTo(prev.getValue()) < 0) {
                            prev.tryReadLockState("data", localLocks);

                            curr = lockLeaf(v, prev, curr);

                            curr.getDeleted().set(true);
                            prev.setLeft(null);
                        } else {
                            prev.tryReadLockState("data", localLocks);

                            curr = lockLeaf(v, prev, curr);

                            curr.getDeleted().set(true);
                            prev.setRight(null);
                        }
                    } else {
                        TreeNode child;
                        if (curr.getValue().compareTo(prev.getValue()) < 0) {
                            child = prev.getRight();
                        } else {
                            child = prev.getLeft();
                        }

                        if (gPrev.getLeft() != null && prev == gPrev.getLeft()) {
                            gPrev.tryLockEdgeRef(prev, localLocks);
                            prev.tryWriteLockState("route", localLocks);
                            prev.tryLockEdgeRef(child, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            prev.getDeleted().set(true);
                            curr.getDeleted().set(true);
                            gPrev.setLeft(child);
                        } else if (gPrev.getRight() != null && prev == gPrev.getRight()) {
                            gPrev.tryLockEdgeRef(prev, localLocks);
                            prev.tryWriteLockState("route", localLocks);
                            prev.tryLockEdgeRef(child, localLocks);

                            curr = lockLeaf(v, prev, curr);

                            prev.getDeleted().set(true);
                            curr.getDeleted().set(true);
                            gPrev.setRight(child);
                        }
                    }
                }
                return true;
            } catch (Exception ignore) {
            } finally {
                unlockAllLocks();
            }
        }
    }

    private TreeNode lockLeaf(Integer value, TreeNode prev, TreeNode curr) {
        prev.tryLockEdgeVal(curr, localLocks);

        if (value.compareTo(prev.getValue()) < 0) {
            curr = prev.getLeft();
        } else {
            curr = prev.getRight();
        }

        curr.tryWriteLockState("data", localLocks);

        if (curr.getLeft() != null || curr.getRight() != null) {
            throw new RuntimeException("is not Leaf");
        }
        return curr;
    }

    private void lockVertexWithOneChild(TreeNode prev, TreeNode curr, TreeNode child) {
        prev.tryLockEdgeRef(curr, localLocks);
        curr.tryWriteLockState("data", localLocks);

        if (curr.getLeft() != null && curr.getRight() != null) {
            throw new RuntimeException("try lockVertexWithOneChild but has 2 children");
        }

        if (curr.getLeft() == null && curr.getRight() == null) {
            throw new RuntimeException("try lockVertexWithOneChild but has 0 children");
        }

        curr.tryLockEdgeRef(child, localLocks);
    }

    public List<Integer> inorderTraversal() {
        List<Integer> list = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;
        while (curr != null || !stack.empty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.getLeft();
            }
            curr = stack.pop();
            if (curr.getState().equals("data") && !curr.getDeleted().get()) {
                list.add(curr.getValue());
            }
            curr = curr.getRight();
        }
        return list;
    }
}
