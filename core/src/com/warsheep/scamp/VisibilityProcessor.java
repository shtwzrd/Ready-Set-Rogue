package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.TransformComponent;
import com.warsheep.scamp.components.VisibleComponent;

public class VisibilityProcessor extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private TransformComponent trans;

    public VisibilityProcessor() {
        batch = new SpriteBatch();
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.getFor(VisibleComponent.class));
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        // Clear buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (int i = 0; i < entities.size(); i++) {
            VisibleComponent v = ECSMapper.visible.get(entities.get(i));
            if (v.color != Color.WHITE) {
                batch.setColor(v.color);
            } else {
                batch.setColor(Color.WHITE);
            }
            // TODO: Transform world coords to screen coords
            trans = ECSMapper.transform.get(entities.get(i));
            batch.draw(v.image,
                    trans.position.x, trans.position.y,
                    v.originX, v.originY,
                    v.image.originalWidth, v.image.originalHeight,
                    trans.scale.x, trans.scale.y,
                    trans.rotation, true);
        }

        batch.end();

    }
}
