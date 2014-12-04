package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.*;
import com.warsheep.scamp.screens.MainMenuScreen;

import java.util.ArrayList;
import java.util.Arrays;

public class Scamp extends Game {

    public static final int V_WIDTH = 320, V_HEIGHT = 240; // Internal dimensions in pixels

    public static final String TITLE = "SCAMP";

    public SpriteBatch batcher;

    @Override
    public void create() {
        batcher = new SpriteBatch();

        // Settings.load();
        // Assets.load();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }
}
