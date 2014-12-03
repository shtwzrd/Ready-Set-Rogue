package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.warsheep.scamp.components.*;

public class DeathProcessor extends IteratingSystem {

    public DeathProcessor() {
        super(Family.all(DamageableComponent.class, StateComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        DamageableComponent dmgComp = ECSMapper.damage.get(entity);
        StateComponent stateComp = ECSMapper.state.get(entity);

        if (dmgComp.healthPoints <= 0) {
            stateComp.state = StateComponent.State.DEAD;
            VisibleComponent vc = ECSMapper.visible.get(entity);
            vc.color = Color.RED;

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
