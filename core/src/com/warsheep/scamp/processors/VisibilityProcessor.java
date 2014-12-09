package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.Scamp;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.TransformComponent;
import com.warsheep.scamp.components.VisibleComponent;

import java.util.Comparator;

public class VisibilityProcessor extends SortedIteratingSystem {

    private OrthographicCamera camera;
    private TransformComponent trans;
    private SpriteBatch batch;
    private AssetDepot assets;

    public VisibilityProcessor() {
        super(Family.all(VisibleComponent.class).get(), new ZComparator());
        this.batch = new SpriteBatch();

        this.camera = new OrthographicCamera(Scamp.V_WIDTH, Scamp.V_HEIGHT);
        this.camera.position.set(Scamp.V_WIDTH / 2, Scamp.V_HEIGHT / 2, 0);
        this.assets = AssetDepot.getInstance();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

        VisibleComponent v = ECSMapper.visible.get(entity);
        if (v.image == null) {
            v.image = assets.fetchImage(v.dir, v.file);
        }
        if (v.color != Color.WHITE) {
            batch.setColor(v.color);
        } else {
            batch.setColor(Color.WHITE);
        }
        // TODO: Transform world coords to screen coords
        trans = ECSMapper.transform.get(entity);
        batch.draw(v.image,
                trans.position.x, trans.position.y,
                v.originX, v.originY,
                v.image.originalWidth, v.image.originalHeight,
                trans.scale.x, trans.scale.y,
                trans.rotation);

    }

    public void startBatch() {
        // Clear buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void endBatch() {
        batch.end();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    private static class ZComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity a, Entity b) {
            float aZ = ECSMapper.transform.get(a).position.z;
            float bZ = ECSMapper.transform.get(b).position.z;
            return (int) Math.signum(bZ - aZ);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

    }
}
