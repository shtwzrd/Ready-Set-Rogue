package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.warsheep.scamp.components.*;

public class Scamp extends Game {
	public static final String TITLE = "SCAMP";
	public static final String CREATURES_PATH = "creatures_24x24/creatures.atlas";
    public static final int V_WIDTH=320,V_HEIGHT=240; // Internal dimensions in pixels

	Engine ecs; // Ashley Entity-Component System
	VisibilityProcessor visibilityProcessor;
	MovementProcessor movementProcessor;
	ControlProcessor controlProcessor;
	CameraProcessor cameraProcessor;

	AssetManager assetManager = new AssetManager();
	public static TextureAtlas CREATURES;

	Entity wizard;

	private long startTime;
	private long delta;

	@Override
	public void create () {
		ecs = new Engine();

		// Initialize processors and associate them with ecs engine
		visibilityProcessor = new VisibilityProcessor();
		movementProcessor = new MovementProcessor();
		controlProcessor = new ControlProcessor();
		cameraProcessor = new CameraProcessor();
		ecs.addSystem(visibilityProcessor);
		ecs.addSystem(movementProcessor);
		ecs.addSystem(cameraProcessor);
		ecs.addSystem(controlProcessor);
		Gdx.input.setInputProcessor(controlProcessor);

		// Load assets
		assetManager.load(CREATURES_PATH, TextureAtlas.class);
		assetManager.finishLoading(); // Synchronous, pauses until everything loads
		CREATURES = assetManager.get(CREATURES_PATH, TextureAtlas.class);

		// Crappy Debug Wizard mans
		wizard = new Entity();
		wizard.add(new VisibleComponent());
		wizard.add(new TransformComponent());
		wizard.add(new MovementComponent());
		wizard.add(new ControllableComponent());
		ecs.addEntity(wizard);
		VisibleComponent wizardVisComp = ECSMapper.visible.get(wizard);
		wizardVisComp.image =CREATURES.findRegion("oryx_m_wizard");
		wizardVisComp.originX = wizardVisComp.image.getRegionWidth() / 2;
		wizardVisComp.originY = wizardVisComp.image.getRegionHeight() / 2;

		createCamera(wizard);

		//Start calculating game time
		startTime = System.currentTimeMillis();

	}

	@Override
	public void render () {
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
