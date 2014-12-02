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
    public static final int V_WIDTH=320,V_HEIGHT=240; // Internal dimensions in pixels

	Engine ecs; // Ashley Entity-Component System
	VisibilityProcessor visibilityProcessor;
	CollisionProcessor collisionProcessor;
	MovementProcessor movementProcessor;
	ControlProcessor controlProcessor;
	CameraProcessor cameraProcessor;
	DeathProcessor deathProcessor;
	CombatProcessor combatProcessor;
	TileProcessor tileProcessor;

	AssetManager assetManager = new AssetManager();
	public static TextureAtlas CREATURES;
	public static TextureAtlas WORLD;

	Entity wizard;

	private long startTime;
	private long delta;

	@Override
	public void create () {
		ecs = new Engine();

		// Initialize processors and associate them with ecs engine
		visibilityProcessor = new VisibilityProcessor();
		collisionProcessor = new CollisionProcessor(1);
		movementProcessor = new MovementProcessor(2);
		controlProcessor = new ControlProcessor();
		cameraProcessor = new CameraProcessor();
		deathProcessor = new DeathProcessor();
		combatProcessor = new CombatProcessor(3);
		ecs.addSystem(visibilityProcessor);
		ecs.addSystem(collisionProcessor);
		tileProcessor = new TileProcessor();
		ecs.addSystem(visibilityProcessor);
		ecs.addSystem(collisionProcessor);
		ecs.addSystem(tileProcessor);
		ecs.addSystem(movementProcessor);
		ecs.addSystem(cameraProcessor);
		ecs.addSystem(controlProcessor);
		ecs.addSystem(deathProcessor);
		ecs.addSystem(combatProcessor);
		Gdx.input.setInputProcessor(controlProcessor);

		// Load assets
		assetManager.load(CREATURES_PATH, TextureAtlas.class);
//		assetManager.load(WORLD_PATH, TextureAtlas.class);
		assetManager.finishLoading(); // Synchronous, pauses until everything loads
		CREATURES = assetManager.get(CREATURES_PATH, TextureAtlas.class);
//		WORLD = assetManager.get(WORLD_PATH, TextureAtlas.class);

		// Crappy Debug PROCEDURAL DEATH LABYRINTH
//		Entity tile1 = new Entity();
//		tile1.add(new TileComponent());
//		tile1.add(new TransformComponent());
//		tile1.add(new VisibleComponent());
//		ECSMapper.visible.get(tile1).image = WORLD.findRegion("oryx_floor_darkgrey_stone");
//		ECSMapper.tile.get(tile1).x = 0;
//		ECSMapper.tile.get(tile1).y = 0;
//		ecs.addEntity(tile1);
//
//		Entity tile2 = new Entity();
//		tile2.add(new TileComponent());
//		tile2.add(new TransformComponent());
//		tile2.add(new VisibleComponent());
//		ECSMapper.visible.get(tile2).image = WORLD.findRegion("oryx_floor_darkgrey_stone", 2);
//		ECSMapper.tile.get(tile2).x = 1;
//		ECSMapper.tile.get(tile2).y = 0;
//		ecs.addEntity(tile2);
//
//		Entity tile3 = new Entity();
//		tile3.add(new TileComponent());
//		tile3.add(new TransformComponent());
//		tile3.add(new VisibleComponent());
//		ECSMapper.visible.get(tile3).image = WORLD.findRegion("oryx_floor_darkgrey_stone");
//		ECSMapper.tile.get(tile3).x = 0;
//		ECSMapper.tile.get(tile3).y = 1;
//		ecs.addEntity(tile3);
//
//		Entity tile4 = new Entity();
//		tile4.add(new TileComponent());
//		tile4.add(new TransformComponent());
//		tile4.add(new VisibleComponent());
//		ECSMapper.visible.get(tile4).image = WORLD.findRegion("oryx_floor_darkgrey_stone", 2);
//		ECSMapper.tile.get(tile4).x = 1;
//		ECSMapper.tile.get(tile4).y = 1;
//		ecs.addEntity(tile4);

		// Skeleton blocker of doom
		Entity skeleton = new Entity();
		skeleton.add(new VisibleComponent());
		skeleton.add(new TransformComponent());
		skeleton.add(new CollidableComponent());
		skeleton.add(new DamageableComponent());
		skeleton.add(new TilePositionComponent());
		ecs.addEntity(skeleton);
		VisibleComponent skeletonVisComp = ECSMapper.visible.get(skeleton);
		skeletonVisComp.image = CREATURES.findRegion("oryx_n_skeleton");
		skeletonVisComp.originY = skeletonVisComp.image.getRegionHeight() / 2;
		skeletonVisComp.originX = skeletonVisComp.image.getRegionWidth() / 2;
		ECSMapper.transform.get(skeleton).position.y = 48;
		ECSMapper.transform.get(skeleton).position.x = 48;
		ECSMapper.tilePosition.get(skeleton).y = 2;
		ECSMapper.tilePosition.get(skeleton).x = 2;

		// Crappy Debug Wizard mans
		wizard = new Entity();
		wizard.add(new VisibleComponent());
		wizard.add(new TransformComponent());
		wizard.add(new MovementComponent());
		wizard.add(new CollidableComponent());
		wizard.add(new ControllableComponent());
		wizard.add(new AttackerComponent());
		wizard.add(new DamageableComponent());
		wizard.add(new TilePositionComponent());
		ecs.addEntity(wizard);

		DamageableComponent dmgComp = ECSMapper.damage.get(wizard);
		dmgComp.essential = true;

		VisibleComponent wizardVisComp = ECSMapper.visible.get(wizard);
		wizardVisComp.image = CREATURES.findRegion("oryx_m_wizard");
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

	@Override
	public void pause() {
		super.pause();
	}
}
