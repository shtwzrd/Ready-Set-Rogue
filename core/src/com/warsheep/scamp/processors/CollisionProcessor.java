package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.components.StateComponent.Directionality;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CollisionProcessor extends EntitySystem {

    private List<CollisionListener> listeners;
    private TileProcessor tileProcessor;


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
        tileProcessor = engine.getSystem(TileProcessor.class);
    }

    public boolean checkMove(int x, int y, Entity entity, StateComponent.Directionality direction) {
        boolean blocked = false;

        int checkTileX = x;
        int checkTileY = y;

        // Figure out what tile position to check for collision
        switch (direction) {
            case UP:
                checkTileY++;
                break;
            case DOWN:
                checkTileY--;
                break;
            case LEFT:
                checkTileX--;
                break;
            case RIGHT:
                checkTileX++;
                break;
            default:
                break;
        }

        // Get all entities on Tile
        ArrayList<Entity> entitiesInPos = tileProcessor.queryByPosition(checkTileX, checkTileY);

        // Check for collidable component in tile
        if (entitiesInPos != null) {
            for (Entity e : entitiesInPos) {
                if (ECSMapper.collide.get(e) != null) {
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

        return blocked;
    }

}
