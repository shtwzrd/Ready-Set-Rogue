package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.Pair;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent;

import java.util.ArrayDeque;
import java.util.Queue;

public class AIProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> aiControllableEntities;
    private ImmutableArray<Entity> damageableEntities;
    private Queue<Pair<Entity, Pair<StateComponent.State, StateComponent.Directionality>>> actions;
    private CollisionProcessor collisions;

    public AIProcessor() {
        this.actions = new ArrayDeque<>();
    }

    public void addedToEngine(Engine engine) {
        aiControllableEntities = engine.getEntitiesFor(Family.all(AIControllableComponent.class, TilePositionComponent.class, AttackerComponent.class, StateComponent.class).get());
        damageableEntities = engine.getEntitiesFor(Family.all(DamageableComponent.class, TilePositionComponent.class, ControllableComponent.class, StateComponent.class).get());
        collisions = engine.getSystem(CollisionProcessor.class);
    }

    @Override
    public Queue<Pair<Entity, Pair<StateComponent.State, StateComponent.Directionality>>> turnEnd() {
        this.actions.removeAll(this.actions); // clear
        System.out.println("TURN===============================");
        for (Entity aiEntity : aiControllableEntities) {
            System.out.println("AI Controllable: " + aiEntity.getId());

            if (ECSMapper.state.get(aiEntity).state != StateComponent.State.DEAD) {
                TilePositionComponent aiTilePos = ECSMapper.tilePosition.get(aiEntity);

                int sightRange = ECSMapper.aiControllable.get(aiEntity).sightRange;
                Entity closestDamageableEntity = scanForEnemy(aiTilePos, sightRange); // Entity to move towards


                if (closestDamageableEntity != null) { // If null, no damageable-ctrl-entities nearby

                    TileProcessor.TileBound simulatedAiPos = aiTilePos;
                    TilePositionComponent closestDmgTilePos = ECSMapper.tilePosition.get(closestDamageableEntity);
                    AttackerComponent attackerComponent = ECSMapper.attack.get(aiEntity);

                    int moveCount = 0;

                    while (moveCount <= ECSMapper.aiControllable.get(aiEntity).movementBonus &&
                            !isInAttackRange(simulatedAiPos, closestDmgTilePos, attackerComponent.attackRange)) {
                        System.out.println(simulatedAiPos.x() + ", " + simulatedAiPos.y());

                        StateComponent.Directionality direction = approachEnemy(simulatedAiPos, closestDmgTilePos);

                        Pair<StateComponent.State, StateComponent.Directionality> action =
                                new Pair<>(StateComponent.State.MOVING, direction);
                        this.actions.add(new Pair(aiEntity, action));
                        moveCount++;
                        simulatedAiPos = simulateAIMovement(simulatedAiPos, direction);
                    }

                    // Attack if possible
                    if (isInAttackRange(simulatedAiPos, closestDmgTilePos, attackerComponent.attackRange)) {
                        Pair<StateComponent.State, StateComponent.Directionality> action =
                                new Pair<>(StateComponent.State.ATTACKING, approachEnemy(simulatedAiPos, closestDmgTilePos));
                        this.actions.add(new Pair(aiEntity, action));
                    }
                }
            }
        }
        return this.actions;
    }


    // Find the closest damageable-ctrl-entity, if any
    private Entity scanForEnemy(TilePositionComponent location, int sightRange) {
        Entity closestDamageableEntity = null;
        for (Entity damageableEntity : damageableEntities) {

            if (ECSMapper.state.get(damageableEntity).state != StateComponent.State.DEAD) {
                TilePositionComponent damageableTilePos = ECSMapper.tilePosition.get(damageableEntity);

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
    private StateComponent.Directionality approachEnemy(TileProcessor.TileBound ai, TileProcessor.TileBound enemy) {
        if (Math.abs(enemy.x() - ai.x()) > Math.abs(enemy.y() - ai.y())) {

            // Horizontal action
            if (enemy.x() > ai.x()) {
                return StateComponent.Directionality.RIGHT;
            } else {
                return StateComponent.Directionality.LEFT;
            }
        } else {
            // Vertical action
            if (enemy.y() > ai.y()) {
                return StateComponent.Directionality.UP;
            } else {
                return StateComponent.Directionality.DOWN;
            }
        }
    }

    private boolean isInAttackRange(TileProcessor.TileBound ai, TileProcessor.TileBound enemy, int reach) {
        boolean canAttack = false;
        if (enemy.x() - ai.x() == 0) { // Chance for vertical attack?
            if (Math.abs(enemy.y() - ai.y()) <= reach) {
                canAttack = true;
            }
        } else if (enemy.y() - ai.y() == 0) { // Chance for horizontal attack?
            if (Math.abs(enemy.x() - ai.x()) <= reach) {
                canAttack = true;
            }
        }

        return canAttack;
    }

    private TileProcessor.TileBound simulateAIMovement(TileProcessor.TileBound aiPos, StateComponent.Directionality dir) {
        TilePositionComponent t = new TilePositionComponent();
        t.x = aiPos.x();
        t.y = aiPos.y();
        switch (dir) {
            case UP:
                t.y(aiPos.y() + 1);
                break;
            case DOWN:
                t.y(aiPos.y() - 1);
                break;
            case LEFT:
                t.x(aiPos.x() - 1);
                break;
            case RIGHT:
                t.x(aiPos.x() + 1);
                break;
        }
        return t;
    }


}
