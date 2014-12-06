package com.warsheep.scamp.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.MapImporter;
import com.warsheep.scamp.Scamp;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainGameScreen extends ScreenAdapter {

    public static Engine ecs; // Ashley Entity-Component System
    public static final float TURN_DURATION = 2;
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

    Entity wizard;
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

        movementProcessor = new MovementProcessor();
        combatProcessor = new CombatProcessor();
        aiProcessor = new AIProcessor();
        controlProcessor = new ControlProcessor();
        ArrayList<StateProcessor.StateListener> stateListeners = new ArrayList();
        stateListeners.add(movementProcessor);
        stateListeners.add(combatProcessor);
        stateListeners.add(aiProcessor);
        stateListeners.add(controlProcessor);
        stateProcessor = new StateProcessor(stateListeners, TURN_DURATION);

        collisionProcessor = new CollisionProcessor(collisionListeners);
        cameraProcessor = new CameraProcessor();
        deathProcessor = new DeathProcessor();
        tileProcessor = new TileProcessor();
        levelProcessor = new LevelingProcessor();


        ecs.addSystem(visibilityProcessor);
        ecs.addSystem(collisionProcessor);
        ecs.addSystem(tileProcessor);
        ecs.addSystem(movementProcessor);
        ecs.addSystem(cameraProcessor);
        ecs.addSystem(deathProcessor);
        ecs.addSystem(combatProcessor);
        ecs.addSystem(stateProcessor);
        ecs.addSystem(aiProcessor);
        ecs.addSystem(controlProcessor);
        ecs.addSystem(levelProcessor);
        Gdx.input.setInputProcessor(controlProcessor);

        AssetDepot assets = AssetDepot.getInstance();

        // Skeleton blocker of doom
        Random rand = new Random();
        for (int i = 1; i < 10; i++) {
            Entity skeleton = new Entity();
            skeleton.add(new VisibleComponent());
            skeleton.add(new TransformComponent());
            skeleton.add(new CollidableComponent());
            skeleton.add(new DamageableComponent());
            skeleton.add(new TilePositionComponent());
            skeleton.add(new AIControllableComponent());
            skeleton.add(new AttackerComponent());
            skeleton.add(new StateComponent());
            skeleton.add(new FactionComponent());
            skeleton.add(new DropComponent());
            ECSMapper.drop.get(skeleton).experienceDrop = 100;
            ecs.addEntity(skeleton);
            VisibleComponent skeletonVisComp = ECSMapper.visible.get(skeleton);
            skeletonVisComp.image = assets.fetch("creatures_24x24", "oryx_n_skeleton");
            skeletonVisComp.originY = skeletonVisComp.image.getRegionHeight() / 2;
            skeletonVisComp.originX = skeletonVisComp.image.getRegionWidth() / 2;
//            int x = rand.nextInt(12) + 2;
//            int y = rand.nextInt(12) + 2;
            int x = rand.nextInt(12) + 2;
            int y = rand.nextInt(12) + 2;
            ECSMapper.transform.get(skeleton).position.y = y * 24;
            ECSMapper.transform.get(skeleton).position.x = x * 24;
            ECSMapper.tilePosition.get(skeleton).y = y;
            ECSMapper.tilePosition.get(skeleton).x = x;
        }

        // Crappy Debug Wizard mans
        wizard = new Entity();
        wizard.add(new VisibleComponent());
        wizard.add(new TransformComponent());
        wizard.add(new CollidableComponent());
        wizard.add(new ControllableComponent());
        wizard.add(new AttackerComponent());
        wizard.add(new DamageableComponent());
        wizard.add(new TilePositionComponent());
        wizard.add(new StateComponent());
        wizard.add(new FactionComponent());
        wizard.add(new LevelComponent());
        wizard.add(new InventoryComponent());
        ecs.addEntity(wizard);

        DamageableComponent dmgComp = ECSMapper.damage.get(wizard);
        dmgComp.essential = true;

        VisibleComponent wizardVisComp = ECSMapper.visible.get(wizard);
        wizardVisComp.image = assets.fetch("creatures_24x24", "oryx_m_wizard");
        wizardVisComp.originX = wizardVisComp.image.getRegionWidth() / 2;
        wizardVisComp.originY = wizardVisComp.image.getRegionHeight() / 2;
        ECSMapper.faction.get(wizard).factions = Arrays.asList(FactionComponent.Faction.GOOD);

        ECSMapper.attack.get(wizard).attackRange = 3;

        ECSMapper.tilePosition.get(wizard).x = 8;
        ECSMapper.tilePosition.get(wizard).y = 8;
        ECSMapper.transform.get(wizard).position.x = 8 * 24;
        ECSMapper.transform.get(wizard).position.y = 8 * 24;

        createCamera(wizard);

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
        switch (gameState) {
            case GAME_RUNNING:
                visibilityProcessor.startBatch();
                delta = ((System.currentTimeMillis() - startTime) / 1000);
                ecs.update(delta);
                visibilityProcessor.endBatch();
                addPlayerStats();
                addTimeCircle(delta);
                break;
            case GAME_OVER:
                game.setScreen(new MainMenuScreen(game));
                break;
            default:
                break;
        }
    }

    private void addTimeCircle(float delta) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();

        if (delta - accumulator > TURN_DURATION) {
            accumulator = delta;
        }
        int size = (int) (10 - (delta % 3 * 3)) - 1;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 0.5f);
        shapeRenderer.circle(Gdx.graphics.getWidth() - 15, Gdx.graphics.getHeight() - 15, size);
        shapeRenderer.end();
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
    }

    private void createCamera(Entity target) {
        Entity entity = new Entity();

        CameraComponent camera = new CameraComponent();
        camera.camera = ecs.getSystem(VisibilityProcessor.class).getCamera();
        camera.target = target;

        entity.add(camera);

        ecs.addEntity(entity);
    }
}
