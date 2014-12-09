package com.warsheep.scamp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.warsheep.scamp.components.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapImporter {

    private int tileSize = 24;
    private static final String MAGIC_BLANK_TILE = "blank_1";

    private ArrayList<TileComponent> tileComponents;
    private ArrayList<VisibleComponent> visibleComponents;
    private ArrayList<CollidableComponent> collidableComponents;
    private ArrayList<Entity> entities;
    private JsonReader reader;
    private Map<Integer, String[]> tileMaps;

    public MapImporter() {
        this.tileComponents = new ArrayList();
        this.visibleComponents = new ArrayList();
        this.collidableComponents = new ArrayList();
        this.entities = new ArrayList();
        this.reader = new JsonReader();
        this.tileMaps = new HashMap();
    }

    public void loadTiledMapJson(String mapPath) {
        FileHandle handle = new FileHandle(mapPath);
        JsonValue jsonMap = reader.parse(handle);

        tileSize = jsonMap.getInt("tileheight");
        JsonValue layers = jsonMap.getChild("layers");
        JsonValue tileSets = jsonMap.getChild("tilesets");

        tileMaps = this.getTileSets(tileSets);

        int layerLevel = layers.getInt("height");

        for (JsonValue layer = layers; layer != null; layer = layer.next) {
            int width = layer.getInt("width");
            int height = layer.getInt("height");
            String name = layer.getString("name");
            int[] data = layer.get("data").asIntArray();

            int x = 0, y = height - 1;
            for (int i = 0; i < data.length; i++) {

                int id = data[i];
                if (id != 0) {
                    id--; // Tiled Json is weird. Just accept it.
                }

                String[] tilePathHandle = tileMaps.get(id);

                if (tilePathHandle != null && !tilePathHandle[1].equals(MAGIC_BLANK_TILE)) {
                    boolean walls = name.equals("Walls") ? true : false;
                    this.buildTile(x, y, layerLevel, tilePathHandle, walls);
                }


                x++;

                // begin new row
                if (x == width) {
                    x = 0;
                    y--;
                }
            }
            layerLevel--;
        }
    }


    public ArrayList<TileComponent> getTileComponents() {
        return this.tileComponents;
    }

    public ArrayList<VisibleComponent> getVisibleComponents() {
        return this.visibleComponents;
    }

    public ArrayList<CollidableComponent> getCollidableComponents() {
        return this.collidableComponents;
    }

    public ArrayList<Entity> getEntities() {
        return this.entities;
    }

    // Build up the TileSets referenced by the map, with Atlas-friendly handles
    private Map<Integer, String[]> getTileSets(JsonValue tileSets) {

        for (JsonValue tileset = tileSets; tileset != null; tileset = tileset.next) {
            JsonValue tiles = tileset.getChild("tiles");

            for (JsonValue tile = tiles; tile != null; tile = tile.next) {
                String[] tileRef = new String[2];
                String imgHandle = tile.getString("image");
                tileRef[0] = Paths.get(imgHandle).getParent().getFileName().toString();
                imgHandle = Paths.get(imgHandle).getFileName().toString(); // just file name
                imgHandle = imgHandle.substring(0, imgHandle.lastIndexOf('.')); // minus extension
                tileRef[1] = imgHandle;
                tileMaps.put(Integer.parseInt(tile.name), tileRef);
            }
        }

        return tileMaps;
    }

    private void buildTile(int x, int y, int z, String[] tilePathHandle, boolean wall) {

        // Separate handle from index
        String imageIndex = tilePathHandle[1]
                .substring(tilePathHandle[1].lastIndexOf('_') + 1, tilePathHandle[1].length());
        String imageHandle = tilePathHandle[1].substring(0, tilePathHandle[1].lastIndexOf('_'));

        Entity e = new Entity();

        TileComponent tc = new TileComponent();
        tc.x = x;
        tc.y = y;
        tc.z = z;

        this.tileComponents.add(tc);

        VisibleComponent vc = new VisibleComponent();

        AssetDepot assets = AssetDepot.getInstance();

        vc.image = assets.fetchImage(tilePathHandle[0], imageHandle, Integer.parseInt(imageIndex));
        vc.originX = this.tileSize / 2;
        vc.originY = this.tileSize / 2;

        this.visibleComponents.add(vc);

        if (wall) {
            CollidableComponent cc = new CollidableComponent();
            e.add(cc);
            this.collidableComponents.add(cc);
        }

        e.add(tc);
        e.add(vc);
        e.add(new TransformComponent());
        this.entities.add(e);
    }
}
