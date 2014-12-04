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

    private int prevSecond = 0;

    public void addedToEngine(Engine engine) {
        aiControllableEntities = engine.getEntitiesFor(Family.all(AIControllableComponent.class, TilePositionComponent.class, AttackerComponent.class, StateComponent.class).get());
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TilePositionComponent.class, ControllableComponent.class, StateComponent.class).get());
    }

    public void update(float deltaTime) {
        int seconds = (int) deltaTime / 1000;
        if (seconds > prevSecond) {
            super.update(deltaTime); // Fire once every second
            prevSecond = seconds;

            for (int i = 0; i < aiControllableEntities.size(); i++) {
                Entity aiEntity = aiControllableEntities.get(i);

                if (ECSMapper.state.get(aiEntity).state != StateComponent.State.DEAD) {
                    TilePositionComponent aiTilePos = ECSMapper.tilePosition.get(aiEntity);

                    int sightRange = ECSMapper.aiControllable.get(aiEntity).sightRange;
                    Entity closestDamageableEntity = null; // Entity to move towards

                    // Find the closeste damageable-ctrl-entitiy, if any
                    for (int k = 0; k < damageableEntities.size(); k++) {
                        Entity damageableEntity = damageableEntities.get(k);

                        if (ECSMapper.state.get(damageableEntity).state != StateComponent.State.DEAD) {
                            TilePositionComponent damageableTilePos = ECSMapper.tilePosition.get(damageableEntity);

                            int distanceToAI = Math.abs(aiTilePos.x - damageableTilePos.x) + Math.abs(aiTilePos.y - damageableTilePos.y);

                            if (distanceToAI < sightRange) {
                                sightRange = distanceToAI;
                                closestDamageableEntity = damageableEntity;
                            }
                        }
                    }

                    if (closestDamageableEntity != null) { // If null, no damageable-ctrl-entities nearby
                        TilePositionComponent closestDmgTilePos = ECSMapper.tilePosition.get(closestDamageableEntity);
                        AttackerComponent attackerComponent = ECSMapper.attack.get(aiEntity);

                        // Figure out whether to fire an action horizontally or vertically
                        if (Math.abs(closestDmgTilePos.x - aiTilePos.x) > Math.abs(closestDmgTilePos.y - aiTilePos.y)) {
                            // Horizontal action
                            if (closestDmgTilePos.x > aiTilePos.x) {
                                ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.RIGHT;
                            } else {
                                ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.LEFT;
                            }
                        } else {
                            // Vertical action
                            if (closestDmgTilePos.y > aiTilePos.y) {
                                ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.UP;
                            } else {
                                ECSMapper.state.get(aiEntity).direction = StateComponent.Directionality.DOWN;
                            }
                        }

                        // Figure out whether to attack or move
                        boolean canAttack = false;
                        if (closestDmgTilePos.x - aiTilePos.x == 0) { // Chance for vertical attack?
                            if (Math.abs(closestDmgTilePos.y - aiTilePos.y) <= attackerComponent.attackRange) {
                                canAttack = true;
                            }
                        }
                        else if (closestDmgTilePos.y - aiTilePos.y == 0) { // Chance for horizontal attack?
                            if (Math.abs(closestDmgTilePos.x - aiTilePos.x) <= attackerComponent.attackRange) {
                                canAttack = true;
                            }
                        }

                        // Attack or Move
                        if (canAttack) {
                            ECSMapper.state.get(aiEntity).state = StateComponent.State.ATTACKING;
                        } else {
                            ECSMapper.state.get(aiEntity).state = StateComponent.State.MOVING;
                        }
                    }
                }
            }
        }
    }

}
