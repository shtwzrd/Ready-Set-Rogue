package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.CollidableComponent;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.MovementComponent;
import com.warsheep.scamp.components.TransformComponent;

public class CollisionProcessor extends EntitySystem {

    private ImmutableArray<Entity> ctmEntities;
    private ImmutableArray<Entity> ctEntities;

    public CollisionProcessor(int order) {
        super(order);
    }

    public void addedToEngine(Engine engine) {
        ctmEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TransformComponent.class, MovementComponent.class));
        ctEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TransformComponent.class));
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < ctmEntities.size(); i++) {
            Entity entityMain = ctmEntities.get(i);
            MovementComponent m = ECSMapper.movement.get(entityMain);
            TransformComponent tMain = ECSMapper.transform.get(entityMain);

            for (int k = 0; k < ctEntities.size(); k++) {
                Entity entityCheck = ctEntities.get(k);
                TransformComponent tCheck = ECSMapper.transform.get(entityCheck);

                if (entityMain.getId() != entityCheck.getId()) {
                    if (m.target.equals(tCheck.position)) {
                        m.target = tMain.previousPosition;
                    }
                }
            }
        }
    }
}

