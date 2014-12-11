package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class EffectCooldownComponent extends Component {

    public int maxCooldown = 5;
    public int currentCooldown = 5;

}
