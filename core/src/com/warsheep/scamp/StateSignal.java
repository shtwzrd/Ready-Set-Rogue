package com.warsheep.scamp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.warsheep.scamp.components.StateComponent;

public class StateSignal implements Pool.Poolable {
    public Entity entity;
    public StateComponent.State state = StateComponent.State.IDLE;
    public StateComponent.Directionality direction = StateComponent.Directionality.NONE;

    @Override
    public void reset() {
        entity = null;
        state = null;
        direction = null;
    }

    @Override
    public String toString() {
        return entity.getId() + ": " + state + ", " + direction;
    }
}
