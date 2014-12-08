package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.adt.Pair;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.components.StateComponent.State;

import java.util.ArrayDeque;
import java.util.Queue;

public class AIProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> aiControllableEntities;
    private ImmutableArray<Entity> damageableEntities;
    private Queue<Pair<Entity, Pair<State, Directionality>>> actions;
    private CollisionProcessor collisions;

    public AIProcessor() {
        this.actions = new ArrayDeque<>();
    }

    public void addedToEngine(Engine engine) {
        aiControllableEntities = engine.getEntitiesFor(Family.all(AIControllableComponent.class, TileComponent.class, AttackerComponent.class, StateComponent.class).get());
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TileComponent.class, ControllableComponent.class, StateComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    @Override
    public Queue<Pair<Entity, Pair<State, Directionality>>> turnEnd() {
        this.actions.removeAll(this.actions); // clear
        for (Entity aiEntity : aiControllableEntities) {

            if (ECSMapper.state.get(aiEntity).state != State.DEAD) {
                TileComponent aiTilePos = ECSMapper.tile.get(aiEntity);

                int sightRange = ECSMapper.aiControllable.get(aiEntity).sightRange;
                Entity closestDamageableEntity = scanForEnemy(aiTilePos, sightRange, this.damageableEntities); // Entity to move towards

                if (closestDamageableEntity != null) { // If null, no damageable-ctrl-entities nearby

                    TileComponent simulatedAiPos = aiTilePos;
                    TileComponent closestDmgTilePos = ECSMapper.tile.get(closestDamageableEntity);
                    AttackerComponent attackerComponent = ECSMapper.attack.get(aiEntity);

                    int moveCount = 0;

                    while (moveCount <= ECSMapper.aiControllable.get(aiEntity).movementBonus &&
                            !isInAttackRange(simulatedAiPos, closestDmgTilePos, attackerComponent.attackRange)) {

                        Directionality direction = approachEnemy(simulatedAiPos, closestDmgTilePos, aiEntity, collisions);

                        if (direction != Directionality.NONE) {
                            Pair<State, Directionality> action =
                                    new Pair<>(State.MOVING, direction);
                            this.actions.add(new Pair(aiEntity, action));
                            simulatedAiPos = simulateAIMovement(simulatedAiPos, direction);
                        }
                        moveCount++;
                    }

                    // Attack if possible
                    if (isInAttackRange(simulatedAiPos, closestDmgTilePos, attackerComponent.attackRange)) {
                        Pair<State, Directionality> action =
                                new Pair<>(State.ATTACKING, approachEnemy(simulatedAiPos, closestDmgTilePos, aiEntity, collisions));
                        this.actions.add(new Pair(aiEntity, action));
                    }
                }
            }
        }
        return this.actions;
    }


    // Find the closest damageable-ctrl-entity, if any
    private static Entity scanForEnemy(TileComponent location, int sightRange, ImmutableArray<Entity> enemies) {
        Entity closestDamageableEntity = null;
        for (Entity damageableEntity : enemies) {

            if (ECSMapper.state.get(damageableEntity).state != State.DEAD) {
                TileComponent damageableTilePos = ECSMapper.tile.get(damageableEntity);

                int distanceToAI = Math.abs(location.x - damageableTilePos.x) + Math.abs(location.y - damageableTilePos.y);

                if (distanceToAI < sightRange) {
                    sightRange = distanceToAI;
                    closestDamageableEntity = damageableEntity;
                }
            }
        }
        return closestDamageableEntity;
    }

    // Figure out whether to fire an action horizontally or vertically
    private static Directionality approachEnemy(TileComponent ai, TileComponent enemy, Entity entity, CollisionProcessor collisions) {
        boolean[] blocked = new boolean[4];
        boolean wantsUp = false;
        boolean wantsRight = false;
        for (int i = 0; i < Directionality.values().length - 1; i++) {
            blocked[i] = collisions.checkMove(ai.x, ai.y, entity, Directionality.values()[i], false);
        }

        if (enemy.x > ai.x) {
            wantsRight = true;
        }
        if (enemy.y > ai.y) {
            wantsUp = true;
        }

        if (Math.abs(enemy.x - ai.x) > Math.abs(enemy.y - ai.y)) {
            if (wantsRight) {
                if (!blocked[Directionality.RIGHT.ordinal()]) {
                    return Directionality.RIGHT;
                }
            } else {
                if (!blocked[Directionality.LEFT.ordinal()]) {
                    return Directionality.LEFT;
                }
            }
        }
        if (wantsUp) {
            if (!blocked[Directionality.UP.ordinal()]) {
                return Directionality.UP;
            }
        } else {
            if (!blocked[Directionality.DOWN.ordinal()]) {
                return Directionality.DOWN;
            }
        }
        return Directionality.NONE;
    }

    private static boolean isInAttackRange(TileComponent ai, TileComponent enemy, int reach) {
        boolean canAttack = false;
        if (enemy.x - ai.x == 0) { // Chance for vertical attack?
            if (Math.abs(enemy.y - ai.y) <= reach) {
                canAttack = true;
            }
        } else if (enemy.y - ai.y == 0) { // Chance for horizontal attack?
            if (Math.abs(enemy.x - ai.x) <= reach) {
                canAttack = true;
            }
        }

        return canAttack;
    }

    private static TileComponent simulateAIMovement(TileComponent aiPos, Directionality dir) {
        TileComponent t = new TileComponent();
        t.x = aiPos.x;
        t.y = aiPos.y;
        switch (dir) {
            case UP:
                t.y = aiPos.y + 1;
                break;
            case DOWN:
                t.y = aiPos.y - 1;
                break;
            case LEFT:
                t.x = aiPos.x - 1;
                break;
            case RIGHT:
                t.x = aiPos.x + 1;
                break;
        }
        return t;
    }


}
