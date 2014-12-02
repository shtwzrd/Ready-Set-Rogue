package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.*;

public class AIProcessor extends EntitySystem {

    private ImmutableArray<Entity> aiControllableEntities;
    private ImmutableArray<Entity> damageableEntities;
    private final int MIN_DISTANCE = 15; // TODO: Change it to be a property of AIControllableComponent?

    private int prevSecond = 0;

    public AIProcessor(int order) {
        super(order);
    }

    public void addedToEngine(Engine engine) {
        aiControllableEntities = engine.getEntitiesFor(Family.getFor(AIControllableComponent.class, TilePositionComponent.class, AttackerComponent.class, MovementComponent.class, StateComponent.class));
        damageableEntities = engine.getEntitiesFor(Family.getFor(DamageableComponent.class, TilePositionComponent.class, ControllableComponent.class, StateComponent.class));
    }

    public void update(float deltaTime) {
        int seconds = (int)deltaTime / 1000;
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
                        MovementComponent aiMoveComp = ECSMapper.movement.get(aiEntity);

                        if (minDistance <= 1) { // TODO: Should be attackdistance (property of AttackerComp?)
                            // Attack!!
                            ECSMapper.attack.get(aiEntity).attacking = true;
                            if (Math.abs(closestDmgTilePos.x - aiTilePos.x) > Math.abs(closestDmgTilePos.y - aiTilePos.y)) {
                                // Move sideways
                                if (closestDmgTilePos.x > aiTilePos.x) {
                                    ECSMapper.attack.get(aiEntity).attackerDirection = AttackerComponent.AttackDirection.RIGHT;
                                } else {
                                    ECSMapper.attack.get(aiEntity).attackerDirection = AttackerComponent.AttackDirection.LEFT;
                                }
                            } else {
                                // Move vertically
                                if (closestDmgTilePos.y > aiTilePos.y) {
                                    ECSMapper.attack.get(aiEntity).attackerDirection = AttackerComponent.AttackDirection.UP;
                                } else {
                                    ECSMapper.attack.get(aiEntity).attackerDirection = AttackerComponent.AttackDirection.DOWN;
                                }
                            }
                        }

                        else {
                            // Move!!
                            if (Math.abs(closestDmgTilePos.x - aiTilePos.x) > Math.abs(closestDmgTilePos.y - aiTilePos.y)) {
                                // Move sideways
                                aiTilePos.prevX = aiTilePos.x;
                                if (closestDmgTilePos.x > aiTilePos.x) {
                                    aiTilePos.x++;
                                } else {
                                    aiTilePos.x--;
                                }
                                aiMoveComp.target.x = aiTilePos.x*24;
                            } else {
                                // Move vertically
                                aiTilePos.prevY = aiTilePos.y;
                                if (closestDmgTilePos.y > aiTilePos.y) {
                                    aiTilePos.y++;
                                } else {
                                    aiTilePos.y--;
                                }
                                aiMoveComp.target.y = aiTilePos.y*24;
                            }
                        }
                    }
                }
            }
        }
    }

}
