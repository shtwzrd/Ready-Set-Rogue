package com.warsheep.scamp.processors;

import com.warsheep.scamp.components.CameraComponent;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.TransformComponent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class CameraProcessor extends IteratingSystem {

    public CameraProcessor() {
        super(Family.all(CameraComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        CameraComponent cam = ECSMapper.camera.get(entity);

        if (cam.target == null) {
            return;
        }

        TransformComponent target = ECSMapper.transform.get(cam.target);

        if (target == null) {
            return;
        }

        cam.camera.position.y = target.position.y + 12;
        cam.camera.position.x = target.position.x + 12;
    }
}
