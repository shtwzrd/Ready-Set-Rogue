package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.components.*;

public class CollisionProcessor extends EntitySystem {

    private ImmutableArray<Entity> controllableEntities;
    private ImmutableArray<Entity> colliderEntities;

    public CollisionProcessor(int order) {
        super(order);
    }

    public void addedToEngine(Engine engine) {
        controllableEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TilePositionComponent.class, MovementComponent.class));
        colliderEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TilePositionComponent.class));
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < controllableEntities.size(); i++) {
            Entity entityMain = controllableEntities.get(i);
            MovementComponent m = ECSMapper.movement.get(entityMain);
            TilePositionComponent tilePosMain = ECSMapper.tilePosition.get(entityMain);

            for (int k = 0; k < colliderEntities.size(); k++) {
                Entity entityCheck = colliderEntities.get(k);
                TilePositionComponent tilePosCheck = ECSMapper.tilePosition.get(entityCheck);

                if (entityMain.getId() != entityCheck.getId()) {
                    if (tilePosMain.x == tilePosCheck.x && tilePosMain.y == tilePosCheck.y) {
                        System.out.println("Block");

                        System.out.println(tilePosMain.x + " " + tilePosMain.y);
                        System.out.println(tilePosCheck.x + " " + tilePosCheck.y);

                        tilePosMain.x = tilePosMain.prevX;
                        tilePosMain.y = tilePosMain.prevY;
                        m.target = new Vector3(tilePosMain.x*24, tilePosMain.y*24, m.target.z);
                    }
                }
            }
        }
    }
}

