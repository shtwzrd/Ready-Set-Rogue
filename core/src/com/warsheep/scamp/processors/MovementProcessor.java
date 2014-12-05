package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.StateProcessor.StateListener;

import java.util.Queue;

public class MovementProcessor extends IteratingSystem implements StateListener {
    private boolean pause = false;
    public static final float MOVE_SPEED = 12.0f; // TODO: Externalize this (maybe a property of the Component?)

    public MovementProcessor() {
        super(Family.all(MovementComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TransformComponent trans = ECSMapper.transform.get(entity);
        MovementComponent mov = ECSMapper.movement.get(entity);
        StateComponent state = ECSMapper.state.get(entity);
//        TilePositionComponent tilePos = ECSMapper.tilePosition.get(entity);

/*        // Don't do anything if we're already at our target
        System.out.println(trans.position.y - mov.target.y);
        if ((Math.abs(trans.position.x - mov.target.x) < 5 &&
                Math.abs(trans.position.y - mov.target.y) < 5)
                || (Math.abs(trans.position.x - mov.target.x) > 24 ||
                Math.abs(trans.position.y - mov.target.y) > 24 )) {
            System.out.println("time to stop");
            entity.remove(MovementComponent.class);
            state.inProgress = false;
            state.previousState = state.state;
            state.state = StateComponent.State.IDLE;
        } else {
            mov.timeSinceMove += deltaTime; // Update how long we've been moving ...
            mov.alpha += MOVE_SPEED / mov.timeSinceMove; // ... And how far we've come

            if (mov.alpha > .80) {
                //    mov.alpha = 1.0f;
            }
            trans.position.interpolate(mov.target, mov.alpha, mov.interpolation);

        }
        */
        for(Vector3 pos : mov.target) {
            trans.position.x = pos.x;
            trans.position.y = pos.y;
        }
        entity.remove(MovementComponent.class);
        state.state = StateComponent.State.IDLE;
        state.inProgress = false;
    }


    @Override
    public boolean checkProcessing() {
        return !pause;
    }

    public void pause(boolean pause) {
        this.pause = pause;
    }

    public void moving(Entity entity, Queue<StateComponent.Directionality> direction) {
        MovementComponent mov = new MovementComponent();
        if (ECSMapper.movement.get(entity) != null) {
            mov = ECSMapper.movement.get(entity);
        }

        TilePositionComponent tilePos = ECSMapper.tilePosition.get(entity);

        for(StateComponent.Directionality dir : direction) {
            int x = tilePos.x;
            int y = tilePos.y;

            switch (dir) {
                case UP:
                    y++;
                    break;
                case DOWN:
                    y--;
                    break;
                case LEFT:
                    x--;
                    break;
                case RIGHT:
                    x++;
                    break;
                default:
                    break;
            }
            mov.target.add(new Vector3(x * 24.0f, y * 24.0f, 10.0f));
            ECSMapper.tilePosition.get(entity).x = x;
            ECSMapper.tilePosition.get(entity).y = y;
        }
        entity.add(mov);
    }
}
