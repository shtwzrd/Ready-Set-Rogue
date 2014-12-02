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

    public static ArrayList<TileComponent> getTileComponents(String mapPath) {
        FileHandle handle = new FileHandle(mapPath);

        JsonReader reader = new JsonReader();
        JsonValue jsonMap = reader.parse(handle);

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
        int layerLevel = 0;
        for (JsonValue layer = layers; layer != null; layer = layer.next) {
            int width = layer.getInt("width");
            String name = layer.getString("name");
            int[] data = layer.get("data").asIntArray();

            int x = 0, y = 0;
            for(int i = 0; i < data.length; i++) {

                Entity e = new Entity();

                TileComponent tc = new TileComponent();
                tc.x = x;
                tc.y = y;

                VisibleComponent vc = new VisibleComponent();
                //TODO: we shouldn't assume everything is in WORLD Atlas
                String imageString = tileMaps.get("world_24x24").get(data[i]);

                if(imageString == null) {
                    imageString = "blank_1";
                }

                String imageIndex = imageString.substring(imageString.lastIndexOf('_') + 1, imageString.length());
                imageString = imageString.substring(0, imageString.lastIndexOf('_'));
                vc.image = Scamp.WORLD.findRegion(imageString, Integer.parseInt(imageIndex));
                vc.originX = 12;
                vc.originY = 12;

                if(name == "Walls") {
                   // TODO:
                   // CollideableComponent = new CollideableComponent();
                    e.add(new CollidableComponent());
                }
                e.add(tc);
                e.add(vc);
                e.add(new TransformComponent());
                Scamp.ecs.addEntity(e);
               x++;
               if(x == width) {
                  x = 0;
                  y++;
               }

            }

            layerLevel++;
        }

        return new ArrayList<TileComponent>();

    }
}
