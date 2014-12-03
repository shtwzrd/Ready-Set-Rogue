package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.warsheep.scamp.processors.TileProcessor.TileBound;

public class TileComponent extends Component implements TileBound {

    public int x = 0;
    public int y = 0;
    public int z = 0;

    @Override
    public int x() {
        return x;
    }

    @Override
    public void x(int x) {
        this.x = x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public void y(int y) {
        this.y = y;
    }
}


