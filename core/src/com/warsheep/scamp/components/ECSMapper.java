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

    public static final ComponentMapper<AnimatableComponent> animatable =
            ComponentMapper.getFor(AnimatableComponent.class);

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

    public static final ComponentMapper<SpellbookComponent> spellBook =
            ComponentMapper.getFor(SpellbookComponent.class);

    public static final ComponentMapper<EffectCooldownComponent> cooldown =
            ComponentMapper.getFor(EffectCooldownComponent.class);

    public static final ComponentMapper<EffectAreaComponent> effectArea =
            ComponentMapper.getFor(EffectAreaComponent.class);

    public static final ComponentMapper<EffectDamagingComponent> effectDamaging =
            ComponentMapper.getFor(EffectDamagingComponent.class);

    public static final ComponentMapper<EffectHealingComponent> effectHealing =
            ComponentMapper.getFor(EffectHealingComponent.class);

    public static final ComponentMapper<EffectShieldingComponent> effectShielding =
            ComponentMapper.getFor(EffectShieldingComponent.class);

    public static final ComponentMapper<EffectTargetingComponent> effectTargeting =
            ComponentMapper.getFor(EffectTargetingComponent.class);

    public static final ComponentMapper<VisualEffectComponent> visualEffect =
            ComponentMapper.getFor(VisualEffectComponent.class);

    public static final ComponentMapper<ManagedLifetimeComponent> managedLifetime =
            ComponentMapper.getFor(ManagedLifetimeComponent.class);

    public static final ComponentMapper<IconComponent> icon =
            ComponentMapper.getFor(IconComponent.class);

}

