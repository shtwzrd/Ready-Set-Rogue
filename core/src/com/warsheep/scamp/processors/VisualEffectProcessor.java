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
    private AttackerComponent attacker;
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
    public void attacking(Array<StateSignal> attacks) {
        life = new ManagedLifetimeComponent();
        life.timeToLive = 1.5;
        for (StateSignal attack : attacks) {
            attacker = ECSMapper.attack.get(attack.entity);
            target = ECSMapper.tile.get(attack.entity);
            int offsetX = 0;
            int offsetY = 0;
            TransformComponent t;
            for(int i = 1; i < attacker.attackRange + 1; i++) {
                t = new TransformComponent();
                Entity effect = fab.buildEntity("vfx/slash");

                switch(attack.direction) {
                    case UP:
                        offsetY++;
                        break;
                    case DOWN:
                        offsetY--;
                        break;
                    case LEFT:
                        offsetX--;
                        break;
                    case RIGHT:
                        offsetX++;
                        break;
                }
                t.position = new Vector3((target.x + offsetX) * 24.0f, (target.y + offsetY) * 24.0f, -10);
                effect.add(t);
                effect.add(life);
                engine.addEntity(effect);
            }
        }
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
            life.timeToLive = Arrays.stream(e.getComponent(AnimatableComponent.class).frameTimings).sum();
            switch (vfx.shape) { // TODO: Implement the other shapes
                case SINGLE:
                    e.add(trans);
                    e.add(life);
                    e.add(vfx);
                    this.engine.addEntity(e);
                    break;
                case CIRCLE:

                    EffectAreaComponent area = spell.getComponent(EffectAreaComponent.class);
                    int z = -10;
                    for (int i = target.x - area.radius + 1; i < target.x + area.radius; i++) {
                        for (int j = target.y - area.radius + 1; j < target.y + area.radius; j++) {
                            if (i == target.x && j == target.y && !vfx.includesTarget) {
                                // Don't.
                            } else {
                                e = fab.buildEntity(vfx.file);
                                TransformComponent t = new TransformComponent();
                                t.position = new Vector3(i * 24.0f, j * 24.0f, z);
                                e.add(t);
                                e.add(life);
                                e.add(vfx);
                                this.engine.addEntity(e);
                            }
                        }
                    }
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
