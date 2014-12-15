package com.warsheep.scamp.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.MapImporter;
import com.warsheep.scamp.PrefabFactory;
import com.warsheep.scamp.Scamp;
import com.warsheep.scamp.algorithms.BSPMapGenerator;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.*;

import java.util.ArrayList;

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
    LifetimeProcessor lifetimeProcessor;
    VisualEffectProcessor visualEffectProcessor;

    Scamp game;
    public static GameState gameState;
    private MainGameScreenUI hud;

    // Temp UI Values


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

        collisionProcessor = new CollisionProcessor(collisionListeners);
        cameraProcessor = new CameraProcessor();
        deathProcessor = new DeathProcessor();
        levelProcessor = new LevelingProcessor();
        animationProcessor = new AnimationProcessor();
        lifetimeProcessor = new LifetimeProcessor(ecs);
        visualEffectProcessor = new VisualEffectProcessor(ecs);
        stateProcessor.addListener(controlProcessor);
        stateProcessor.addListener(visualEffectProcessor);

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
        ecs.addSystem(lifetimeProcessor);
        ecs.addSystem(visualEffectProcessor);
        Gdx.input.setInputProcessor(controlProcessor);
        this.hud = new MainGameScreenUI(stateProcessor);
        stateProcessor.addListener(this.hud);
        controlProcessor.addListener(this.hud);

        PrefabFactory fab = new PrefabFactory();


        // Crappy Debug Wizard mans
        Entity wizard = fab.buildEntity("creatures/debugwizard");
        ecs.addEntity(wizard);
        this.hud.changeEntity(wizard);

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
                ecs.update(delta);
                visibilityProcessor.endBatch();
                this.hud.drawHud(delta);
                break;
            case GAME_OVER:
                game.setScreen(new MainMenuScreen(game));
                break;
            default:
                break;
        }
    }

    public static Vector3 worldToScreen(Vector3 worldCoord) {
       Camera cam = ecs.getSystem(VisibilityProcessor.class).getCamera();
       return cam.project(worldCoord);
    }

    private void createCamera(Entity target) {
        Entity entity = new Entity();

        CameraComponent camera = new CameraComponent();
        camera.camera = ecs.getSystem(VisibilityProcessor.class).getCamera();
        camera.target = target;

        entity.add(camera);
        this.hud.changeEntity(target);

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
