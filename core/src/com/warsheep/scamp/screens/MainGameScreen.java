package com.warsheep.scamp.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.MapImporter;
import com.warsheep.scamp.PrefabFactory;
import com.warsheep.scamp.Scamp;
import com.warsheep.scamp.algorithms.BSPMapGenerator;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.*;

import java.util.ArrayList;
import java.util.Random;

public class MainGameScreen extends ScreenAdapter {

    public static Engine ecs; // Ashley Entity-Component System
    public static final float TURN_DURATION = 2.2f;
    public static final int MAP_WIDTH = 47;
    public static final int MAP_HEIGHT = 65;
    public static Vector3 moveToPos = new Vector3();
    public static Vector3 attackPos = new Vector3();
    VisibilityProcessor visibilityProcessor;
    MovementProcessor movementProcessor;
    CollisionProcessor collisionProcessor;
    ControlProcessor controlProcessor;
    DeathProcessor deathProcessor;
    StateProcessor stateProcessor;
    CombatProcessor combatProcessor;
    CameraProcessor cameraProcessor;
    TileProcessor tileProcessor;
    AIProcessor aiProcessor;
    LevelingProcessor levelProcessor;
    AnimationProcessor animationProcessor;
    SpellCastProcessor spellCastProcessor;

    Scamp game;
    public static GameState gameState;
    private float accumulator = 0;

    // Temp UI Values
    public static int damage = 0;
    public static int currentHealth = 0;
    public static int maxHealth = 0;
    public static int level = 0;
    public static int currentExp = 0;
    public static int nextLevelExp = 0;

    public enum GameState {
        GAME_RUNNING, GAME_PAUSED, GAME_OVER, GAME_READY
    }

    private long startTime;

    public MainGameScreen(Scamp game) {
        this.game = game;
        gameState = GameState.GAME_RUNNING;
        create();
    }

    public void create() {
        ecs = new Engine();

        ArrayList<CollisionProcessor.CollisionListener> collisionListeners = new ArrayList();
        // Initialize processors and associate them with ecs engine
        visibilityProcessor = new VisibilityProcessor();

        tileProcessor = new TileProcessor(MAP_WIDTH, MAP_HEIGHT);
        ArrayList<MovementProcessor.MovementListener> movementListeners = new ArrayList();
        movementListeners.add(tileProcessor);
        movementProcessor = new MovementProcessor(movementListeners);
        combatProcessor = new CombatProcessor();
        aiProcessor = new AIProcessor();
        spellCastProcessor = new SpellCastProcessor();

        ArrayList<StateProcessor.StateListener> stateListeners = new ArrayList();
        stateListeners.add(movementProcessor);
        stateListeners.add(combatProcessor);
        stateListeners.add(aiProcessor);
        stateListeners.add(spellCastProcessor);
        stateProcessor = new StateProcessor(stateListeners, TURN_DURATION);
        controlProcessor = new ControlProcessor(stateProcessor);
        stateListeners.add(controlProcessor);
        stateProcessor.addListener(controlProcessor);

        collisionProcessor = new CollisionProcessor(collisionListeners);
        cameraProcessor = new CameraProcessor();
        deathProcessor = new DeathProcessor();
        levelProcessor = new LevelingProcessor();
        animationProcessor = new AnimationProcessor();

        ecs.addSystem(tileProcessor);
        ecs.addSystem(visibilityProcessor);
        ecs.addSystem(collisionProcessor);
        ecs.addSystem(movementProcessor);
        ecs.addSystem(cameraProcessor);
        ecs.addSystem(deathProcessor);
        ecs.addSystem(combatProcessor);
        ecs.addSystem(spellCastProcessor);
        ecs.addSystem(stateProcessor);
        ecs.addSystem(aiProcessor);
        ecs.addSystem(controlProcessor);
        ecs.addSystem(levelProcessor);
        ecs.addSystem(animationProcessor);
        Gdx.input.setInputProcessor(controlProcessor);

        PrefabFactory fab = new PrefabFactory();


        // Skeleton blocker of doom

//        Random rand = new Random();
//        for (int i = 1; i < 20; i++) {
//            Entity skeleton = fab.buildEntity("creatures/skeleton");
//            ECSMapper.tile.get(skeleton).x = rand.nextInt(30-1) + 1;
//            ECSMapper.tile.get(skeleton).y = rand.nextInt(30-1) + 1;
//            ecs.addEntity(skeleton);
//        }
//
//        for (int i = 1; i < 5; i++) {
//            Entity skeleton = fab.buildEntity("creatures/ghost");
//            ECSMapper.tile.get(skeleton).x = rand.nextInt(30-1) + 1;
//            ECSMapper.tile.get(skeleton).y = rand.nextInt(30-1) + 1;
//            ecs.addEntity(skeleton);
//        }
//
//        for (int i = 1; i < 10; i++) {
//            Entity skeleton = fab.buildEntity("creatures/skeleton_archer");
//            ECSMapper.tile.get(skeleton).x = rand.nextInt(30-1) + 1;
//            ECSMapper.tile.get(skeleton).y = rand.nextInt(30-1) + 1;
//            ecs.addEntity(skeleton);
//        }

        // Crappy Debug Wizard mans
        Entity wizard = fab.buildEntity("creatures/debugwizard");
        ecs.addEntity(wizard);

        ECSMapper.tile.get(wizard).x = 23;
        ECSMapper.tile.get(wizard).y = 7;
        ECSMapper.transform.get(wizard).position.x = 23 * 24 + ECSMapper.transform.get(wizard).xOffset;
        ECSMapper.transform.get(wizard).position.y = 7 * 24 + ECSMapper.transform.get(wizard).yOffset;

        createCamera(wizard);


        // genMap();
        MapImporter mapImporter = new MapImporter();
        mapImporter.loadTiledMapJson(AssetDepot.MAP_PATH);

        for (Entity e : mapImporter.getEntities()) {
            ecs.addEntity(e);
        }

        //Start calculating game time
        startTime = System.currentTimeMillis();
    }

