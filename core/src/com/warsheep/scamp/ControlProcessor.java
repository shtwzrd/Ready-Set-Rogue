package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.warsheep.scamp.components.ControllableComponent;
import com.warsheep.scamp.components.PositionComponent;


public class ControlProcessor extends EntitySystem implements InputProcessor {

    private ImmutableArray<Entity> entities;

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.getFor(ControllableComponent.class));
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureY += 24f;
                }
                return true;
            case Input.Keys.DOWN:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureY -= 24f;
                }
                return true;
            case Input.Keys.RIGHT:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureX += 24f;
                }
                return true;
            case Input.Keys.LEFT:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureX -= 24f;
                }
                return true;
            case Input.Keys.W:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureY += 24f;
                }
                return true;
            case Input.Keys.S:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureY -= 24f;
                }
                return true;
            case Input.Keys.D:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureX += 24f;
                }
                return true;
            case Input.Keys.A:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(PositionComponent.class).futureX -= 24f;
                }
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
