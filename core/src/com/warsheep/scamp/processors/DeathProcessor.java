package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.*;

public class DeathProcessor extends IteratingSystem {

    public DeathProcessor() {
        super(Family.getFor(DamageableComponent.class, StateComponent.class));
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        DamageableComponent dmgComp = ECSMapper.damage.get(entity);
        StateComponent stateComp = ECSMapper.state.get(entity);

        if (dmgComp.healthPoints <= 0) {
            stateComp.state = StateComponent.State.DEAD;

            if (dmgComp.essential) {
                // Lose the game....
            }

            if (ECSMapper.collide.get(entity) != null) {
                entity.remove(CollidableComponent.class);
            }

            if (ECSMapper.control.get(entity) != null) {
                entity.remove(ControllableComponent.class);
            }
        }
    }

}