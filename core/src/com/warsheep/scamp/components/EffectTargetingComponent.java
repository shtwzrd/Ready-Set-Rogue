package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class EffectTargetingComponent extends Component {

    public Effect effect = Effect.SELF_TARGETING; // Targeting self
    public int range = 5; // Used for HOMING
    public int x;
    public int y;

    public enum Effect {
        SELF_TARGETING, HOMING, SPECIFIC_TARGET
    }

}
