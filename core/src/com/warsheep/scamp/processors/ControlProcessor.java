package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.warsheep.scamp.adt.Pair;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.State;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.screens.MainGameScreen;

import java.awt.*;
import java.util.*;

public class ControlProcessor extends EntitySystem implements InputProcessor, StateProcessor.StateListener {

    private ImmutableArray<Entity> entities;
    private Point touchStartPosition = new Point();
    private Queue<Pair<State, Directionality>> actions;
    private CollisionProcessor collisions;
    private int simulatedX = 0;
    private int simulatedY = 0;
    private boolean hasAttacked = false; // If player can attack more than once, change this to an int-variable inside AttackerComp

    public ControlProcessor() {
        actions = new ArrayDeque<>();
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(ControllableComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    private void addAction(Entity entity, Pair<State, Directionality> pair) {
        TileComponent tilePos = ECSMapper.tile.get(entity);
        if (pair.getLeft() == State.MOVING) {
            if (ECSMapper.control.get(entity).movesConsumed <= ECSMapper.control.get(entity).movementBonus) {
                if (collisions.checkMove(tilePos.x + simulatedX,
                        tilePos.y + simulatedY,
                        entity, pair.getRight(), false)) {
                    System.out.println("Blocked");
                    // Some visual feedback
                } else {
                    actions.add(pair);
                    switch (pair.getRight()) {
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
                    ECSMapper.control.get(entity).movesConsumed++;
                }
            }
        }
        if (pair.getLeft() == State.ATTACKING) {
            if (!hasAttacked) {
                MainGameScreen.attackPos.x = simulatedX;
                MainGameScreen.attackPos.y = simulatedY;
                switch (pair.getRight()) {
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

                actions.add(pair);
                hasAttacked = true;
            }
        }
        if (pair.getLeft() == State.CASTING) {
            if (!hasAttacked) {
                actions.add(pair);
                hasAttacked = true;
            }
        }
    }

    @Override
    public Queue<Pair<Entity, Pair<State, Directionality>>> turnEnd() {
        Queue<Pair<Entity, Pair<State, Directionality>>> actionQueue = new ArrayDeque<>();
        for (int i = 0; i < entities.size(); i++) {
            for (Pair<State, Directionality> action : this.actions) {
                actionQueue.add(new Pair(entities.get(i), action));
            }
            ECSMapper.control.get(entities.get(i)).movesConsumed = 0;
        }

        while (!this.actions.isEmpty()) {
            this.actions.poll();
        }

        simulatedX = 0;
        simulatedY = 0;
        hasAttacked = false;

        return actionQueue;
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
        switch (keycode) {
            case Input.Keys.UP:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.UP));
                }
                return true;
            case Input.Keys.DOWN:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.DOWN));
                }
                return true;
            case Input.Keys.RIGHT:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.RIGHT));
                }
                return true;
            case Input.Keys.LEFT:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.LEFT));
                }
                return true;
            case Input.Keys.W:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.UP));
                }
                return true;
            case Input.Keys.S:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.DOWN));
                }
                return true;
            case Input.Keys.D:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.RIGHT));
                }
                return true;
            case Input.Keys.A:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.LEFT));
                }
                return true;

            // Attacking scheme
            case Input.Keys.I:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.UP));
                }
                return true;
            case Input.Keys.K:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.DOWN));
                }
                return true;
            case Input.Keys.L:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.RIGHT));
                }
                return true;
            case Input.Keys.J:
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.LEFT));
                }
                return true;

            // Spell casting scheme
            case Input.Keys.NUM_6:
                tryCastingSpell(0);
                return true;
            case Input.Keys.NUM_7:
                tryCastingSpell(1);
                return true;
            case Input.Keys.NUM_8:
                tryCastingSpell(2);
                return true;
            case Input.Keys.NUM_9:
                tryCastingSpell(3);
                return true;
            case Input.Keys.NUM_0:
                tryCastingSpell(4);
                return true;

            // Misc
            case Input.Keys.R:
                MainGameScreen.gameState = MainGameScreen.GameState.GAME_OVER;
                return true;
        }

        return false;
    }

    private void tryCastingSpell(int spellNum) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            SpellbookComponent spellBook = ECSMapper.spellBook.get(e);
            if (spellBook != null) {
                // Check if spell i has been added to spellbook
                if (spellBook.spellbook.size() >= spellNum+1) { // +1 cause zero-indexed
                    // Set lastCastSpell to the spell cast
                    Entity lastCastSpell = spellBook.spellbook.get(spellNum);
                    if (lastCastSpell != null) {
                        // Check if the spell has a cooldown, if yes, check if current cooldown = 0, else fire
                        CooldownComponent cooldown = ECSMapper.cooldown.get(lastCastSpell);
                        if (cooldown != null) {
                            if (cooldown.currentCooldown == 0) {
                                spellBook.lastSpellCast = lastCastSpell;
                                this.addAction(e, new Pair<>(State.CASTING, Directionality.NONE));
                            }
                        } else {
                            spellBook.lastSpellCast = lastCastSpell;
                            this.addAction(e, new Pair<>(State.CASTING, Directionality.NONE));
                        }
                    }
                }
                else {
                    System.out.println("Spell not unlocked");
                }
            }
        }

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

        if (screenX > Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 7) {
            if (screenY > Gdx.graphics.getHeight() / 7 && screenY <= Gdx.graphics.getHeight() / 7 * 2) {
                tryCastingSpell(0);
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 3) {
                tryCastingSpell(1);
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 4) {
                tryCastingSpell(2);
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 5) {
                tryCastingSpell(3);
            } else if (screenY <= Gdx.graphics.getHeight() / 7 * 6) {
                tryCastingSpell(4);
            }
        } else if (Math.abs(clickPosX) > Math.abs(clickPosY)) {
            // Move Left or Right
            if (clickPosX > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.RIGHT));
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.LEFT));
                }
            }
        } else {
            // Move Up or Down
            if (clickPosY > 0) {
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.DOWN));
                }
            } else {
                for (int i = 0; i < entities.size(); i++) {
                    this.addAction(entities.get(i), new Pair<>(State.MOVING, Directionality.UP));
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
                        this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.RIGHT));
                    }
                } else {
                    for (int i = 0; i < entities.size(); i++) {
                        this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.LEFT));
                    }
                }
            } else {
                // Attack up/down
                if (yDiff < 0) {
                    for (int i = 0; i < entities.size(); i++) {
                        this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.UP));
                    }
                } else {
                    for (int i = 0; i < entities.size(); i++) {
                        this.addAction(entities.get(i), new Pair<>(State.ATTACKING, Directionality.DOWN));
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
