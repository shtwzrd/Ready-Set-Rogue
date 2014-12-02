package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.*;

public class DeathProcessor extends IteratingSystem {

    public DeathProcessor() {
        super(Family.getFor(DamageableComponent.class, ControllableComponent.class));
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        DamageableComponent dmgComp = ECSMapper.damage.get(entity);

        if (dmgComp.healthPoints <= 0) {
            if (dmgComp.essential) {
                // TODO: Figure out what to do when losing the game
                System.out.println("You lost the game...");
            }
            else {
                // TODO: Implement proper death
                System.out.println("Yo, mob! You deeed!");
            }
        }
    }

}
