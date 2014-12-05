package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.warsheep.scamp.Pair;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.*;

import java.util.*;

public class StateProcessor extends IntervalIteratingSystem {

    private List<StateListener> listeners;

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

        default public Queue<Pair<Long, Pair<State, Directionality>>> turnEnd() {
            return new ArrayDeque<>();
        }
    }

    public StateProcessor(List<StateListener> listeners, float interval) {
        super(Family.all(StateComponent.class).get(), interval);
        this.listeners = listeners;
    }

    @Override
    protected void updateInterval() {
        super.updateInterval();
        Queue<Pair<Long, Pair<State, Directionality>>> actionQueue = new ArrayDeque<>();
        Map<Entity, Queue<Directionality>> movesMap = new HashMap<>();

        for (StateListener listener : this.listeners) {
                actionQueue = listener.turnEnd();

                if (actionQueue != null) {
                    Entity e = null;
                    Queue<Directionality> moves = new ArrayDeque<>();
                    System.out.println(actionQueue.toString());
                    for (Pair<Long, Pair<State, Directionality>> action : actionQueue) {
                        for(int i = 0; i < this.getEntities().size(); i++) {
                            if(this.getEntities().get(i).getId() == action.getLeft()) {
                                e = this.getEntities().get(i);
                            }
                        }
                        if (action.getRight().getLeft() == State.MOVING) {
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

    @Override
    protected void processEntity(Entity entity) {
        State state = ECSMapper.state.get(entity).state;
        boolean inProgress = ECSMapper.state.get(entity).inProgress;
        if (!inProgress) {
            ECSMapper.state.get(entity).inProgress = true;
            Directionality direction = ECSMapper.state.get(entity).direction;
            switch (state) {
                case ATTACKING:
                    for (StateListener listener : this.listeners) {
                        listener.attacking(entity, direction);
                    }
                    break;
                case DEAD:
                    for (StateListener listener : this.listeners) {
                        listener.dead(entity);
                    }
                    break;
                case IDLE:
                    for (StateListener listener : this.listeners) {
                        //                 listener.idle(entity);
                    }
                    break;
                case HURT:
                    for (StateListener listener : this.listeners) {
                        listener.hurt(entity);
                    }
                    break;
                default:
                    System.out.println("Entity " + entity + " was in an unknown state");
                    ECSMapper.state.get(entity).inProgress = false;
                    break;
            }
        }
    }

}
