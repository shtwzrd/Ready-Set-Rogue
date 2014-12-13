package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector3;

public class AnimatableComponent extends Component {
    public String title = "";
    public AtlasRegion[] frames = {};
    public double[] frameTimings = {};
    public float[] frameTransformsY = {};
    public float[] frameTransformsX = {};
    public Vector3 additiveTransform = new Vector3();
    public int currentFrameIndex = 0;
    public boolean timeIndexed = true;
    public double playTime;
}