    @Override
    public void render(float delta) {
        if (delta > 0.1f) {
            delta = 0.1f;
        }
        switch (gameState) {
            case GAME_RUNNING:
                visibilityProcessor.startBatch();
                // delta = ((System.currentTimeMillis() - startTime) / 1000);
                ecs.update(delta);
                visibilityProcessor.endBatch();
                addPlayerStats();
                addTimeCircle(delta);
                addMoveToPos();
                addSpellGrid();
                addTurnTimer();
                break;
            case GAME_OVER:
                game.setScreen(new MainMenuScreen(game));
                break;
            default:
                break;
        }
    }

    private void addMoveToPos() {
        boolean attackHappened = attackPos.x != 0 || attackPos.y != 0;
        boolean moveHappened = moveToPos.x != 0 || moveToPos.y != 0;
        if (attackHappened || moveHappened) {
            SpriteBatch spriteBatch = new SpriteBatch();
            BitmapFont font = new BitmapFont();

            spriteBatch.enableBlending();
            spriteBatch.begin();
            if (moveHappened) {
                font.draw(spriteBatch, "X", moveToPos.x * 24 + Gdx.graphics.getWidth() / 2 - 5, moveToPos.y * 24 + Gdx.graphics.getHeight() / 2);
            }
            if (attackHappened) {
                font.draw(spriteBatch, "O", attackPos.x * 24 + Gdx.graphics.getWidth() / 2 - 5, attackPos.y * 24 + Gdx.graphics.getHeight() / 2);
            }
            spriteBatch.end();
            font.dispose();
            spriteBatch.dispose();
        }
    }

    private void addSpellGrid() {
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 0.5f);

        for (int i = 1; i < 6; i++) {
            shapeRenderer.rect(Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 7, Gdx.graphics.getHeight() / 7 * i, Gdx.graphics.getHeight() / 7, Gdx.graphics.getHeight() / 7);
        }
        shapeRenderer.end();
        shapeRenderer.dispose();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void addTimeCircle(float delta) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        int size = (int) (10 - (delta % 3 * 3)) - 1;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if(stateProcessor.isPlanningTurn()) {
            shapeRenderer.setColor(0, 1, 0, 0.5f);
        } else {
            shapeRenderer.setColor(1, 0, 0, 0.5f);
        }
        shapeRenderer.circle(Gdx.graphics.getWidth() - 15, Gdx.graphics.getHeight() - 15, size);
        shapeRenderer.end();
        shapeRenderer.dispose();
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }

    private void addPlayerStats() {
        CharSequence str = String.format("Lvl %d | Dmg %d | HP %d/%d | Exp %d/%d",
                level, damage, currentHealth, maxHealth, currentExp, nextLevelExp);
        SpriteBatch spriteBatch = new SpriteBatch();
        BitmapFont font = new BitmapFont();

        spriteBatch.enableBlending();
        spriteBatch.begin();
        font.draw(spriteBatch, str, 10, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();
        spriteBatch.dispose();
        font.dispose();
    }

    private void addTurnTimer() {
        CharSequence str = String.format("%s %s", "Processing... ", stateProcessor.getCurrentTurn().toString());
        SpriteBatch spriteBatch = new SpriteBatch();
        BitmapFont font = new BitmapFont();

        spriteBatch.enableBlending();
        spriteBatch.begin();
        font.draw(spriteBatch, str, 10, 20);
        spriteBatch.end();
        spriteBatch.dispose();
        font.dispose();
    }

    private void createCamera(Entity target) {
        Entity entity = new Entity();

        CameraComponent camera = new CameraComponent();
        camera.camera = ecs.getSystem(VisibilityProcessor.class).getCamera();
        camera.target = target;

        entity.add(camera);

        ecs.addEntity(entity);
    }

    private void genMap() {
        BSPMapGenerator gen = new BSPMapGenerator(MAP_WIDTH, MAP_HEIGHT, 1, 3, 4);
        byte[][] data = gen.to2DArray();
        for (int x = 0; x < data.length; x++) {
            for (int y = data[0].length - 1; y >= 0; y--) {
                if (data[x][y] != ' ') {
                    buildTile(x, data.length - y - 1, 1, (char) data[x][y]);
                }
            }
        }
    }

    private void buildTile(int x, int y, int z, char type) {
        Entity e = new Entity();

        TileComponent tc = new TileComponent();
        tc.x = x;
        tc.y = y;
        tc.z = z;

        VisibleComponent vc = new VisibleComponent();

        AssetDepot assets = AssetDepot.getInstance();

        vc.originX = 12;
        vc.originY = 12;

        if (type == '#') {
            CollidableComponent cc = new CollidableComponent();
            e.add(cc);
            vc.image = assets.fetchImage("world_24x24", "oryx_wall_island_stone", 1);
        } else {
            vc.image = assets.fetchImage("world_24x24", "oryx_floor_darkgrey_stone", 1);
        }

        e.add(tc);
        e.add(vc);
        e.add(new TransformComponent());
        ecs.addEntity(e);
    }
}
