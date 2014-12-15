package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.components.*;

import java.util.ArrayList;

public class SpellCastProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> damageableEntities;
    private CollisionProcessor collisions;
    private TileProcessor tileProcessor;
    private Entity casterEntity;
    private Entity spell;
    private EffectHealingComponent healing;
    private EffectShieldingComponent shielding;
    private EffectDamagingComponent damaging;

    public void addedToEngine(Engine engine) {
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TileComponent.class, FactionComponent.class, StateComponent.class).get());
        tileProcessor = engine.getSystem(TileProcessor.class);
    }

    @Override
    public void spellCasting(Array<StateSignal> signals) {
        // Figure out what spell was cast and process it
        for(StateSignal signal : signals) {
            // Get Entity
            casterEntity = signal.entity;
            // Get spellbook from entity
            SpellbookComponent spellbook = ECSMapper.spellBook.get(casterEntity);
            // Get spell that was cast
            spell = spellbook.lastSpellCast;

            if (spell != null) {
                processSpell(spell);
            }
        }
    }

    private void processSpell(Entity spell) {
        // Get faction, target and area for spell
        FactionComponent factionCaster = ECSMapper.faction.get(casterEntity);
        EffectTargetingComponent target = ECSMapper.effectTargeting.get(spell);
        EffectAreaComponent area = ECSMapper.effectArea.get(spell);

        if (area != null && target != null && factionCaster != null) {
            setTargetPosition(target);

            healing = ECSMapper.effectHealing.get(spell);
            shielding = ECSMapper.effectShielding.get(spell);
            damaging = ECSMapper.effectDamaging.get(spell);

            int radius = area.radius;
            int x = target.x;
            int y = target.y;

            // Fire spell effects in area/radius
            int tileTargetX, tileTargetY;
            for (int row = 0; row < radius*2 - 1; row++) {
                tileTargetY = y + row - radius + 1; // targetY

                for (int col = 0; col < radius*2 - 1; col++) {
                    tileTargetX = x + col - radius + 1; // targetX

                    // Get entities at position
                    if (tileTargetX >= 0 && tileTargetY >= 0) {
                        ArrayList<Entity> entitiesAtPos = tileProcessor.queryByPosition(tileTargetX, tileTargetY);
                        if (entitiesAtPos != null) {
                            for (Entity e : entitiesAtPos) {
                                FactionComponent faction = ECSMapper.faction.get(e);
                                if (faction != null && factionCaster != null) {
                                    if (shareFaction(faction, factionCaster)) { // Shares faction ('friend')
                                        if (healing != null) {
                                            heal(healing, e);
                                        }
                                        if (shielding != null) {
                                            shield(shielding, e);
                                        }
                                    }
                                    else { // Do not share faction (enemy)
                                        if (damaging != null) {
                                            damage(damaging, e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Reset cooldown on spell
            EffectCooldownComponent cooldown = ECSMapper.cooldown.get(spell);
            if (cooldown != null) { // Reset cooldown
                cooldown.currentCooldown = cooldown.maxCooldown;
            }
        }
    }

    private boolean shareFaction(FactionComponent fc1, FactionComponent fc2) {
        for (FactionComponent.Faction f : fc1.factions)
            if (fc2.factions.contains(f))
                return true;
        return false;
    }

    private void setTargetPosition(EffectTargetingComponent target) {
        if (target.effect == EffectTargetingComponent.Effect.SPECIFIC_TARGET) {
            // Do nothing, target should have been set elsewhere...
        }

        else {
            TileComponent tile = ECSMapper.tile.get(casterEntity);
            if (tile != null) {
                target.x = tile.x;
                target.y = tile.y;

                if (target.effect == EffectTargetingComponent.Effect.HOMING) {
                    // TODO: Use A* to find the nearest ally/enemy within range and change target.x, target.y to be this
                }
            }
        }
    }

    private void heal(EffectHealingComponent healEffect, Entity entity) {
        DamageableComponent damageable = ECSMapper.damage.get(entity);
        if (damageable != null) {
            for (int i = 1; i <= healEffect.healAmount; i++) { // Heal one at a time to stay inside maxHealth
                if (damageable.currentHealth == damageable.maxHealth) {
                    break;
                } else {
                    damageable.currentHealth++;
                }
            }
        }
    }

    private void shield(EffectShieldingComponent shieldEffect, Entity entity) {
        DamageableComponent damageable = ECSMapper.damage.get(entity);
        if (damageable != null) {
            damageable.shieldOn = true;
            damageable.shieldDuration = shieldEffect.duration;
        }
    }

    private void damage(EffectDamagingComponent damageEffect, Entity entity) {
        StateComponent state = ECSMapper.state.get(entity);
        if (state.state != StateComponent.State.DEAD) {
            DamageableComponent damageable = ECSMapper.damage.get(entity);
            if (damageable != null) {
                damageable.currentHealth -= damageEffect.damage;

                // Check to see if the damageable entity is dead and if it has anything to drop
                DropComponent dropComponent = ECSMapper.drop.get(entity);
                if (damageable.currentHealth <= 0 && dropComponent != null) {

                    // Check to see if exp points can be applied to attacker entity
                    LevelComponent levelComp = ECSMapper.level.get(casterEntity);
                    if (levelComp != null) {
                        levelComp.experiencePoints += dropComponent.experience;
                        if (dropComponent.itemDrop != null) {
                            // TODO: Drop item
                        }
                    }
                }
            }
        }
    }

}
