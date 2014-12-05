package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.Pair;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.*;

import java.util.*;

public class StateProcessor extends EntitySystem {

    private List<StateListener> listeners;
    private ImmutableArray<Entity> statefuls;
    private float interval;
    private float accumulator = 0;


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

        if (deltaTime - accumulator > this.interval) {
            this.accumulator = deltaTime;
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

        for (StateListener listener : this.listeners) {
            actionQueue = listener.turnEnd();

            if (actionQueue != null) {
                Entity e = null;
                Queue<Directionality> moves = new ArrayDeque<>();

                for (Pair<Entity, Pair<State, Directionality>> action : actionQueue) {
                    if (action.getRight().getLeft() == State.MOVING) {
                        e = action.getLeft();
                        moves.offer(action.getRight().getRight());
                        movesMap.put(e, moves);
                    }
                    // What about attacks amirite?
                }
                for (StateListener movers : this.listeners) {
                    for (Map.Entry<Entity, Queue<Directionality>> m : movesMap.entrySet()) {
                        movers.moving(m.getKey(), m.getValue());
                    }
                }

            }
        }
    }

    private void resolveTurn() {
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
