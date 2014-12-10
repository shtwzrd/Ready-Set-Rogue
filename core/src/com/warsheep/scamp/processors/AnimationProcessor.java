package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.components.AnimatableComponent;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.StateComponent;
import com.warsheep.scamp.components.StateComponent.Directionality;
import com.warsheep.scamp.components.VisibleComponent;

public class AnimationProcessor extends IteratingSystem implements StateProcessor.StateListener {
    private AssetDepot assets = AssetDepot.getInstance();

    public AnimationProcessor() {
        super(Family.all(AnimatableComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VisibleComponent tex = ECSMapper.visible.get(entity);
        AnimatableComponent anim = ECSMapper.animatable.get(entity);
        StateComponent state = ECSMapper.state.get(entity);
        if (anim.animations.size == 0) {
            for (int i = 0; i < StateComponent.State.values().length; i++) {
                TextureAtlas.AtlasRegion oneStep = assets.fetchImage(tex.dir, tex.file);
                TextureAtlas.AtlasRegion twoStep = assets.fetchImage(tex.dir, tex.file, 2);
                TextureAtlas.AtlasRegion[] frames = new TextureAtlas.AtlasRegion[2];
                frames[0] = oneStep;
                frames[1] = twoStep;
                Animation ani = new Animation(.7f, frames);
                ani.setPlayMode(Animation.PlayMode.LOOP);
                anim.animations.put(StateComponent.State.values()[i].ordinal(), ani);
            }

        }
        Animation animation = anim.animations.get(state.state.ordinal());
        if (animation != null) {
            tex.image = (TextureAtlas.AtlasRegion) animation.getKeyFrame(state.time);
            if (state.direction == Directionality.RIGHT) {
                if (!tex.image.isFlipX()) {
                    tex.image.flip(true, false);
                }
            }
            if (state.direction == Directionality.LEFT) {
                if (tex.image.isFlipX()) {
                    tex.image.flip(true, false);
                }
            }
        }
        state.time += deltaTime;
    }
}
