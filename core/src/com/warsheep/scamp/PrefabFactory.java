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
                return new CollidableComponent();
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
                return new TileComponent();
            case TransformComponent:
                return cloner.deepClone(c);
            case VisibleComponent:
                return cloner.shallowClone(c);
            default:
                return cloner.shallowClone(c);
        }
    }

    private enum CLASS {
        AIControllableComponent,
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
        StateComponent,
        TileComponent,
        TransformComponent,
        VisibleComponent
    }
}
