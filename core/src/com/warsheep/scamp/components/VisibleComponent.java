package com.warsheep.scamp.components;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class VisibleComponent extends Component {
    public TextureAtlas.AtlasRegion image;
    public Color color = Color.WHITE;
    public float originX = 0.0f;
    public float originY = 0.0f;
    public float scale = 1.0f;
    public float rotation = 90.0f;
}
