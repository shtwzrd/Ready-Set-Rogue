package com.warsheep.scamp;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.HashMap;
import java.util.Map;

public class AssetDepot {
    public static final String MAP_PATH = "prefabs/maps/chamber-of-secrets.json";

    private static final String[] LOAD_ON_START = {"creatures_24x24", "world_24x24"};
    private Map<String, TextureAtlas> textures;
    private AssetManager assetManager;

    private static final AssetDepot instance = new AssetDepot();

    public static synchronized AssetDepot getInstance() {
        return instance;
    }

    private AssetDepot() {
        assetManager = new AssetManager();
        textures = new HashMap<>();
        for (int i = 0; i < LOAD_ON_START.length; i++) {
            this.prefetch(LOAD_ON_START[i]);
        }
    }

    public AtlasRegion fetch(String path, String handle) {
        if (!this.textures.containsKey(path)) {
            this.prefetch(path);
        }
        return textures.get(path).findRegion(handle);
    }

    public AtlasRegion fetch(String path, String handle, int index) {
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
