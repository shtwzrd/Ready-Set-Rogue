package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.warsheep.scamp.components.*;

public class CombatProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> damageableEntities;
    private CollisionProcessor collisions;

    public void addedToEngine(Engine engine) {
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TilePositionComponent.class, FactionComponent.class, StateComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    @Override
    public void attacking(Entity entity, StateComponent.Directionality direction) {
        Entity attacker = entity;
        AttackerComponent attackerComp = ECSMapper.attack.get(attacker);
        StateComponent state = ECSMapper.state.get(attacker);

        if (state.state != StateComponent.State.DEAD) {
            // Get attackers "position"
            TilePositionComponent attackerTilePos = ECSMapper.tilePosition.get(attacker);

            for (int k = 0; k < damageableEntities.size(); k++) {
                Entity damageable = damageableEntities.get(k);

                if (!shareFaction(ECSMapper.faction.get(damageable), ECSMapper.faction.get(attacker))) {
                    DamageableComponent damageableComponent = ECSMapper.damage.get(damageable);
                    TilePositionComponent damageableTilePos = ECSMapper.tilePosition.get(damageable);

                    if (ECSMapper.state.get(damageable).state != StateComponent.State.DEAD) {
                        boolean attacked = false;
                        for (int i = 0; i < attackerComp.attackRange; i++) {

                        }
                        // Figure out what direction to attack in and apply damage
                        if (direction == StateComponent.Directionality.UP) {
                            for (int i = 0; i < attackerComp.attackRange; i++) {
                                if (collisions.checkMove(attackerTilePos.x, attackerTilePos.y+i, entity, direction)) {
                                    if (attackerTilePos.x == damageableTilePos.x && attackerTilePos.y+i+1 == damageableTilePos.y) {
                                        System.out.println("HitUp");
                                        attacked = true;
                                    }
                                    else {
                                        System.out.println("Wall hit");
                                        break;
                                    }
                                }
                            }
                        } else if (direction == StateComponent.Directionality.DOWN) {
                            for (int i = 0; i < attackerComp.attackRange; i++) {
                                if (collisions.checkMove(attackerTilePos.x, attackerTilePos.y-i, entity, direction)) {
                                    if (attackerTilePos.x == damageableTilePos.x && attackerTilePos.y-i-1 == damageableTilePos.y) {
                                        System.out.println("HitDown");
                                        attacked = true;
                                    }
                                    else {
                                        System.out.println("Wall hit");
                                        break;
                                    }
                                }
                            }
                        } else if (direction == StateComponent.Directionality.RIGHT) {
                            for (int i = 0; i < attackerComp.attackRange; i++) {
                                if (collisions.checkMove(attackerTilePos.x+i, attackerTilePos.y, entity, direction)) {
                                    if (attackerTilePos.x+i+1 == damageableTilePos.x && attackerTilePos.y == damageableTilePos.y) {
                                        System.out.println("HitRight");
                                        attacked = true;
                                    }
                                    else {
                                        System.out.println("Wall hit");
                                        break;
                                    }
                                }
                            }
                        } else if (direction == StateComponent.Directionality.LEFT) {
                            for (int i = 0; i < attackerComp.attackRange; i++) {
                                if (collisions.checkMove(attackerTilePos.x-i, attackerTilePos.y, entity, direction)) {
                                    if (attackerTilePos.x == damageableTilePos.x-i-1 && attackerTilePos.y == damageableTilePos.y) {
                                        System.out.println("HitLeft");
                                        attacked = true;
                                    }
                                    else {
                                        System.out.println("Wall hit");
                                        break;
                                    }
                                }
                            }
                        }

                        // Check if the player actually attacked
                        if (attacked) {
                            // Apply damage
                            damageableComponent.currentHealth -= attackerComp.baseDamage;
                            // Check to see if the damageable entity is dead and if it has anything to drop
                            DropComponent dropComponent = ECSMapper.drop.get(damageable);
                            if (damageableComponent.currentHealth <= 0 && dropComponent != null) {
                                // Check to see if exp points can be applied to attacker entity
                                LevelComponent levelComp = ECSMapper.level.get(entity);
                                if (levelComp != null) {
                                    levelComp.experiencePoints += dropComponent.experienceDrop;
                                    if (dropComponent.itemDrop != null) {
                                        // TODO: Drop item
                                    }
                                }
                            }
                        }

                    }
                }

                state.state = StateComponent.State.IDLE;
                state.inProgress = false;
            }
        }

    }

    private boolean shareFaction(FactionComponent fc1, FactionComponent fc2) {
        for (FactionComponent.Faction f : fc1.factions)
            if (fc2.factions.contains(f))
                return true;
        return false;
    }

    @Override
    public void hurt(Entity entity) {
        for (int i = 0; i < damageableEntities.size(); i++) {
            ECSMapper.visible.get(damageableEntities.get(i)).color = Color.WHITE;

        }
        VisibleComponent vc = ECSMapper.visible.get(entity); // TODO: Temporary
        vc.color = Color.RED;
    }
}
