package com.warsheep.scamp.adt;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BSPRectangle implements Container {

    private final boolean DISCARD_BY_RATIO = true;
    private final double H_RATIO = 0.45;
    private final double W_RATIO = 0.45;

    private static Random rnd = new Random();

    private int x, y, width, height;
    private Vector2 center;
    private BSPRectangle leftChild;
    private BSPRectangle rightChild;
    private Room room;

    public BSPRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.center = new Vector2(this.x + this.width / 2, this.y + this.height / 2);
    }

    public BSPRectangle[] split(int timeout) {
        if (rnd.nextBoolean()) {
            // Vertical
            leftChild = new BSPRectangle(x, y, rnd.nextInt(width - 1) + 1, height);
            rightChild = new BSPRectangle(x + leftChild.width, y, width - leftChild.width, height);

            if (DISCARD_BY_RATIO) {
                double r1_w_ratio = (float) leftChild.width / leftChild.height;
                double r2_w_ratio = (float) rightChild.width / rightChild.height;

                if ((r1_w_ratio < W_RATIO || r2_w_ratio < W_RATIO) && timeout > 0) {
                    return split(timeout--);
                }
            }
        } else {
            // Horizontal
            leftChild = new BSPRectangle(x, y, width, rnd.nextInt(height - 1) + 1);
            rightChild = new BSPRectangle(x, y + leftChild.height, width, height - leftChild.height);

            if (DISCARD_BY_RATIO) {
                double r1_h_ratio = (float) leftChild.height / leftChild.width;
                double r2_h_ratio = (float) rightChild.height / rightChild.width;
                if ((r1_h_ratio < H_RATIO || r2_h_ratio < H_RATIO) && timeout > 0) {
                    return split(timeout--);
                }
            }
        }
        BSPRectangle[] children = {leftChild, rightChild};

        return children;
    }

    public BSPRectangle partition(BSPRectangle root, int iterations) {
        if (iterations != 0) {
            BSPRectangle[] leaves = root.split(2);
            root.leftChild = partition(leaves[0], iterations - 1);
            root.rightChild = partition(leaves[1], iterations - 1);
        }
        return root;
    }

    public BSPRectangle getLeftChild() {
        return this.leftChild;
    }

    public BSPRectangle getRightChild() {
        return this.rightChild;
    }

    public List<BSPRectangle> getLeaves() {
        List<BSPRectangle> children = new ArrayList<>();

        BSPRectangle left;
        BSPRectangle right;
        left = this.getLeftChild();
        right = this.getRightChild();
        if (left == null || right == null) {
            children.add(this);
            return children;
        } else {
            children.add(left);
            children.add(right);
            children.addAll(left.getLeaves());
            children.addAll(right.getLeaves());
            return children;
        }
    }

    public List<BSPRectangle> getLevel(int level) {
        List<BSPRectangle> list = new ArrayList<>();
        if (level == 1) {
            list.add(this);
        } else {
            if (this.getLeftChild() != null) {
                list.addAll(this.getLeftChild().getLevel(level - 1));
            }
            if (this.getRightChild() != null) {
                list.addAll(this.getRightChild().getLevel(level - 1));
            }
        }
        return list;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return this.room;
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public Vector2 center() {
        return this.center;
    }
}

