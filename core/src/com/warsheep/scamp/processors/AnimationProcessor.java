package com.warsheep.scamp.processors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.components.*;
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

        if (state != null) {
            animateStateful(tex, anim, state, deltaTime);
        }

        VisualEffectComponent vfx = ECSMapper.visualEffect.get(entity);
        if (vfx != null) {
            animateVfx(tex, anim, vfx, deltaTime);
        }

        anim.playTime += deltaTime;
    }

    private void animateVfx(VisibleComponent tex, AnimatableComponent anim, VisualEffectComponent vfx, float deltaTime) {
        if (anim.frames.length == 0 || anim.frames == null) {
            anim.frames = new TextureAtlas.AtlasRegion[anim.frameTimings.length];
            for (int i = 0; i < anim.frameTimings.length; i++) {
                anim.frames[i] = assets.fetchImage(tex.dir, tex.file, i + 1);
            }
        }

        if (anim.timeIndexed) {
            animateTimeIndexed(anim, tex);
        }

    }

    private void animateStateful(VisibleComponent tex, AnimatableComponent anim, StateComponent state,
                                 float deltaTime) {
        if (anim.frames.length == 0) {
            initializeDefaultWalk(tex, anim);
        }

        if (anim.timeIndexed) {
            animateTimeIndexed(anim, tex);
        }
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

        state.time += deltaTime;
    }

    private void initializeDefaultWalk(VisibleComponent tex, AnimatableComponent anim) {
        TextureAtlas.AtlasRegion oneStep = assets.fetchImage(tex.dir, tex.file);
        TextureAtlas.AtlasRegion twoStep = assets.fetchImage(tex.dir, tex.file, 2);
        float[] frameTrans = {12, 12};
        anim.frames = new TextureAtlas.AtlasRegion[2];
        anim.frames[0] = oneStep;
        anim.frames[1] = twoStep;
        anim.frameTransformsX = frameTrans;
        anim.frameTransformsY = frameTrans;
        anim.title = "Walk";
        anim.timeIndexed = true; // TODO: don't be timeIndexed; use transforms for walking
        double[] frameTimings = {1f, 1f};
        anim.frameTimings = frameTimings;
    }

    private void animateTimeIndexed(AnimatableComponent anim, VisibleComponent tex) {

        tex.image = anim.frames[anim.currentFrameIndex];
        if (anim.playTime >= anim.frameTimings[anim.currentFrameIndex]) {
            if (anim.currentFrameIndex < anim.frames.length - 1) {
                anim.currentFrameIndex++;

            } else {
                anim.currentFrameIndex = 0;
            }
            anim.playTime = 0;
        }
    }
}
