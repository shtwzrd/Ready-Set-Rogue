package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.processors.TileProcessor.TileBound;

import java.util.List;

public class CollisionProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> collidableTilePosEntities;
    private ImmutableArray<Entity> collidableTileEntities;
    private List<CollisionListener> listeners;

    public static interface CollisionListener {
        default public void successfulMove(Entity entity, Directionality direction) {
            // Do nothing
        }

        default public void collidedMove(Entity entity, Directionality direction) {
            // Do nothing
        }
    }

    public CollisionProcessor(List<CollisionListener> listeners) {
        this.listeners = listeners;
    }

    public void addedToEngine(Engine engine) {
        collidableTilePosEntities = engine.getEntitiesFor(Family.all(CollidableComponent.class, TilePositionComponent.class).get());
        collidableTileEntities = engine.getEntitiesFor(Family.all(CollidableComponent.class, TileComponent.class).get());
    }

    @Override
    public void moving(Entity entity, StateComponent.Directionality direction) {
        TileBound tilePosMain = ECSMapper.tilePosition.get(entity);
        Directionality dir = ECSMapper.state.get(entity).direction;

        boolean blocked = false;

        // Iterate over all collidable components to check for a collision
        for (int i = 0; i < collidableTileEntities.size() + collidableTilePosEntities.size(); i++) {
            Entity entityCheck;
            TileBound tileCheck;

            if (i < collidableTileEntities.size()) {
                entityCheck = collidableTileEntities.get(i);
                tileCheck = ECSMapper.tile.get(entityCheck);
            } else {
                entityCheck = collidableTilePosEntities.get(i - collidableTileEntities.size());
                tileCheck = ECSMapper.tilePosition.get(entityCheck);
            }

            if (entity.getId() != entityCheck.getId()) {
                if (hasCollision(tilePosMain, tileCheck, dir)) {
                    blocked = true;
                }
            }
        }

        // Notify all Collision Listeners of the result
        for (CollisionListener listener : this.listeners) {
            if (blocked) {
                listener.collidedMove(entity, direction);
            } else {
                listener.successfulMove(entity, direction);
            }
        }
    }

    private boolean hasCollision(TileBound a, TileBound b, Directionality dir) {
        switch (dir) {
            case UP:
                if (a.y() == b.y() && a.y() + 1 == b.y()) {
                    return true;
                }
                break;
            case DOWN:
                if (a.x() == b.x() && a.y() - 1 == b.y()) {
                    return true;
                }
                break;
            case LEFT:
                if (a.x() - 1 == b.x() && a.y() == b.y()) {
                    return true;
                }
                break;
            case RIGHT:
                if (a.x() + 1 == b.x() && a.y() == b.y()) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

}
