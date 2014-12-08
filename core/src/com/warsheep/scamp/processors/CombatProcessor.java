package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.warsheep.scamp.components.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CombatProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> damageableEntities;
    private CollisionProcessor collisions;
    private TileProcessor tileProcessor;

    public void addedToEngine(Engine engine) {
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TileComponent.class, FactionComponent.class, StateComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
        tileProcessor = engine.getSystem(TileProcessor.class);
    }

    @Override
    public void attacking(Entity entity, StateComponent.Directionality direction) {
        Entity attacker = entity;
        AttackerComponent atkComp = ECSMapper.attack.get(attacker);
        StateComponent atkState = ECSMapper.state.get(attacker);

        if (atkState.state != StateComponent.State.DEAD) {
            // Get attackers "position"
            TileComponent atkPos = ECSMapper.tile.get(attacker);

            int checkPosX = atkPos.x;
            int checkPosY = atkPos.y;

            ArrayList<Entity> entitiesInPos = tileProcessor.queryByPosition(checkPosX, checkPosY);

            if (entitiesInPos != null) {
                for (Entity damageable : entitiesInPos) {
                    DamageableComponent dmgComp = ECSMapper.damage.get(damageable);
                    if (dmgComp != null) {
                        // Apply damage
                        dmgComp.currentHealth -= atkComp.baseDamage;
                        // Check to see if the damageable entity is dead and if it has anything to drop
                        DropComponent dropComponent = ECSMapper.drop.get(damageable);
                        if (dmgComp.currentHealth <= 0 && dropComponent != null) {
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

            switch (direction) {
                case UP:
                    checkPosY++;
                    break;
                case DOWN:
                    checkPosY--;
                    break;
                case RIGHT:
                    checkPosX++;
                    break;
                case LEFT:
                    checkPosX--;
                    break;
            }

            atkState.state = StateComponent.State.IDLE;
            atkState.inProgress = false;
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
