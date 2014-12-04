package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.State;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.screens.MainGameScreen;

import java.awt.*;


public class ControlProcessor extends EntitySystem implements InputProcessor {

    private ImmutableArray<Entity> entities;
    private Point touchStartPosition = new Point();

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControllableComponent.class).get());
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.UP;
                }
                return true;
            case Input.Keys.DOWN:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.DOWN;
                }
                return true;
            case Input.Keys.RIGHT:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.RIGHT;
                }
                return true;
            case Input.Keys.LEFT:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.LEFT;
                }
                return true;
            case Input.Keys.W:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.UP;
                }
                return true;
            case Input.Keys.S:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.DOWN;
                }
                return true;
            case Input.Keys.D:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.RIGHT;
                }
                return true;
            case Input.Keys.A:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.LEFT;
                }
                return true;

            // Attacking scheme --> To be changed
            case Input.Keys.I:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.UP;

                }
                return true;
            case Input.Keys.K:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.DOWN;
                }
                return true;
            case Input.Keys.L:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.RIGHT;
                }
                return true;
            case Input.Keys.J:
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.LEFT;
                }
                return true;
            case Input.Keys.R:
                MainGameScreen.gameState = MainGameScreen.GameState.GAME_OVER;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchStartPosition.x = screenX;
        touchStartPosition.y = screenY;

        int clickPosX = screenX - Gdx.graphics.getWidth() / 2;
        int clickPosY = screenY - Gdx.graphics.getHeight() / 2;

        if (Math.abs(clickPosX) > Math.abs(clickPosY)) {
            // Move Left or Right
            if (clickPosX > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.RIGHT;
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.LEFT;
                }
            }
        } else {
            // Move Up or Down
            if (clickPosY > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.DOWN;
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.MOVING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.UP;
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int xDiff = screenX - touchStartPosition.x;
        int yDiff = screenY - touchStartPosition.y;

        if (Math.abs(xDiff) > 50 || Math.abs(yDiff) > 50) {
            if (Math.abs(xDiff) > Math.abs(yDiff)) {
                // Attack sideways
                if (xDiff > 0) {
                    for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.RIGHT;
                    }
                } else {
                    for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.LEFT;
                    }
                }
            } else {
                // Attack up/down
                if (yDiff < 0) {
                    for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.UP;
                    }
                } else {
                    for (int i = 0; i < entities.size(); i++) {
                    ECSMapper.state.get(entities.get(i)).state = State.ATTACKING;
                    ECSMapper.state.get(entities.get(i)).direction = Directionality.DOWN;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
