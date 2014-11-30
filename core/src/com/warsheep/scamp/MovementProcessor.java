package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.PositionComponent;
import com.warsheep.scamp.components.VelocityComponent;

public class MovementProcessor extends EntitySystem {
    private ImmutableArray<Entity> entities;

    public MovementProcessor() {}

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.getFor(PositionComponent.class, VelocityComponent.class));
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent position = ECSMapper.position.get(entity);
            VelocityComponent velocity = ECSMapper.velocity.get(entity);

            // Update position each frame while current position != future position
            if(position.currentX <= position.futureX) {
                position.currentX += velocity.x * deltaTime;
            }

            if(position.currentY <= position.futureY) {
                position.currentY += velocity.y * deltaTime;
            }

            if(position.currentX >= position.futureX) {
                position.currentX -= velocity.x * deltaTime;
            }

            if(position.currentY >= position.futureY) {
                position.currentY -= velocity.y * deltaTime;
            }
        }
    }
}
