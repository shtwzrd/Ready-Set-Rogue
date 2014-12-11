package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.components.DamageableComponent;
import com.warsheep.scamp.components.EffectCooldownComponent;
import com.warsheep.scamp.TurnSystem;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.*;
import com.warsheep.scamp.screens.MainGameScreen;

import java.util.*;

public class StateProcessor extends EntitySystem implements TurnSystem {

    private List<StateListener> listeners;
    private ImmutableArray<Entity> statefuls;
    private ImmutableArray<Entity> cooldowners;
    private ImmutableArray<Entity> damageables;
    private float interval;
    private float accumulator = interval;
    private Array<StateSignal> playerQueue;
    private Array<StateSignal> aiQueue;
    private Array<StateSignal> playerMoves;
    private Array<StateSignal> playerAttacks;
    private Array<StateSignal> playerCasts;
    private Array<StateSignal> aiMoves;
    private Array<StateSignal> aiAttacks;
    private Array<StateSignal> aiCasts;
    private static final Array<StateSignal> empty = new Array();
    private MovementActionComparator actionSorter = new MovementActionComparator();
    private static final int TURNS_PER_ROUND = 4;
    private Turn turn = Turn.PLANNING;


    public static interface StateListener {

        // Do nothing defaults
        default public void idle(Entity entity) {
        }

        default public void dead(Entity entity) {
        }

        default public void hurt(Entity entity) {
        }

        default public void attacking(Array<StateSignal> actions) {
        }

        default public void spellCasting(Array<StateSignal> signal) {
        }

        default public void moving(Array<StateSignal> actions) {
        }

        default public Array<StateSignal> aiTurnEnd() {
            return new Array<>();
        }

        default public Array<StateSignal> playerTurnEnd() {
            return new Array<>();
        }
    }

    public StateProcessor(List<StateListener> listeners, float interval) {
        this.listeners = listeners;
        this.interval = interval;
        this.playerQueue = new Array<>();
        this.playerMoves = new Array<>();
        this.playerAttacks = new Array<>();
        this.playerCasts = new Array<>();
        this.aiQueue = new Array<>();
        this.aiMoves = new Array<>();
        this.aiAttacks = new Array<>();
        this.aiCasts = new Array<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        statefuls = engine.getEntitiesFor(Family.all(StateComponent.class).get());
        cooldowners = engine.getEntitiesFor(Family.all(EffectCooldownComponent.class).get());
        damageables = engine.getEntitiesFor(Family.all(DamageableComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        if (turn != Turn.PLANNING) { // triple deincrement if not Planning Turn
            accumulator -= deltaTime * 2;
        }
        accumulator -= deltaTime;
        if (accumulator <= 0) {
            this.updateInterval(turn);
            if (turn == Turn.AI_COMBAT) {
                turn = Turn.PLANNING;
            } else {
                turn = Turn.values()[turn.ordinal() + 1];
            }
            this.accumulator = interval;
        }
    }

    protected void updateInterval(Turn currentTurn) {
        this.updateCooldowns();
        this.updateDamageables();
        switch (currentTurn) {
            case PLANNING:

                break;
            case PLAYER_MOVE:
                pullPlayerActions();
                this.resolveTurn(playerMoves, empty, empty);
                playerMoves = new Array();
                break;
            case AI_MOVE:
                pullAiActions();
                this.resolveTurn(aiMoves, empty, empty);
                aiMoves = new Array();
                break;
            case PLAYER_COMBAT:
                this.resolveTurn(empty, playerAttacks, playerCasts);
                playerAttacks = new Array();
                playerCasts = new Array();
                break;
            case AI_COMBAT:
                this.resolveTurn(empty, aiAttacks, aiCasts);
                aiAttacks = new Array();
                aiCasts = new Array();
                break;
        }
    }

    private void updateDamageables() {
        // Update all damageables
        for (Entity c : this.damageables) {
            DamageableComponent damageable = ECSMapper.damage.get(c);
            if (damageable.shieldOn) {
                damageable.shieldDuration--;
                if (damageable.shieldDuration < 1) {
                    damageable.shieldOn = false;
                }
            }
        }
    }

    private void updateCooldowns() {
        // Update all cooldowns
        for (Entity c : this.cooldowners) {
            EffectCooldownComponent cooldown = ECSMapper.cooldown.get(c);
            if (cooldown.currentCooldown > 0) {
                cooldown.currentCooldown--;
                System.out.println("Cooldown: " + cooldown.currentCooldown + "/" + cooldown.maxCooldown);
            }
        }
    }

    private void pullAiActions() {
        aiQueue = new Array();

        for (StateListener listener : this.listeners) {
            aiQueue.addAll(listener.aiTurnEnd());
        }

        sortActions(aiQueue, aiMoves, aiAttacks, aiCasts);
    }

    private void pullPlayerActions() {
        playerQueue = new Array();

        for (StateListener listener : this.listeners) {
            playerQueue.addAll(listener.playerTurnEnd());
        }

        sortActions(playerQueue, playerMoves, playerAttacks, playerCasts);
    }

    private void sortActions(Array<StateSignal> main,
                             Array<StateSignal> moves,
                             Array<StateSignal> attacks,
                             Array<StateSignal> casts) {

        for (StateSignal action : main) {
            if (action.state == State.MOVING) {
                moves.add(action);
            } else if (action.state == State.ATTACKING) {
                attacks.add(action);
            } else if (action.state == State.CASTING) {
                casts.add(action);
            }
        }

        Sort sort = Sort.instance();
        sort.sort(moves, this.actionSorter);
    }

    private void resolveTurn(Array<StateSignal> moves, Array<StateSignal> attacks, Array<StateSignal> casts) {
        MainGameScreen.moveToPos.x = 0;
        MainGameScreen.moveToPos.y = 0;
        MainGameScreen.attackPos.x = 0;
        MainGameScreen.attackPos.y = 0;

        System.out.println(turn);
        System.out.println(moves);
        System.out.println(attacks);
        System.out.println(casts);
        for (StateListener listener : this.listeners) {
            listener.moving(moves);
            listener.attacking(attacks);
            listener.spellCasting(casts);
        }

        for (Entity stateful : this.statefuls) {
            State state = ECSMapper.state.get(stateful).state;
            switch (state) {
                case DEAD:
                    for (StateListener listener : this.listeners) {
                        listener.dead(stateful);
                    }
                    break;
                case IDLE:
                    for (StateListener listener : this.listeners) {
                        listener.idle(stateful);
                    }
                    break;
                case HURT:
                    for (StateListener listener : this.listeners) {
                        listener.hurt(stateful);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean isPlanningTurn() {
        return turn == Turn.PLANNING;
    }

    @Override
    public float getTurnLength() {
        return interval;
    }

    @Override
    public int getTurnsPerRound() {
        return TURNS_PER_ROUND;
    }

    public void addListener(StateListener listener) {
        this.listeners.add(listener);
    }

    private static class MovementActionComparator implements Comparator<StateSignal> {
        @Override
        public int compare(StateSignal a, StateSignal b) {
            long entityA = a.entity.getId();
            long entityB = b.entity.getId();
            return (int) Math.signum(entityA - entityB);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

    }
}
