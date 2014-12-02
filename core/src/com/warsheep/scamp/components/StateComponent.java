package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class StateComponent extends Component{

    public State state = State.ALIVE;

    public enum State {
        ALIVE, DEAD, HURT
    }

}
