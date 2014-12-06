package com.warsheep.scamp.algorithms;

import java.awt.*;
import java.util.Random;

public class BSPRectangle {

    private final boolean DISCARD_BY_RATIO = true;
    private final double H_RATIO = 0.45;
    private final double W_RATIO = 0.45;

    private static Random rnd = new Random();

    private int x, y, width, height;
    private Point center;
    private BSPRectangle leftChild;
    private BSPRectangle rightChild;

    public BSPRectangle(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.center = new Point(this.x + (this.width/2), this.y + (this.height/2));
    }

    public boolean split() {
        if (rnd.nextBoolean()) {
            // Vertical
            leftChild = new BSPRectangle(x, y, rnd.nextInt(width-1)+1, height);
            rightChild = new BSPRectangle(x + leftChild.width, y, width - leftChild.width, height);

            if (DISCARD_BY_RATIO) {
                double r1_w_ratio = leftChild.width / leftChild.height;
                double r2_w_ratio = rightChild.width / rightChild.height;

                if (r1_w_ratio < W_RATIO || r2_w_ratio < W_RATIO) {
                    return split();
                }
            }
        }
        else {
            // Horizontal
            leftChild = new BSPRectangle(x, y, width, rnd.nextInt(height-1)+1);
            rightChild = new BSPRectangle(x, y + leftChild.height, width, height - leftChild.height);

            if (DISCARD_BY_RATIO) {
                double r1_h_ratio = leftChild.height / leftChild.width;
                double r2_h_ratio = rightChild.height / rightChild.width;
                if (r1_h_ratio < H_RATIO || r2_h_ratio < H_RATIO) {
                    return split();
                }
            }
        }

        return true;
    }

}
