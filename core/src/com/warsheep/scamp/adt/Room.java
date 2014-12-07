package com.warsheep.scamp.adt;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Room implements Container {
    private int x;
    private int y;
    private int width;
    private int height;
    private Vector2 center;
    private Random rand;

    public Room(Container container) {
        this.rand = new Random();
        int offsetX = rand.nextInt(container.width() / 3) + 1;
        int offsetY = rand.nextInt(container.height() / 3) + 1;

        this.x = container.x() + offsetX;
        this.y = container.y() + offsetY;

        this.width = container.width() - (offsetX * 2);
        this.height = container.height() - (offsetY * 2);

        this.center = new Vector2(container.x() + container.width() / 2, container.y() + container.height() / 2);

    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
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
