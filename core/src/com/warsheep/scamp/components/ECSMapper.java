package com.warsheep.scamp.components;
import com.badlogic.ashley.core.ComponentMapper;

public class ECSMapper {
    public static final ComponentMapper<VisibleComponent> visible =
            ComponentMapper.getFor(VisibleComponent.class);

    public static final ComponentMapper<PositionComponent> position =
            ComponentMapper.getFor(PositionComponent.class);

    public static final ComponentMapper<VelocityComponent> velocity =
            ComponentMapper.getFor(VelocityComponent.class);

    public static final ComponentMapper<ControllableComponent> control =
            ComponentMapper.getFor(ControllableComponent.class);
}
