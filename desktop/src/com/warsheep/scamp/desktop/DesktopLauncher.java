package com.warsheep.scamp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.warsheep.scamp.ReadySetRogue;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = ReadySetRogue.TITLE;
		config.width = ReadySetRogue.V_WIDTH;
		config.height = ReadySetRogue.V_HEIGHT;
		new LwjglApplication(new ReadySetRogue(), config);
	}
}
