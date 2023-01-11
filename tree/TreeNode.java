package org.example.multi.tree;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TreeNode {
    private String state;
    private Integer value;
    private TreeNode left;
    private TreeNode right;

    private final AtomicBoolean is_deleted = new AtomicBoolean();
    private final ReentrantReadWriteLock leftLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock rightLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock stateLock = new ReentrantReadWriteLock();

    public void tryLockLeftEdgeRef(TreeNode expRef, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.leftLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.is_deleted.get() || this.left != expRef) {
            throw new RuntimeException("Lock edge error");
        }
    }

    public void tryLockRightEdgeRef(TreeNode expRef, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.rightLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.is_deleted.get() || this.right != expRef) {
            throw new RuntimeException("Lock edge error");
        }
    }

    public void tryLockEdgeRef(TreeNode expRef, ThreadLocal<Stack<Lock>> localLocks) {
        if (value.compareTo(expRef.getValue()) < 0) {
            tryLockRightEdgeRef(expRef, localLocks);
        } else {
            tryLockLeftEdgeRef(expRef, localLocks);
        }
    }

    public void tryLockLeftEdgeVal(Integer expVal, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.leftLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.is_deleted.get() || Objects.isNull(left) || this.left.getValue().compareTo(expVal) != 0) {
            throw new RuntimeException("Lock value edge error");
        }
    }

    public void tryLockRightEdgeVal(Integer expVal, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = this.rightLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (this.is_deleted.get() || Objects.isNull(right) || this.right.getValue().compareTo(expVal) != 0) {
            throw new RuntimeException("Lock value edge error");
        }
    }

    public void tryLockEdgeVal(TreeNode expRef, ThreadLocal<Stack<Lock>> localLocks) {
        if (value.compareTo(expRef.getValue()) < 0) {
            tryLockRightEdgeVal(expRef.getValue(), localLocks);
        } else {
            tryLockLeftEdgeVal(expRef.getValue(), localLocks);
        }
    }

    public void tryReadLockState(ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.ReadLock lock = stateLock.readLock();

        lock.lock();
        localLocks.get().push(lock);

        if (is_deleted.get()) {
            throw new RuntimeException("ReadLockState deleted node");
        }
    }

    public void tryReadLockState(String expState, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.ReadLock lock = stateLock.readLock();

        lock.lock();
        localLocks.get().push(lock);

        if (is_deleted.get() || !expState.equals(state)) {
            throw new RuntimeException("ReadLockState deleted node");
        }
    }

    public void tryWriteLockState(String expState, ThreadLocal<Stack<Lock>> localLocks) {
        ReentrantReadWriteLock.WriteLock lock = stateLock.writeLock();

        lock.lock();
        localLocks.get().push(lock);

        if (is_deleted.get() || !expState.equals(state)) {
            throw new RuntimeException("WriteLock deleted");
        }
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AtomicBoolean getDeleted() {
        return is_deleted;
    }

}
