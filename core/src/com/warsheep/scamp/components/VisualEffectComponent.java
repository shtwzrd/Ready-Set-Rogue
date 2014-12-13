package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;

public class VisualEffectComponent extends Component {

    public String file;
    public int distance = 0;
    public EffectShape shape = EffectShape.SINGLE;
    public boolean includesTarget = true;

    public enum EffectShape {
        SINGLE,
        LINEAR,
        CONE,
        CIRCLE,
        CIRCLE_EDGE
    }
}

