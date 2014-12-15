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
import com.warsheep.scamp.TurnSystem;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.State;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.screens.MainGameScreen;

import javax.naming.ldap.Control;
import java.awt.*;
import java.util.ArrayList;

public class ControlProcessor extends EntitySystem implements InputProcessor, StateProcessor.StateListener {

    private ImmutableArray<Entity> entities;
    private Point touchStartPosition = new Point();
    private Array<StateSignal> actions;
    private CollisionProcessor collisions;
    private int simulatedX = 0;
    private int simulatedY = 0;
    private int selectedSpell = 0;
    private boolean hasAttacked = false; // If player can attack more than once, change this to an int-variable inside AttackerComp
    private final Pool<StateSignal> pool = Pools.get(StateSignal.class);
    private final TurnSystem turnSystem;
    private ArrayList<ControlListener> listeners;

    public static interface ControlListener {

        default public void selectMove(StateSignal signal) {
        }

        default public void selectAttack(StateSignal signal) {
        }

        default public void selectSpell(StateSignal signal) {
        }

        default public void alreadyAttacked(StateSignal signal) {
        }

        default public void onCooldown(StateSignal signal) {
        }

        default public void spellLocked(StateSignal signal) {
        }

        default public void movementBlocked(StateSignal signal) {
        }
    }

    public ControlProcessor(TurnSystem turnSystem) {
        this.turnSystem = turnSystem;
        actions = new Array<>();
        this.listeners = new ArrayList<>();
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControllableComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    private void addAction(StateSignal signal) {
        if (turnSystem.isPlanningTurn()) {
            for (Entity entity : this.entities) {
                signal.entity = entity;
                TileComponent tilePos = ECSMapper.tile.get(signal.entity);
                if (signal.state == State.MOVING) {
                    if (ECSMapper.control.get(signal.entity).movesConsumed <= ECSMapper.control.get(signal.entity).movementBonus) {
                        if (collisions.checkMove(tilePos.x + simulatedX,
                                tilePos.y + simulatedY,
                                signal.entity, signal.direction, false)) {
                            System.out.println("Blocked");

                            // Notify Listeners of movementBlocked
                            for (ControlListener listener : listeners) {
                                listener.movementBlocked(signal);
                            }

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

                            for (ControlListener listener : listeners) {
                                listener.selectMove(signal);
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
                        for (ControlListener listener : this.listeners) {
                            listener.selectAttack(signal);
                        }
                        hasAttacked = true;
                    } else {

                        for (ControlListener listener : listeners) {
                            listener.alreadyAttacked(signal);
                        }
                    }
                }

                if (signal.state == State.CASTING) {
                    if (!hasAttacked) {
                        if (tryCastingSpell(selectedSpell)) {
                            actions.add(signal);
                            hasAttacked = true;
                        }
                    } else {
                        for (ControlListener listener : listeners) {
                            listener.alreadyAttacked(signal);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Array<StateSignal> playerTurnEnd() {
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

            // Attacking scheme
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
            case Input.Keys.X:
                for (Entity e : entities) {
                    LevelComponent levelComponent = ECSMapper.level.get(e);
                    if (levelComponent != null) {
                        levelComponent.experiencePoints += 1000;
                    }
                }
                break;

            // Spell casting scheme
            case Input.Keys.NUM_6:
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 0;
                break;
            case Input.Keys.NUM_7:
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 1;
                break;
            case Input.Keys.NUM_8:
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 2;
                break;
            case Input.Keys.NUM_9:
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 3;
                break;
            case Input.Keys.NUM_0:
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 4;
                break;

            // Misc
            case Input.Keys.R:
                MainGameScreen.gameState = MainGameScreen.GameState.GAME_OVER;
                break;
        }

        if (input.state != null) {
            this.addAction(input);
            return true;
        }

        return false;
    }

    private boolean tryCastingSpell(int spellNum) {
        SpellbookComponent spellBook = ECSMapper.spellBook.get(entities.get(0));
        if (spellBook != null) {
            // Check if spell i has been added to spellbook
            if (spellBook.spellbook.size() >= spellNum + 1) { // +1 cause zero-indexed
                // Set lastCastSpell to the spell cast
                Entity lastCastSpell = spellBook.spellbook.get(spellNum);
                StateSignal signal = new StateSignal();
                signal.entity = lastCastSpell;
                if (lastCastSpell != null) {
                    // Check if the spell has a cooldown, if yes, check if current cooldown = 0, else fire
                    EffectCooldownComponent cooldown = ECSMapper.cooldown.get(lastCastSpell);
                    if (cooldown != null) {
                        if (cooldown.currentCooldown == 0) {
                            spellBook.lastSpellCast = lastCastSpell;
                            return true;
                        } else {
                            // Alert Listeners that the spell is on cooldown
                            for (ControlListener listener : this.listeners) {

                                listener.onCooldown(signal);
                            }
                        }
                    } else {
                        spellBook.lastSpellCast = lastCastSpell;
                        for (ControlListener listener : listeners) {
                            listener.selectSpell(signal);
                        }
                        return true;
                    }
                }
            } else {
                for (ControlListener listener : this.listeners) {
                    StateSignal signal = new StateSignal();
                    signal.entity = entities.get(0);
                    listener.spellLocked(signal);
                }
                System.out.println("Spell not unlocked");
            }
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

        System.out.println(clickPosX + " " + clickPosY);

        StateSignal input = pool.obtain();
        if (screenX > Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 7) {
            if (screenY > Gdx.graphics.getHeight() / 7 && screenY <= Gdx.graphics.getHeight() / 7 * 2) {
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 0;
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 3) {
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 1;
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 4) {
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 2;
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 5) {
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 3;
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 6) {
                input.direction = Directionality.NONE;
                input.state = State.CASTING;
                this.selectedSpell = 4;
            }
        } else if (Math.abs(clickPosX) > Math.abs(clickPosY)) {
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

        if (input.state != null) {
            this.addAction(input);
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        int xDiff = screenX - touchStartPosition.x;
        int yDiff = screenY - touchStartPosition.y;


        StateSignal input = new StateSignal();
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
        if (input.state != null) {
            this.addAction(input);
            return true;
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

    public void addListener(ControlListener listener) {
        this.listeners.add(listener);
    }
}
