package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.*;

public class Scamp extends Game {

    public static final String TITLE = "SCAMP";
    public static final String CREATURES_PATH = "creatures_24x24/creatures.atlas";
    public static final String WORLD_PATH = "world_24x24/world.atlas";
    public static final String MAP_PATH = "prefabs/maps/chamber-of-secrets.json";
    public static final int V_WIDTH = 320, V_HEIGHT = 240; // Internal dimensions in pixels

    public static Engine ecs; // Ashley Entity-Component System
    VisibilityProcessor visibilityProcessor;
    MovementProcessor movementProcessor;
	CollisionProcessor collisionProcessor;
    ControlProcessor controlProcessor;
	DeathProcessor deathProcessor;
	CombatProcessor combatProcessor;
    CameraProcessor cameraProcessor;
    TileProcessor tileProcessor;

    AssetManager assetManager = new AssetManager();
    public static TextureAtlas CREATURES;
    public static TextureAtlas WORLD;

    Entity wizard;

    private long startTime;
    private long delta;

    @Override
    public void create() {
        ecs = new Engine();

        // Initialize processors and associate them with ecs engine
        visibilityProcessor = new VisibilityProcessor();
		collisionProcessor = new CollisionProcessor(1);
		movementProcessor = new MovementProcessor(2);
		controlProcessor = new ControlProcessor();
		cameraProcessor = new CameraProcessor();
		deathProcessor = new DeathProcessor();
		combatProcessor = new CombatProcessor(3);
        tileProcessor = new TileProcessor();
        ecs.addSystem(visibilityProcessor);
		ecs.addSystem(collisionProcessor);
		ecs.addSystem(tileProcessor);
		ecs.addSystem(movementProcessor);
		ecs.addSystem(cameraProcessor);
		ecs.addSystem(deathProcessor);
		ecs.addSystem(combatProcessor);
        ecs.addSystem(controlProcessor);
        Gdx.input.setInputProcessor(controlProcessor);

        // Load assets
        assetManager.load(CREATURES_PATH, TextureAtlas.class);
        assetManager.load(WORLD_PATH, TextureAtlas.class);
        assetManager.finishLoading(); // Synchronous, pauses until everything loads
        CREATURES = assetManager.get(CREATURES_PATH, TextureAtlas.class);
        WORLD = assetManager.get(WORLD_PATH, TextureAtlas.class);

        // Lol load map first sos it gets drawn first
        MapImporter.getTileComponents(MAP_PATH);

		// Skeleton blocker of doom
		Entity skeleton = new Entity();
		skeleton.add(new VisibleComponent());
		skeleton.add(new TransformComponent());
		skeleton.add(new CollidableComponent());
		skeleton.add(new DamageableComponent());
		ecs.addEntity(skeleton);
		VisibleComponent skeletonVisComp = ECSMapper.visible.get(skeleton);
		TransformComponent skeletonTraComp = ECSMapper.transform.get(skeleton);
		skeletonVisComp.image = CREATURES.findRegion("oryx_n_skeleton");
		skeletonVisComp.originY = skeletonVisComp.image.getRegionHeight() / 2;
		skeletonVisComp.originX = skeletonVisComp.image.getRegionWidth() / 2;
		skeletonTraComp.position = new Vector3(48, 48, 0);

        // Crappy Debug Wizard mans
        wizard = new Entity();
        wizard.add(new VisibleComponent());
        wizard.add(new TransformComponent());
        wizard.add(new MovementComponent());
        wizard.add(new ControllableComponent());
        wizard.add(new TilePositionComponent());
        ecs.addEntity(wizard);
        VisibleComponent wizardVisComp = ECSMapper.visible.get(wizard);
        wizardVisComp.image = CREATURES.findRegion("oryx_m_wizard");
        wizardVisComp.originX = wizardVisComp.image.getRegionWidth() / 2;
        wizardVisComp.originY = wizardVisComp.image.getRegionHeight() / 2;

        createCamera(wizard);

        //Start calculating game time
        startTime = System.currentTimeMillis();

    }

    @Override
    public void render() {
        delta = (System.currentTimeMillis() - startTime);
        ecs.update(delta);
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
