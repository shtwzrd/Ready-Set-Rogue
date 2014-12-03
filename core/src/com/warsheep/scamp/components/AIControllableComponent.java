package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class AIControllableComponent extends Component {

    public Behavior behavior = Behavior.AGGRESSIVE;

    public int movementSpeed = 1; // Moves per turn
    public int sightRange = 10;

    public enum Behavior {
        AGGRESSIVE
    }

}
