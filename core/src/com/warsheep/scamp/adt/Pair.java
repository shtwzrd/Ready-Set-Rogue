package com.warsheep.scamp.adt;

import java.io.Serializable;

public class Pair<L, R > implements Serializable {

    private static final long serialVersionUID = 843L;
    private L left;
    private R right;

    public Pair() {
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public void setLeft(L value) {
        this.left = value;
    }

    public void setRight(R value) {
        this.right = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (this.getClass() == obj.getClass()) {
                Pair<?, ?> other = (Pair<?, ?>) obj;
                if (this.left.equals(other.getLeft())
                        && this.right.equals(other.getRight())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.left.hashCode() + this.right.hashCode();
    }

    public String toString() {
        return "[" + this.left.toString() + ", " + this.right.toString() + "]";
    }

}
