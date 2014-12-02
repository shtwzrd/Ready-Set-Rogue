package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.warsheep.scamp.components.AttackerComponent;
import com.warsheep.scamp.components.ControllableComponent;
import com.warsheep.scamp.components.MovementComponent;
import com.warsheep.scamp.components.TilePositionComponent;

import java.awt.*;


public class ControlProcessor extends EntitySystem implements InputProcessor {

    private ImmutableArray<Entity> entities;
    private Point touchStartPosition = new Point();

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.getFor(ControllableComponent.class));
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y++;
                }
                return true;
            case Input.Keys.DOWN:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y--;
                }
                return true;
            case Input.Keys.RIGHT:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x++;
                }
                return true;
            case Input.Keys.LEFT:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x--;
                }
                return true;
            case Input.Keys.W:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y++;
                }
                return true;
            case Input.Keys.S:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y--;
                }
                return true;
            case Input.Keys.D:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x++;
                }
                return true;
            case Input.Keys.A:
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x--;
                }
                return true;

            // Attacking scheme --> To be changed
            case Input.Keys.I:
                for (int i = 0; i < entities.size(); i++) {
                    AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                    attacker.attackerDirection = AttackerComponent.AttackDirection.UP;
                    attacker.attacking = true;

                }
                return true;
            case Input.Keys.K:
                for (int i = 0; i < entities.size(); i++) {
                    AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                    attacker.attackerDirection = AttackerComponent.AttackDirection.DOWN;
                    attacker.attacking = true;
                }
                return true;
            case Input.Keys.L:
                for (int i = 0; i < entities.size(); i++) {
                    AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                    attacker.attackerDirection = AttackerComponent.AttackDirection.RIGHT;
                    attacker.attacking = true;
                }
                return true;
            case Input.Keys.J:
                for (int i = 0; i < entities.size(); i++) {
                    AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                    attacker.attackerDirection = AttackerComponent.AttackDirection.LEFT;
                    attacker.attacking = true;
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
        touchStartPosition.x = screenX;
        touchStartPosition.y = screenY;

        int clickPosX = screenX - Gdx.graphics.getWidth() / 2;
        int clickPosY = screenY - Gdx.graphics.getHeight() / 2;

        if (Math.abs(clickPosX) > Math.abs(clickPosY)) {
            // Move Left or Right
            if (clickPosX > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x++;
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.x -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevX = tilePosCom.x;
                    tilePosCom.x--;
                }
            }
        } else {
            // Move Up or Down
            if (clickPosY > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y -= 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y--;
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    entities.get(i).getComponent(MovementComponent.class).target.y += 24f;
                    TilePositionComponent tilePosCom = entities.get(i).getComponent(TilePositionComponent.class);
                    tilePosCom.prevY = tilePosCom.y;
                    tilePosCom.y++;
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
                        AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                        attacker.attackerDirection = AttackerComponent.AttackDirection.RIGHT;
                        attacker.attacking = true;
                    }
                }
                else {
                    for (int i = 0; i < entities.size(); i++) {
                        AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                        attacker.attackerDirection = AttackerComponent.AttackDirection.LEFT;
                        attacker.attacking = true;
                    }
                }
            } else {
                // Attack up/down
                if (yDiff < 0) {
                    for (int i = 0; i < entities.size(); i++) {
                        AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                        attacker.attackerDirection = AttackerComponent.AttackDirection.UP;
                        attacker.attacking = true;
                    }
                }
                else {
                    for (int i = 0; i < entities.size(); i++) {
                        AttackerComponent attacker = entities.get(i).getComponent(AttackerComponent.class);
                        attacker.attackerDirection = AttackerComponent.AttackDirection.DOWN;
                        attacker.attacking = true;
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
