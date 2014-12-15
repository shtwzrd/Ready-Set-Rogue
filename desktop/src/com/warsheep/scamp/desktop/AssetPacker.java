package com.warsheep.scamp.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main(String[] args) throws Exception {
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

        String fxInputDir = "../android/assets/fx_24x24";
        String fxOutputDir = "../android/assets/fx_24x24";
        String fxPackFileName = "fx";

        TexturePacker.processIfModified(settings, fxInputDir,
                fxOutputDir, fxPackFileName);

        String fx32InputDir = "../android/assets/fx_32x32";
        String fx32OutputDir = "../android/assets/fx_32x32";
        String fx32PackFileName = "fx32";

        TexturePacker.processIfModified(settings, fx32InputDir,
                fx32OutputDir, fx32PackFileName);

        String iconInputDir = "../android/assets/icons_26x28";
        String iconOutputDir = "../android/assets/icons_26x28";
        String iconPackFileName = "icons";

        TexturePacker.processIfModified(settings, iconInputDir,
                iconOutputDir, iconPackFileName);
    }
}
