package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.ManagedLifetimeComponent;

public class LifetimeProcessor extends IteratingSystem {
    private ManagedLifetimeComponent life;
    private Engine engine;

    public LifetimeProcessor(Engine engine) {
        super(Family.all(ManagedLifetimeComponent.class).get());

        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        life = ECSMapper.managedLifetime.get(entity);
        life.timer += deltaTime;
        if (life.timer >= life.timeToLive) {
            this.engine.removeEntity(entity);
        }

    }
}
