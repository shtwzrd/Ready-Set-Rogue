package com.warsheep.scamp.components;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * The ECSMapper is a convenient container for all of the ComponentMappers in the game.
 * <p/>
 * ComponentMappers provide O(1) access to their designated ComponentType for the given entity,
 * making them the most efficient way to look up a Component of an Entity.
 */
public class ECSMapper {
    public static final ComponentMapper<VisibleComponent> visible =
            ComponentMapper.getFor(VisibleComponent.class);

    public static final ComponentMapper<TransformComponent> transform =
            ComponentMapper.getFor(TransformComponent.class);

    public static final ComponentMapper<MovementComponent> movement =
            ComponentMapper.getFor(MovementComponent.class);

    public static final ComponentMapper<ControllableComponent> control =
            ComponentMapper.getFor(ControllableComponent.class);

    public static final ComponentMapper<CameraComponent> camera =
            ComponentMapper.getFor(CameraComponent.class);

    public static final ComponentMapper<CollidableComponent> collide =
            ComponentMapper.getFor(CollidableComponent.class);

    public static final ComponentMapper<AttackerComponent> attack =
            ComponentMapper.getFor(AttackerComponent.class);

    public static final ComponentMapper<DamageableComponent> damage =
            ComponentMapper.getFor(DamageableComponent.class);

    public static final ComponentMapper<TileComponent> tile =
            ComponentMapper.getFor(TileComponent.class);

    public static final ComponentMapper<StateComponent> state =
            ComponentMapper.getFor(StateComponent.class);

    public static final ComponentMapper<FactionComponent> faction =
            ComponentMapper.getFor(FactionComponent.class);

    public static final ComponentMapper<LevelComponent> level =
            ComponentMapper.getFor(LevelComponent.class);

    public static final ComponentMapper<InventoryComponent> inventory =
            ComponentMapper.getFor(InventoryComponent.class);

    public static final ComponentMapper<DropComponent> drop =
            ComponentMapper.getFor(DropComponent.class);

    public static final ComponentMapper<AIControllableComponent> aiControllable =
            ComponentMapper.getFor(AIControllableComponent.class);
}

