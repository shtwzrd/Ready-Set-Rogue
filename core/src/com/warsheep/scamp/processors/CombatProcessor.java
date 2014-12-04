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

    public void addedToEngine(Engine engine) {
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TilePositionComponent.class, FactionComponent.class, StateComponent.class).get());
    }

    @Override
    public void attacking(Entity entity, StateComponent.Directionality direction) {
        Entity attacker = entity;
        AttackerComponent attackerComp = ECSMapper.attack.get(attacker);
        StateComponent state = ECSMapper.state.get(attacker);

        // Get attackers "position"
        TilePositionComponent tilePosAttackerComp = ECSMapper.tilePosition.get(attacker);
        if (state.state != StateComponent.State.DEAD) {
            for (int k = 0; k < damageableEntities.size(); k++) {
                Entity damageable = damageableEntities.get(k);

                if (!shareFaction(ECSMapper.faction.get(damageable), ECSMapper.faction.get(attacker))) {
                    DamageableComponent damageableComponent = ECSMapper.damage.get(damageable);
                    TilePositionComponent tilePosDamageableComp = ECSMapper.tilePosition.get(damageable);
                    if (ECSMapper.state.get(damageable).state != StateComponent.State.DEAD) {
                        if (state.direction == StateComponent.Directionality.UP) {
                            if (tilePosAttackerComp.x == tilePosDamageableComp.x &&
                                    (tilePosAttackerComp.y + attackerComp.attackRange >= tilePosDamageableComp.y &&
                                            tilePosAttackerComp.y + 1 <= tilePosDamageableComp.y)) {
                                System.out.println("HitUp");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        } else if (state.direction == StateComponent.Directionality.DOWN) {
                            if (tilePosAttackerComp.x == tilePosDamageableComp.x &&
                                    (tilePosAttackerComp.y - attackerComp.attackRange <= tilePosDamageableComp.y &&
                                            tilePosAttackerComp.y - 1 >= tilePosDamageableComp.y)) {
                                System.out.println("HitDown");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        } else if (state.direction == StateComponent.Directionality.RIGHT) {
                            if (tilePosAttackerComp.y == tilePosDamageableComp.y &&
                                    (tilePosAttackerComp.x + attackerComp.attackRange >= tilePosDamageableComp.x &&
                                            tilePosAttackerComp.x + 1 <= tilePosDamageableComp.x)) {
                                System.out.println("HitRight");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
                            }
                        } else if (state.direction == StateComponent.Directionality.LEFT) {
                            if (tilePosAttackerComp.y == tilePosDamageableComp.y &&
                                    (tilePosAttackerComp.x - attackerComp.attackRange <= tilePosDamageableComp.x &&
                                            tilePosAttackerComp.x - 1 >= tilePosDamageableComp.x)) {
                                System.out.println("HitLeft");
                                damageableComponent.healthPoints -= attackerComp.baseDamage;
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
