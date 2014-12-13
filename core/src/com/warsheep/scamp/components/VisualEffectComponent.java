package com.warsheep.scamp.components;

public class VisualEffectComponent {

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

