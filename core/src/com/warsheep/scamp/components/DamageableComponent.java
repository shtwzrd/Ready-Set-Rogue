package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class DamageableComponent extends Component {

    public int currentHealth = 5;
    public int maxHealth = 5;
    public boolean essential = false;

}
