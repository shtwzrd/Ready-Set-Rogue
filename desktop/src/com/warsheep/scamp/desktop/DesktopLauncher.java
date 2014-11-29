package com.warsheep.scamp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.warsheep.scamp.Scamp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Scamp.TITLE;
		config.width = Scamp.V_WIDTH;
		config.height = Scamp.V_HEIGHT;
		new LwjglApplication(new Scamp(), config);
	}
}
