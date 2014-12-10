package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
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


    public static interface StateListener {

        // Do nothing defaults
        default public void idle(Entity entity) {
            ECSMapper.state.get(entity).inProgress = false;
        }

        default public void dead(Entity entity) {
            ECSMapper.state.get(entity).inProgress = false;
        }

        default public void hurt(Entity entity) {
            ECSMapper.state.get(entity).inProgress = false;
        }

        default public void attacking(Entity entity, Directionality direction) {
            ECSMapper.state.get(entity).inProgress = false;
        }

        default public void moving(Entity entity, Queue<Directionality> direction) {
            ECSMapper.state.get(entity).inProgress = false;
        }

        default public Queue<Pair<Entity, Pair<State, Directionality>>> turnEnd() {
            return new ArrayDeque<>();
        }
    }

    public StateProcessor(List<StateListener> listeners, float interval) {
        this.listeners = listeners;
        this.interval = interval;
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
        Queue<Pair<Entity, Pair<State, Directionality>>> actionQueue;
        Map<Entity, Queue<Directionality>> movesMap = new HashMap<>();
        Map<Entity, Directionality> attacksMap = new HashMap<>();

        for (StateListener listener : this.listeners) {
            actionQueue = listener.turnEnd();

            if (actionQueue != null) {
                Entity e = null;

                for (Pair<Entity, Pair<State, Directionality>> action : actionQueue) {
                    if (action.getRight().getLeft() == State.MOVING) {
                        e = action.getLeft();
                        if(!movesMap.containsKey(e)) {
                            movesMap.put(e, new ArrayDeque<>());
                        }
                        movesMap.get(e).add(action.getRight().getRight());
                    }
                    if (action.getRight().getLeft() == State.ATTACKING) {
                        e = action.getLeft();
                        attacksMap.put(e, action.getRight().getRight());
                    }
                }
            }
        }
        for (StateListener movers : this.listeners) {
            for (Map.Entry<Entity, Queue<Directionality>> m : movesMap.entrySet()) {
                movers.moving(m.getKey(), m.getValue());
            }
            for (Map.Entry<Entity, Directionality> a : attacksMap.entrySet()) {
                movers.attacking(a.getKey(), a.getValue());
            }
        }

    }

    private void resolveTurn() {
        MainGameScreen.moveToPos.x = 0;
        MainGameScreen.moveToPos.y = 0;
        MainGameScreen.attackPos.x = 0;
        MainGameScreen.attackPos.y = 0;
        for (Entity stateful : this.statefuls) {
            State state = ECSMapper.state.get(stateful).state;
            boolean inProgress = ECSMapper.state.get(stateful).inProgress;
            if (!inProgress) {
                ECSMapper.state.get(stateful).inProgress = true;
                Directionality direction = ECSMapper.state.get(stateful).direction;
                switch (state) {
                    case ATTACKING:
                        for (StateListener listener : this.listeners) {
                            listener.attacking(stateful, direction);
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
    }
}
