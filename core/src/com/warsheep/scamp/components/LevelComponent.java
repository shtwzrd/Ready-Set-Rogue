package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class LevelComponent extends Component {

    public int level = 1;
    public int healthOnLevel = 1;
    public int damageOnLevel = 1;
    public int experiencePoints = 100; // Level 1
    public int nextLevelExp = (level+1) * (level+1) * 100;

}
