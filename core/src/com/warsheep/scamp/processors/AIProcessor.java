package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent;

public class AIProcessor extends EntitySystem {

    private ImmutableArray<Entity> aiControllableEntities;
    private ImmutableArray<Entity> damageableEntities;
    private final int MIN_DISTANCE = 15; // TODO: Change it to be a property of AIControllableComponent?

    private int prevSecond = 0;

    public void addedToEngine(Engine engine) {
        aiControllableEntities = engine.getEntitiesFor(Family.getFor(AIControllableComponent.class, TilePositionComponent.class, AttackerComponent.class, StateComponent.class));
        damageableEntities = engine.getEntitiesFor(Family.getFor(DamageableComponent.class, TilePositionComponent.class, ControllableComponent.class, StateComponent.class));
    }

    public void update(float deltaTime) {
        int seconds = (int) deltaTime / 1000;
        if (seconds > prevSecond) {
            super.update(deltaTime);
            prevSecond = seconds;

            for (int i = 0; i < aiControllableEntities.size(); i++) {
                Entity aiEntity = aiControllableEntities.get(i);
                if (ECSMapper.state.get(aiEntity).state != StateComponent.State.DEAD) {
                    TilePositionComponent aiTilePos = ECSMapper.tilePosition.get(aiEntity);

                    int minDistance = MIN_DISTANCE;

                    Entity closestDamageableEntity = null; // Entity to move towards

                    for (int k = 0; k < damageableEntities.size(); k++) {
                        Entity damageableEntity = damageableEntities.get(k);

                        if (ECSMapper.state.get(damageableEntity).state != StateComponent.State.DEAD) {
                            TilePositionComponent damageableTilePos = ECSMapper.tilePosition.get(damageableEntity);

                            int distanceToAI = Math.abs(aiTilePos.x - damageableTilePos.x) + Math.abs(aiTilePos.y - damageableTilePos.y);

                            if (distanceToAI < minDistance) {
                                minDistance = distanceToAI;
                                closestDamageableEntity = damageableEntity;
                            }
                        }
                    }

                    if (closestDamageableEntity != null) {
                        // Figure out what direction to move in
                        TilePositionComponent closestDmgTilePos = ECSMapper.tilePosition.get(closestDamageableEntity);

                        if (minDistance <= 1) { // TODO: Should be attackdistance (property of AttackerComp?)
                            // Attack!!
                            ECSMapper.state.get(aiEntity).state = StateComponent.State.ATTACKING;
                            if (Math.abs(closestDmgTilePos.x - aiTilePos.x) > Math.abs(closestDmgTilePos.y - aiTilePos.y)) {
                                // Move sideways
                                if (closestDmgTilePos.x > aiTilePos.x) {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.RIGHT;
                                } else {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.LEFT;
                                }
                            } else {
                                // Move vertically
                                if (closestDmgTilePos.y > aiTilePos.y) {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.UP;
                                } else {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.DOWN;
                                }
                            }
                        } else {
                            // Move!!
                            ECSMapper.state.get(aiEntity).state = StateComponent.State.MOVING;
                            if (Math.abs(closestDmgTilePos.x - aiTilePos.x) > Math.abs(closestDmgTilePos.y - aiTilePos.y)) {
                                // Move sideways
                                if (closestDmgTilePos.x > aiTilePos.x) {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.RIGHT;
                                } else {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.LEFT;
                                }
                            } else {
                                // Move vertically
                                if (closestDmgTilePos.y > aiTilePos.y) {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.UP;
                                } else {
                                    ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.DOWN;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
