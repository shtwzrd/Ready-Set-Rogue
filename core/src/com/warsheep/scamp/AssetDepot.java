package com.warsheep.scamp;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class AssetDepot {
    public static final String MAP_PATH = "prefabs/maps/test_level.json";

    private static final String[] LOAD_ON_START = {"creatures_24x24", "world_24x24"};
    private Map<String, TextureAtlas> textures;
    private Map<String, JsonValue> prefabs;
    private AssetManager assetManager;
    private JsonReader jsonReader;

    private static final AssetDepot instance = new AssetDepot();

    public static synchronized AssetDepot getInstance() {
        return instance;
    }

    private AssetDepot() {
        assetManager = new AssetManager();
        jsonReader = new JsonReader();
        textures = new HashMap<>();
        prefabs = new HashMap<>();
        for (int i = 0; i < LOAD_ON_START.length; i++) {
            this.prefetch(LOAD_ON_START[i]);
        }
    }

    public JsonValue fetchJson(String handle) {
        if(!this.prefabs.containsKey(handle)) {
            this.prefabs.put(handle, jsonReader.parse(new FileHandle("prefabs/" + handle + ".json")));
        }
        return this.prefabs.get(handle);
    }

    public AtlasRegion fetchImage(String path, String handle) {
        if (!this.textures.containsKey(path)) {
            this.prefetch(path);
        }
        return textures.get(path).findRegion(handle);
    }

    public AtlasRegion fetchImage(String path, String handle, int index) {
        if (!this.textures.containsKey(path)) {
            this.prefetch(path);
        }
        return textures.get(path).findRegion(handle, index);
    }

    public void prefetch(String path) {
        assetManager.load(atlasPath(path), TextureAtlas.class);
        assetManager.finishLoading();
        textures.put(path, assetManager.get(atlasPath(path), TextureAtlas.class));
    }

    private String atlasPath(String path) {
        String fileName = path.substring(0, path.indexOf('_')) + ".atlas";
        return path + "/" + fileName;
    }

}
