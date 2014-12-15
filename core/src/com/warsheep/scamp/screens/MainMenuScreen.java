package com.warsheep.scamp.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.ReadySetRogue;

import java.util.Random;

public class MainMenuScreen extends ScreenAdapter {

    ReadySetRogue game;
    OrthographicCamera guiCam;
    Rectangle playBounds;
    Vector3 touchPoint;
    SpriteBatch spriteBatch;
    BitmapFont font;

    private static Random rnd = new Random();

    public MainMenuScreen(ReadySetRogue game) {
        this.game = game;

        guiCam = new OrthographicCamera(ReadySetRogue.V_WIDTH, ReadySetRogue.V_HEIGHT);
        guiCam.position.set(ReadySetRogue.V_WIDTH / 2, ReadySetRogue.V_HEIGHT / 2, 0);
        playBounds = new Rectangle(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        touchPoint = new Vector3();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
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

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);

        CharSequence str = "Play";


        spriteBatch.begin();
        font.draw(spriteBatch, str, Gdx.graphics.getWidth() / 2 - 12, Gdx.graphics.getHeight() / 2);
        spriteBatch.end();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

}
