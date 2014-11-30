package com.warsheep.scamp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.MovementComponent;
import com.warsheep.scamp.components.TransformComponent;

public class MovementProcessor extends IteratingSystem {
    private boolean pause = false;
    public static final float MOVE_SPEED = 16.0f; // TODO: Externalize this (maybe a property of the Component?)

    public MovementProcessor() {
        super(Family.getFor(TransformComponent.class, MovementComponent.class));
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

        TransformComponent trans = ECSMapper.transform.get(entity);
        MovementComponent mov = ECSMapper.movement.get(entity);

        // Don't do anything if we're already at our target
        if(trans.position.x != mov.target.x || trans.position.y != mov.target.y) {
            mov.timeSinceMove += deltaTime; // Update how long we've been moving ...
            mov.alpha += MOVE_SPEED / mov.timeSinceMove; // ... And how far we've come

            trans.position.interpolate(mov.target, mov.alpha, mov.interpolation);
        }
    }

    @Override
    public boolean checkProcessing() {
        return !pause;
    }

    public void pause(boolean pause) {
        this.pause = pause;
    }

}
