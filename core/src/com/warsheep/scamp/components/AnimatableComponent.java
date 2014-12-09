package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;

public class AnimatableComponent extends Component {
    public IntMap<Animation> animations = new IntMap<>();
}
