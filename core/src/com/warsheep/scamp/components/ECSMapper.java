package com.warsheep.scamp.components;
import com.badlogic.ashley.core.ComponentMapper;

public class ECSMapper {
    public static final ComponentMapper<VisibleComponent> visible = ComponentMapper.getFor(VisibleComponent.class);
}
