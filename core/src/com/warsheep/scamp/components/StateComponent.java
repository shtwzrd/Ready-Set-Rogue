package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class StateComponent extends Component{

    public State state = State.IDLE;
    public Directionality direction = Directionality.NONE;

    public float time = 0.0f;

    public enum State {
        IDLE,
        DEAD,
        HURT,
        MOVING,
        ATTACKING,
    }

    public enum Directionality {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE
    }

}
