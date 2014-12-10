package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.State;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.screens.MainGameScreen;

import java.awt.*;

public class ControlProcessor extends EntitySystem implements InputProcessor, StateProcessor.StateListener {

    private ImmutableArray<Entity> entities;
    private Point touchStartPosition = new Point();
    private Array<StateSignal> actions;
    private CollisionProcessor collisions;
    private int simulatedX = 0;
    private int simulatedY = 0;
    private boolean hasAttacked = false; // If player can attack more than once, change this to an int-variable inside AttackerComp
    private final Pool<StateSignal> pool = Pools.get(StateSignal.class);

    public ControlProcessor() {
        actions = new Array<>();
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControllableComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    private void addAction(StateSignal signal) {
        for(Entity entity : this.entities) {
            signal.entity = entity;
            TileComponent tilePos = ECSMapper.tile.get(signal.entity);
            if (signal.state == State.MOVING) {
                if (ECSMapper.control.get(signal.entity).movesConsumed <= ECSMapper.control.get(signal.entity).movementBonus) {
                    if (collisions.checkMove(tilePos.x + simulatedX,
                            tilePos.y + simulatedY,
                            signal.entity, signal.direction, false)) {
                        System.out.println("Blocked");
                        // Some visual feedback
                    } else {
                        actions.add(signal);
                        switch (signal.direction) {
                            case UP:
                                if (hasAttacked) MainGameScreen.attackPos.y++;
                                simulatedY++;
                                break;
                            case DOWN:
                                if (hasAttacked) MainGameScreen.attackPos.y--;
                                simulatedY--;
                                break;
                            case LEFT:
                                if (hasAttacked) MainGameScreen.attackPos.x--;
                                simulatedX--;
                                break;
                            case RIGHT:
                                if (hasAttacked) MainGameScreen.attackPos.x++;
                                simulatedX++;
                                break;
                        }
                        MainGameScreen.moveToPos.x = simulatedX;
                        MainGameScreen.moveToPos.y = simulatedY;
                        ECSMapper.control.get(signal.entity).movesConsumed++;
                    }
                }
            }
            if (signal.state == State.ATTACKING) {
                if (!hasAttacked) {
                    MainGameScreen.attackPos.x = simulatedX;
                    MainGameScreen.attackPos.y = simulatedY;
                    switch (signal.direction) {
                        case UP:
                            MainGameScreen.attackPos.y++;
                            break;
                        case DOWN:
                            MainGameScreen.attackPos.y--;
                            break;
                        case LEFT:
                            MainGameScreen.attackPos.x--;
                            break;
                        case RIGHT:
                            MainGameScreen.attackPos.x++;
                            break;
                    }

                    actions.add(signal);
                    hasAttacked = true;
                }
            }
        }
    }

    @Override
    public Array<StateSignal> turnEnd() {
        for (int i = 0; i < entities.size(); i++) {
            ECSMapper.control.get(entities.get(i)).movesConsumed = 0;
        }

        simulatedX = 0;
        simulatedY = 0;
        hasAttacked = false;

        Array<StateSignal> out = this.actions;
        this.actions = new Array();
        return out;
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            LevelComponent lvlCmp = ECSMapper.level.get(entity);
            DamageableComponent dmgCmp = ECSMapper.damage.get(entity);
            AttackerComponent atkCmp = ECSMapper.attack.get(entity);

            if (lvlCmp != null) {
                MainGameScreen.level = lvlCmp.level;
                MainGameScreen.currentExp = lvlCmp.experiencePoints;
                MainGameScreen.nextLevelExp = lvlCmp.nextLevelExp;
            }

            if (dmgCmp != null) {
                MainGameScreen.maxHealth = dmgCmp.maxHealth;
                MainGameScreen.currentHealth = dmgCmp.currentHealth;
            }

            if (atkCmp != null) {
                MainGameScreen.damage = atkCmp.baseDamage;
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        StateSignal input = new StateSignal();
        switch (keycode) {
            case Input.Keys.UP:
                input.direction = Directionality.UP;
                input.state = State.MOVING;
                break;
            case Input.Keys.DOWN:
                input.direction = Directionality.DOWN;
                input.state = State.MOVING;
                break;
            case Input.Keys.RIGHT:
                input.direction = Directionality.RIGHT;
                input.state = State.MOVING;
                break;
            case Input.Keys.LEFT:
                input.direction = Directionality.LEFT;
                input.state = State.MOVING;
                break;
            case Input.Keys.W:
                input.direction = Directionality.UP;
                input.state = State.MOVING;
                break;
            case Input.Keys.S:
                input.direction = Directionality.DOWN;
                input.state = State.MOVING;
                break;
            case Input.Keys.D:
                input.direction = Directionality.RIGHT;
                input.state = State.MOVING;
                break;
            case Input.Keys.A:
                input.direction = Directionality.LEFT;
                input.state = State.MOVING;
                break;

            // Attacking scheme --> To be changed
            case Input.Keys.I:
                input.direction = Directionality.UP;
                input.state = State.ATTACKING;
                break;
            case Input.Keys.K:
                input.direction = Directionality.DOWN;
                input.state = State.ATTACKING;
                break;
            case Input.Keys.L:
                input.direction = Directionality.RIGHT;
                input.state = State.ATTACKING;
                break;
            case Input.Keys.J:
                input.direction = Directionality.LEFT;
                input.state = State.ATTACKING;
                break;
            case Input.Keys.R:
                MainGameScreen.gameState = MainGameScreen.GameState.GAME_OVER;
                break;
        }

        if(input.state != State.IDLE) {
            this.addAction(input);
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


        StateSignal input = pool.obtain();
        if (Math.abs(clickPosX) > Math.abs(clickPosY)) {
            // Move Left or Right
            if (clickPosX > 0) {
                input.direction = Directionality.RIGHT;
                input.state = State.MOVING;
            } else {
                input.direction = Directionality.LEFT;
                input.state = State.MOVING;
            }
        } else {
            // Move Up or Down
            if (clickPosY > 0) {
                input.direction = Directionality.DOWN;
                input.state = State.MOVING;
            } else {
                input.direction = Directionality.UP;
                input.state = State.MOVING;
            }
        }

        this.addAction(input);


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int xDiff = screenX - touchStartPosition.x;
        int yDiff = screenY - touchStartPosition.y;


        StateSignal input = pool.obtain();
        if (Math.abs(xDiff) > 50 || Math.abs(yDiff) > 50) {
            if (Math.abs(xDiff) > Math.abs(yDiff)) {
                // Attack sideways
                if (xDiff > 0) {
                input.direction = Directionality.RIGHT;
                input.state = State.ATTACKING;
                } else {
                input.direction = Directionality.LEFT;
                input.state = State.ATTACKING;
                }
            } else {
                // Attack up/down
                if (yDiff < 0) {
                input.direction = Directionality.UP;
                input.state = State.ATTACKING;
                } else {
                input.direction = Directionality.DOWN;
                input.state = State.ATTACKING;
                }
            }
        }
        this.addAction(input);

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
