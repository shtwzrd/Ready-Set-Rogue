package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class EffectAreaComponent extends Component {

    public int radius = 1; // 1 = only the selected tile, 2 = selected tile + all tiles around it (9 tiles)

}
