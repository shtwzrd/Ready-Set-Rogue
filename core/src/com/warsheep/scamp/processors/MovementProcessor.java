package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.StateProcessor.StateListener;

import java.util.ArrayList;
import java.util.Queue;

public class MovementProcessor extends IteratingSystem implements StateListener {
    private boolean pause = false;
    private ArrayList<MovementListener> listeners;
    public static final float MOVE_SPEED = 1.0f;

    public static interface MovementListener {

        default public void tileMove(Entity mover, int oldX, int oldY) {
            // Do nothing
        }

        default public void transformMove(Entity mover) {
            // Do nothing
        }
    }

    public MovementProcessor(ArrayList<MovementListener> listeners) {
        super(Family.all(MovementComponent.class).get());
        this.listeners = listeners;
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
        if (mov.previousTranslation == null) {
            mov.previousTranslation = trans.position;
        }

        if (mov.activeTranslation == null && mov.target.peek() != null) {
            mov.activeTranslation = mov.target.poll();
        }

        if (mov.activeTranslation != null) {
            System.out.println("Our targets: " + mov.target.toString());


            mov.timeSinceMove += deltaTime;

            System.out.println(mov.previousTranslation + " " + mov.activeTranslation);

            trans.position.x = mov.previousTranslation
                    .interpolate(
                            mov.activeTranslation,
                            mov.timeSinceMove / MOVE_SPEED, mov.interpolation).x;
            trans.position.y = mov.previousTranslation
                    .interpolate(
                            mov.activeTranslation,
                            mov.timeSinceMove / MOVE_SPEED, mov.interpolation).y;

            if (Math.abs(trans.position.x - mov.activeTranslation.x) < .2 && Math.abs(trans.position.y - mov.activeTranslation.y) < .2) {
                System.out.println("Do I even happen fuck shit damn");
                trans.position = mov.activeTranslation;
                mov.previousTranslation = mov.activeTranslation;
                trans.position.x += trans.xOffset;
                trans.position.y += trans.yOffset;
                if (mov.target.peek() != null) {
                    mov.activeTranslation = mov.target.poll();
                } else {
                    entity.remove(MovementComponent.class);
                    state.state = StateComponent.State.IDLE;
                }
            }
        } else {
            entity.remove(MovementComponent.class);
            state.state = StateComponent.State.IDLE;
        }
    }


    @Override
    public boolean checkProcessing() {
        return !pause;
    }

    public void pause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public void moving(Array<StateSignal> actions) {
        Array<StateComponent.Directionality> moveQueue = new Array<>();
        Entity currentEntity = null;
        if (actions.size > 0) {
            currentEntity = actions.get(0).entity;
        }
        for (StateSignal action : actions) {
            if (currentEntity.getId() == action.entity.getId()) {
                moveQueue.add(action.direction);
            } else {
                processMoves(currentEntity, moveQueue);
                moveQueue = new Array();
                //Pools.get(StateSignal.class).free(action);
            }
            currentEntity = action.entity;
        }

        if (moveQueue != null && currentEntity != null) {
            processMoves(currentEntity, moveQueue);
        }
    }

    private void processMoves(Entity entity, Array<StateComponent.Directionality> direction) {
        MovementComponent mov = new MovementComponent();
        if (ECSMapper.movement.get(entity) != null) {
            mov = ECSMapper.movement.get(entity);
        }

        StateComponent state = ECSMapper.state.get(entity);
        for (StateComponent.Directionality dir : direction) {
            TileComponent tilePos = ECSMapper.tile.get(entity);
            System.out.print(dir);
            int oldX = tilePos.x;
            int oldY = tilePos.y;
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
            ECSMapper.tile.get(entity).x = x;
            ECSMapper.tile.get(entity).y = y;

            for (MovementListener listener : listeners) {
                listener.tileMove(entity, oldX, oldY);
            }
            if (dir == StateComponent.Directionality.LEFT || dir == StateComponent.Directionality.RIGHT) {
                state.direction = dir;
            }
        }
        entity.add(mov);
    }

    public void listen(MovementListener listener) {
        this.listeners.add(listener);
    }
}
