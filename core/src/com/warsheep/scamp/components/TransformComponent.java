package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent extends Component {
    public Vector3 position = new Vector3();
	public Vector2 scale = new Vector2(1.0f, 1.0f);
	public float rotation = 90.0f;
}
