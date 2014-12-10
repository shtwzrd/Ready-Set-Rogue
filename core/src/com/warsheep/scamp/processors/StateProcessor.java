package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Sort;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.adt.Pair;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.*;
import com.warsheep.scamp.screens.MainGameScreen;

import java.util.*;

public class StateProcessor extends EntitySystem {

    private List<StateListener> listeners;
    private ImmutableArray<Entity> statefuls;
    private float interval;
    private float accumulator = interval;
    private Array<StateSignal> actionQueue;
    private Array<StateSignal> moves;
    private Array<StateSignal> attacks;
    private MovementActionComparator actionSorter = new MovementActionComparator();


    public static interface StateListener {

        // Do nothing defaults
        default public void idle(Entity entity) {
        }

        default public void dead(Entity entity) {
        }

        default public void hurt(Entity entity) {
        }

        default public void attacking(Array<StateSignal> actions) {
        }

        default public void moving(Array<StateSignal> actions) {
        }

        default public Array<StateSignal> turnEnd() {
            return new Array<>();
        }
    }

    public StateProcessor(List<StateListener> listeners, float interval) {
        this.listeners = listeners;
        this.interval = interval;
        this.actionQueue = new Array<>();
        this.moves = new Array<>();
        this.attacks = new Array<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        statefuls = engine.getEntitiesFor(Family.all(StateComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        accumulator -= deltaTime;
        if (accumulator <= 0) {
            this.accumulator = interval;
            this.updateInterval();
        }
    }

    protected void updateInterval() {
        this.pullActions();
        this.resolveTurn();
    }

    private void pullActions() {
        moves = new Array();
        attacks = new Array();
        actionQueue = new Array();

        for (StateListener listener : this.listeners) {
            actionQueue.addAll(listener.turnEnd());
        }

        for (StateSignal action : actionQueue) {
            if (action.state == State.MOVING) {
                moves.add(action);
            } else if (action.state == State.ATTACKING) {
                attacks.add(action);
            }
        }

        Sort sort = Sort.instance();
        sort.sort(moves, this.actionSorter);

        for (StateListener movers : this.listeners) {
            movers.moving(moves);
            movers.attacking(attacks);
        }

    }

    private void resolveTurn() {
        MainGameScreen.moveToPos.x = 0;
        MainGameScreen.moveToPos.y = 0;
        MainGameScreen.attackPos.x = 0;
        MainGameScreen.attackPos.y = 0;
        for (Entity stateful : this.statefuls) {
            State state = ECSMapper.state.get(stateful).state;
            switch (state) {
                case ATTACKING:
                    for (StateListener listener : this.listeners) {
                        listener.attacking(attacks);
                    }
                    break;
                case DEAD:
                    for (StateListener listener : this.listeners) {
                        listener.dead(stateful);
                    }
                    break;
                case IDLE:
                    for (StateListener listener : this.listeners) {
                        listener.idle(stateful);
                    }
                    break;
                case HURT:
                    for (StateListener listener : this.listeners) {
                        listener.hurt(stateful);
                    }
                    break;
                default:
                    break;
            }
        }
    }



    private static class MovementActionComparator implements Comparator<StateSignal> {
        @Override
        public int compare(StateSignal a, StateSignal b) {
            long entityA = a.entity.getId();
            long entityB = b.entity.getId();
            return (int) Math.signum(entityA - entityB);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

    }
}
