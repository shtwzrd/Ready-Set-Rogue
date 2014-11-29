package com.warsheep.scamp.desktop;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    public static void main (String[] args) throws Exception {
        String inputDir = "../android/assets/creatures_24x24";
        String outputDir = "../android/assets/creatures_24x24";
        String packFileName = "creatures";
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.pot = true;
        settings.square = true;
        TexturePacker.processIfModified(settings, inputDir, outputDir, packFileName);
    }
}