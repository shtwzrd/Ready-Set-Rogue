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
        state = StateComponent.State.IDLE;
        direction = StateComponent.Directionality.NONE;
    }
}
