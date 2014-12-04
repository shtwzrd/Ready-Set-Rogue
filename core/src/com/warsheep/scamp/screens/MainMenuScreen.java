package com.warsheep.scamp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.Scamp;

public class MainMenuScreen extends ScreenAdapter {

    Scamp game;
    OrthographicCamera guiCam;
    Rectangle playBounds;
    Vector3 touchPoint;

    public MainMenuScreen(Scamp game) {
        this.game = game;

        guiCam = new OrthographicCamera(Scamp.V_WIDTH, Scamp.V_HEIGHT);
        guiCam.position.set(Scamp.V_WIDTH / 2, Scamp.V_HEIGHT / 2, 0);
        playBounds = new Rectangle(Scamp.V_WIDTH/4, Scamp.V_HEIGHT/4, Scamp.V_WIDTH/2, Scamp.V_HEIGHT/2);
        touchPoint = new Vector3();
    }

    public void update() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
//                Assets.playSound(Assets.clickSound);
                game.setScreen(new MainGameScreen(game));
                return;
            }
        }
    }

    public void draw () {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        game.batcher.begin();
        // Draw Main Menu Screen
        game.batcher.end();
    }

    @Override
    public void render (float delta) {
        update();
        draw();
    }

}
