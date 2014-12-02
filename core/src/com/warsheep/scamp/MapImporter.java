package com.warsheep.scamp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.warsheep.scamp.components.CollidableComponent;
import com.warsheep.scamp.components.TileComponent;
import com.warsheep.scamp.components.TransformComponent;
import com.warsheep.scamp.components.VisibleComponent;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapImporter {

    private int tileSize = 24;

    private ArrayList<TileComponent> tileComponents;
    private ArrayList<VisibleComponent> visibleComponents;
    private ArrayList<CollidableComponent> collidableComponents;
    private ArrayList<Entity> entities;

    public MapImporter() {
       this.tileComponents = new ArrayList();
       this.visibleComponents = new ArrayList();
       this.collidableComponents = new ArrayList();
       this.entities = new ArrayList();
    }

    public void loadTiledMapJson(String mapPath) {
        FileHandle handle = new FileHandle(mapPath);

        JsonReader reader = new JsonReader();
        JsonValue jsonMap = reader.parse(handle);

        tileSize = jsonMap.getInt("tileheight");
        JsonValue layers = jsonMap.getChild("layers");
        JsonValue tileSets = jsonMap.getChild("tilesets");
        Map<String, Map<Integer, String>> tileMaps = new HashMap();

        // Build up the TileSets referenced by the map, with Atlas-friendly handles
        for (JsonValue tileset = tileSets; tileset != null; tileset = tileset.next) {
            Map<Integer, String> tileMap = new HashMap();
            JsonValue tiles = tileset.getChild("tiles");

            for (JsonValue tile = tiles; tile != null; tile = tile.next) {
                String imgHandle = tile.getString("image");
                imgHandle = Paths.get(imgHandle).getFileName().toString(); // just file name
                imgHandle = imgHandle.substring(0, imgHandle.lastIndexOf('.')); // minus extension
                tileMap.put(Integer.parseInt(tile.name), imgHandle);
            }

            tileMaps.put(tileset.getString("name"), tileMap);
        }

        // Construct the respective Components

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

                String imageString = tileMaps.get("world_24x24").get(id);

                if (!imageString.equals("blank_1") && imageString != null) {
                    // Separate handle from index
                    String imageIndex = imageString.substring(imageString.lastIndexOf('_') + 1, imageString.length());
                    imageString = imageString.substring(0, imageString.lastIndexOf('_'));

                    Entity e = new Entity();

                    TileComponent tc = new TileComponent();
                    tc.x = x;
                    tc.y = y;
                    tc.z = layerLevel;

                    this.tileComponents.add(tc);

                    VisibleComponent vc = new VisibleComponent();
                    //TODO: we shouldn't assume everything is in WORLD Atlas

                    vc.image = Scamp.WORLD.findRegion(imageString, Integer.parseInt(imageIndex));
                    vc.originX = this.tileSize / 2;
                    vc.originY = this.tileSize / 2;

                    this.visibleComponents.add(vc);

                    if (name.equals("Walls")) {
                        CollidableComponent cc = new CollidableComponent();
                        e.add(cc);
                        this.collidableComponents.add(cc);
                    }

                    e.add(tc);
                    e.add(vc);
                    e.add(new TransformComponent());
                    this.entities.add(e);
                }
                x++;
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
}
