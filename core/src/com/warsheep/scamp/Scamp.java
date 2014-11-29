package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Scamp extends Game {
	public static final String TITLE="SCAMP";
    public static final int V_WIDTH=320,V_HEIGHT=240; // Internal dimensions in pixels

	Engine ecs; //Ashley Entity-Component System
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		ecs = new Engine();
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("oryx_16bit_fantasy_creatures_trans.png"));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
