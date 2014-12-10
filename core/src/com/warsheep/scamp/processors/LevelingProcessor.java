package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.screens.MainGameScreen;

public class LevelingProcessor extends IteratingSystem {

    private LevelComponent levelComp;
    private Entity entity;
    private final int[] levelMilestones = {2, 3, 4, 5, 6};

    public LevelingProcessor() {
        super(Family.all(LevelComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        this.entity = entity;
        levelComp = ECSMapper.level.get(entity);

        int currentLevel = currentLevel();

        if (currentLevel > levelComp.level) {
            levelUp();
        }
    }

    private int currentLevel() {
        return (int) (0.1 * Math.sqrt(levelComp.experiencePoints));
    }

    private void levelUp() {
        levelComp.level++;
        levelComp.nextLevelExp = (levelComp.level+1) * (levelComp.level+1) * 100;

        AttackerComponent atkComp = ECSMapper.attack.get(entity);
        DamageableComponent dmgComp = ECSMapper.damage.get(entity);

        if (atkComp != null) {
            atkComp.baseDamage += levelComp.damageOnLevel;
        }

        if (dmgComp != null) {
            dmgComp.maxHealth += levelComp.healthOnLevel;
            dmgComp.currentHealth = dmgComp.maxHealth;
        }

        if (hitMilestone(levelComp.level)) {
            addSpell();
        }

        resetCooldowns();
    }

    private boolean hitMilestone(int level) {
        for (int i = 0; i < levelMilestones.length; i++) {
            if (levelMilestones[i] == level) {
                return true;
            }
        }
        return false;
    }

    private void addSpell() {
        // Cooldown spell
        Entity cooldownSpell = new Entity();
        cooldownSpell.add(new CooldownComponent());
        MainGameScreen.ecs.addEntity(cooldownSpell);

        // Add cooldown spell to player
        SpellbookComponent spellbook = ECSMapper.spellBook.get(entity);
        spellbook.spellbook.add(cooldownSpell);
    }

    private void resetCooldowns() {
        SpellbookComponent spellbook = ECSMapper.spellBook.get(entity);
        for (Entity spell : spellbook.spellbook) {
            CooldownComponent cooldownComponent = ECSMapper.cooldown.get(spell);
            if (cooldownComponent != null) {
                cooldownComponent.currentCooldown = 0;
            }
        }
    }

}
