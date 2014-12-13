package com.warsheep.scamp;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.rits.cloning.Cloner;
import com.warsheep.scamp.components.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrefabFactory {
    private AssetDepot assets = AssetDepot.getInstance();
    private Json json = new Json();
    private Map<String, ArrayList<Component>> prefabs = new HashMap<>();
    private Cloner cloner = new Cloner();
    private CollidableComponent collidable = new CollidableComponent();

    public Entity buildEntity(String handle) {
        if (!this.prefabs.containsKey(handle)) {
            this.loadPrefab(handle);
        }

        Entity e = new Entity();

        for (Component c : this.prefabs.get(handle)) {
            e.add(initializeComponent(c));
        }

        return e;
    }

    private void loadPrefab(String handle) {
        JsonValue data = assets.fetchJson(handle);
        String com = data.get("components").toString();
        ArrayList<Component> c = json.fromJson(ArrayList.class, com);
        prefabs.put(handle, c);
    }

    private Component initializeComponent(Component c) {
        switch (CLASS.valueOf(c.getClass().getSimpleName())) {
            case AIControllableComponent:
                return cloner.deepClone(c);
            case AttackerComponent:
                return cloner.shallowClone(c);
            case CameraComponent:
                return new CameraComponent();
            case CollidableComponent:
                return collidable;
            case ControllableComponent:
                return cloner.shallowClone(c);
            case DamageableComponent:
                return cloner.shallowClone(c);
            case DropComponent:
                return cloner.shallowClone(c);
            case FactionComponent:
                return cloner.shallowClone(c);
            case InventoryComponent:
                return cloner.deepClone(c);
            case LevelComponent:
                return cloner.shallowClone(c);
            case MovementComponent:
                return cloner.shallowClone(c);
            case StateComponent:
                return new StateComponent();
            case TileComponent:
                return cloner.deepClone(c);
            case TransformComponent:
                return cloner.deepClone(c);
            case VisibleComponent:
                return cloner.shallowClone(c);
            case SpellbookComponent:
                return cloner.shallowClone(c);
            case VisualEffectComponent:
                return cloner.deepClone(c);
            case ManagedLifetimeComponent:
                return new ManagedLifetimeComponent();
            default:
                return cloner.shallowClone(c);
        }
    }

    private enum CLASS {
        AIControllableComponent,
        AnimatableComponent,
        AttackerComponent,
        CameraComponent,
        CollidableComponent,
        ControllableComponent,
        DamageableComponent,
        DropComponent,
        FactionComponent,
        InventoryComponent,
        LevelComponent,
        MovementComponent,
        ManagedLifetimeComponent,
        StateComponent,
        TileComponent,
        TransformComponent,
        VisibleComponent,
        VisualEffectComponent,
        SpellbookComponent
    }
}
