package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.Directionality;

import java.util.List;

public class CollisionProcessor extends EntitySystem implements StateProcessor.StateListener {

    private ImmutableArray<Entity> colliableTilePosEntities;
    private ImmutableArray<Entity> colliableTileEntities;
    private List<CollisionListener> listeners;

    public static interface CollisionListener {
        public void successfulMove(Entity entity, Directionality direction);

        public void collidedMove(Entity entity, Directionality direction);
    }

    public CollisionProcessor(List<CollisionListener> listeners) {
        this.listeners = listeners;
    }

    public void addedToEngine(Engine engine) {
        colliableTilePosEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TilePositionComponent.class));
        colliableTileEntities = engine.getEntitiesFor(Family.getFor(CollidableComponent.class, TileComponent.class));
    }

    public void update(float deltaTime) {
        //super.update(deltaTime);
    }


    @Override
    public void moving(Entity entity, StateComponent.Directionality direction) {
        TilePositionComponent tilePosMain = ECSMapper.tilePosition.get(entity);
        Directionality dir = ECSMapper.state.get(entity).direction;

        boolean blocked = false;
        // TilePos + Collidable
        for (int k = 0; k < colliableTilePosEntities.size(); k++) {
            Entity entityCheck = colliableTilePosEntities.get(k);
            TilePositionComponent tilePosCheck = ECSMapper.tilePosition.get(entityCheck);

            if (entity.getId() != entityCheck.getId()) {
                if (hasCollision(tilePosMain, tilePosCheck, dir)) {
                    for (CollisionListener listener : this.listeners) {
                        listener.collidedMove(entity, direction);
                    }
                    blocked = true;
                }
            }
        }

        // Tile + Collidable
        for (int k = 0; k < colliableTileEntities.size(); k++) {
            Entity entityCheck = colliableTileEntities.get(k);
            TileComponent tileCheck = ECSMapper.tile.get(entityCheck);

            if (entity.getId() != entityCheck.getId()) {
                if (hasCollision(tilePosMain, tileCheck, dir)) {
                    for (CollisionListener listener : this.listeners) {
                        listener.collidedMove(entity, direction);
                    }
                    blocked = true;
                }
            }
        }

        if (!blocked) {
            for (CollisionListener listener : this.listeners) {
                listener.successfulMove(entity, direction);
            }
        } else {
           ECSMapper.state.get(entity).inProgress = false;
        }
    }

    private boolean hasCollision(TilePositionComponent a, TilePositionComponent b, Directionality dir) {
        switch (dir) {
            case UP:
                if (a.x == b.x && a.y + 1 == b.y) {
                    return true;
                }
                break;
            case DOWN:
                if (a.x == b.x && a.y - 1 == b.y) {
                    return true;
                }
                break;
            case LEFT:
                if (a.x - 1 == b.x && a.y == b.y) {
                    return true;
                }
                break;
            case RIGHT:
                if (a.x + 1 == b.x && a.y == b.y) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean hasCollision(TilePositionComponent a, TileComponent b, Directionality dir) {
        switch (dir) {
            case UP:
                if (a.x == b.x && a.y + 1 == b.y) {
                    return true;
                }
                break;
            case DOWN:
                if (a.x == b.x && a.y - 1 == b.y) {
                    return true;
                }
                break;
            case LEFT:
                if (a.x - 1 == b.x && a.y == b.y) {
                    return true;
                }
                break;
            case RIGHT:
                if (a.x + 1 == b.x && a.y == b.y) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

}

