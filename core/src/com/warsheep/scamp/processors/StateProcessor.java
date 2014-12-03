package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.State;
import com.warsheep.scamp.components.StateComponent.Directionality;

import java.util.List;

public class StateProcessor extends IteratingSystem {

    private List<StateListener> listeners;

    public static interface StateListener {
        public void idle(Entity entity);

        public void dead(Entity entity);

        public void hurt(Entity entity);

        public void attacking(Entity entity, Directionality direction);

        public void moving(Entity entity, Directionality direction);
    }

    public StateProcessor(List<StateListener> listeners) {
        super(Family.all(StateComponent.class).get());
        this.listeners = listeners;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
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
                case MOVING:
                    for (StateListener listener : this.listeners) {
                        listener.moving(entity, direction);
                    }
                    break;
                case DEAD:
                    for (StateListener listener : this.listeners) {
                        listener.dead(entity);
                    }
                    ECSMapper.state.get(entity).inProgress = false;
                    break;
                case IDLE:
                    for (StateListener listener : this.listeners) {
                        listener.idle(entity);
                    }
                    ECSMapper.state.get(entity).inProgress = false;
                    break;
                case HURT:
                    for (StateListener listener : this.listeners) {
                        listener.hurt(entity);
                    }
                    ECSMapper.state.get(entity).inProgress = false;

                    break;
                default:
                    ECSMapper.state.get(entity).inProgress = false;
                    break;
            }
        }
    }

}
