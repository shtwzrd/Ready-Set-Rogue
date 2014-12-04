package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.AttackerComponent;
import com.warsheep.scamp.components.DamageableComponent;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.LevelComponent;

public class LevelingProcessor extends IteratingSystem {

    private LevelComponent levelComp;

    public LevelingProcessor() {
        super(Family.all(LevelComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        levelComp = ECSMapper.level.get(entity);

        int currentLevel = currentLevel();

        if (currentLevel > levelComp.level) {
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
        }
    }

    private int currentLevel() {
        return (int) (0.1 * Math.sqrt(levelComp.experiencePoints));
    }

}
