package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.*;

public class CombatProcessor extends EntitySystem {

    private ImmutableArray<Entity> attackerEntities;
    private ImmutableArray<Entity> damageableEntities;

    public CombatProcessor(int order) {
        super(order);
    }

    public void addedToEngine(Engine engine) {
        attackerEntities = engine.getEntitiesFor(Family.getFor(AttackerComponent.class, TilePositionComponent.class));
        damageableEntities = engine.getEntitiesFor(Family.getFor(DamageableComponent.class, TilePositionComponent.class, StateComponent.class));
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < attackerEntities.size(); i++) {
            // Get attacker
            Entity attacker = attackerEntities.get(i);
            AttackerComponent attackerComp = ECSMapper.attack.get(attacker);

            // Check if attacker is currently attacking
            if (attackerComp.attacking) {
                // Get attackers "position"
                TilePositionComponent tilePosAttackerComp = ECSMapper.tilePosition.get(attacker);

                for (int k = 0; k < damageableEntities.size(); k++) {
                    Entity damageable = damageableEntities.get(k);
                    DamageableComponent damageableComponent = ECSMapper.damage.get(damageable);
                    TilePositionComponent tilePosDamageableComp = ECSMapper.tilePosition.get(damageable);
                    if (ECSMapper.state.get(damageable).state == StateComponent.State.ALIVE) {
                        if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.UP) {
                            if (tilePosAttackerComp.x == tilePosDamageableComp.x && tilePosAttackerComp.y+1 == tilePosDamageableComp.y) {
                                System.out.println("HitUp");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        }
                        else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.DOWN) {
                            if (tilePosAttackerComp.x == tilePosDamageableComp.x && tilePosAttackerComp.y-1 == tilePosDamageableComp.y) {
                                System.out.println("HitDown");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        }
                        else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.RIGHT) {
                            if (tilePosAttackerComp.x+1 == tilePosDamageableComp.x && tilePosAttackerComp.y == tilePosDamageableComp.y) {
                                System.out.println("HitRight");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        }
                        else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.LEFT) {
                            if (tilePosAttackerComp.x-1 == tilePosDamageableComp.x && tilePosAttackerComp.y == tilePosDamageableComp.y) {
                                System.out.println("HitLeft");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        }
                    }

                }

                attackerComp.attacking = false;
            }
        }
    }
}
