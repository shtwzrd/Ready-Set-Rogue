package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class ControllableComponent extends Component {

    // TODO: Make holding down the key work
    public int movementBonus = 1;
    public int movesConsumed = 0;
    boolean holdUp;
    boolean holdDown;
    boolean holdLeft;
    boolean holdRight;

}
