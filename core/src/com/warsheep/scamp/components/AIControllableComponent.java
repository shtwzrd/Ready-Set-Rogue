package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class AIControllableComponent extends Component {

    public Behavior behavior = Behavior.AGGRESSIVE;

    public enum Behavior {
        AGGRESSIVE
    }

}
