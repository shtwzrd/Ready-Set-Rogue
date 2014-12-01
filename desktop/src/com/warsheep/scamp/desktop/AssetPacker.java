package com.warsheep.scamp.desktop;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main (String[] args) throws Exception {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.pot = true;
        settings.square = true;

        String creatureInputDir = "../android/assets/creatures_24x24";
        String creatureOutputDir = "../android/assets/creatures_24x24";
        String creaturePackFileName = "creatures";

        TexturePacker.processIfModified(settings, creatureInputDir,
                creatureOutputDir, creaturePackFileName);

        String worldInputDir = "../android/assets/world_24x24";
        String worldOutputDir = "../android/assets/world_24x24";
        String worldPackFileName = "world";

        TexturePacker.processIfModified(settings, worldInputDir,
                worldOutputDir, worldPackFileName);
    }
}
