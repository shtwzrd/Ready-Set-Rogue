package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.warsheep.scamp.PrefabFactory;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.components.*;

import java.util.Arrays;

public class VisualEffectProcessor extends IteratingSystem implements StateProcessor.StateListener {
    private final Engine engine;
    private VisualEffectComponent vfx;
    private TransformComponent trans;
    private TileComponent target;
    private ManagedLifetimeComponent life;
    private AnimatableComponent ani;
    private PrefabFactory fab;

    public VisualEffectProcessor(Engine engine) {
        super(Family.all(VisualEffectComponent.class).get());
        this.engine = engine;
        this.fab = new PrefabFactory();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        vfx = ECSMapper.visualEffect.get(entity);
    }

    @Override
    public void spellCasting(Array<StateSignal> casts) {
        for (StateSignal cast : casts) {
            Entity spell = ECSMapper.spellBook.get(cast.entity).lastSpellCast;
            vfx = ECSMapper.visualEffect.get(spell);
            target = ECSMapper.tile.get(cast.entity);
            trans = new TransformComponent();
            life = new ManagedLifetimeComponent();

            trans.position = new Vector3(target.x * 24.0f, target.y * 24.0f, -10);
            Entity e = fab.buildEntity(vfx.file);
            ani = ECSMapper.animatable.get(e);
            life.timeToLive = Arrays.stream(e.getComponent(AnimatableComponent.class).frameTimings).sum();
            switch (vfx.shape) { // TODO: Implement the other shapes
                case SINGLE:
                    e.add(trans);
                    e.add(life);
                    e.add(vfx);
                    this.engine.addEntity(e);
                    break;
                case CIRCLE:
                    break;
                case CIRCLE_EDGE:
                    break;
                case CONE:
                    break;
                case LINEAR:
                    break;
            }
        }

    }
}
