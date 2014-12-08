package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.TileComponent;
import com.warsheep.scamp.components.TransformComponent;

public class TileProcessor extends EntitySystem implements EntityListener {

    private ImmutableArray<Entity> tileEntities;
    private TransformComponent trans;
    private TileComponent tile;
    private static final int TILE_SIZE = 24;

    public TileProcessor() {

    }

    @Override
    public void addedToEngine(Engine engine) {
        Family family = Family.all(TileComponent.class).get();
        engine.addEntityListener(family, this);

        tileEntities = engine.getEntitiesFor(Family.getFor(TileComponent.class));
    }

    @Override
    public void entityAdded(Entity entity) {
        trans = ECSMapper.transform.get(entity);
        tile = ECSMapper.tile.get(entity);

        trans.position.x = tile.x * TILE_SIZE;
        trans.position.y = tile.y * TILE_SIZE;
        trans.position.z = tile.z;
    }

    @Override
    public void entityRemoved(Entity entity) {
        // Do nothing
    }
}
