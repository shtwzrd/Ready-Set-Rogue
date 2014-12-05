package com.warsheep.scamp;

import java.io.Serializable;

public class Pair<L extends Comparable<L>, R extends Comparable<R>>
        implements Comparable<Pair<L, R>>,
        Serializable {

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

    public int compareTo(Pair<L, R> other) {
        return this.left.compareTo(other.getLeft());
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
