package com.warsheep.scamp.adt;

import com.badlogic.gdx.math.Vector2;

public class Room implements Container {
    private int x;
    private int y;
    private int containerWidth;
    private int containerHeight;

    private int xOffset = 2;
    private int yOffset = 2;
    private int containerX;
    private int containerY;

    public Room(Container container) {
        this.x = container.x();
        this.y = container.y();
        this.containerHeight = container.height();
        this.containerWidth = container.width();
        this.containerX = container.x();
        this.containerY = container.y();
    }

    public void setXOffset(int offset) {
        this.xOffset = offset;
    }

    public void setYOffset(int offset) {
        this.yOffset = offset;
    }

    @Override
    public int x() {
        return x + xOffset;
    }

    @Override
    public int y() {
        return y + yOffset;
    }

    @Override
    public int width() {
        return this.containerWidth - (this.x() - this.containerX) - xOffset;
    }

    @Override
    public int height() {
        return this.containerWidth - (this.y() - this.containerY - yOffset);
    }

    @Override
    public Vector2 center() {
        return new Vector2(this.width() / 2, this.height() / 2);
    }

}
