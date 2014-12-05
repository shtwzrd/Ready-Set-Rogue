package com.warsheep.scamp.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayDeque;
import java.util.Queue;

public class MovementComponent extends Component {

	public Queue<Vector3> target = new ArrayDeque<>();
	public final Interpolation interpolation = Interpolation.linear;
	public float alpha = 0.0f;
	public float timeSinceMove = 0.0f;

}
