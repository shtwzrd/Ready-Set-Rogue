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
        attackerEntities = engine.getEntitiesFor(Family.getFor(AttackerComponent.class, TransformComponent.class));
        damageableEntities = engine.getEntitiesFor(Family.getFor(DamageableComponent.class, TransformComponent.class));
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i < attackerEntities.size(); i++) {
            // Get attacker
            Entity attacker = attackerEntities.get(i);
            AttackerComponent attackerComp = ECSMapper.attack.get(attacker);

            // Check if attacker is currently attacking
            if (attackerComp.attacking) {
                System.out.println("Attacker");
                // Get attackers "position"
                AttackerComponent transAttackerComp = ECSMapper.attack.get(attacker);

                // Check if attacker is currently attacking
                for (int k = 0; k < damageableEntities.size(); k++) {
                    Entity damageable = damageableEntities.get(k);
                    DamageableComponent damageableComponent = ECSMapper.damage.get(damageable);
                    TransformComponent transDamageableComp = ECSMapper.transform.get(damageable);

                    if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.UP) {
                        // Check if hit using Tile positions
                        damageableComponent.healthPoints--;
                        System.out.println("Hit!! <");
                    }
                    else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.DOWN) {

                    }
                    else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.RIGHT) {

                    }
                    else if (attackerComp.attackerDirection == AttackerComponent.AttackDirection.LEFT) {

                    }


                }

                attackerComp.attacking = false;
            }
        }
    }
}
