package com.warsheep.scamp;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.warsheep.scamp.components.ECSMapper;
import com.warsheep.scamp.components.VisibleComponent;

public class Scamp extends Game {
	public static final String TITLE="SCAMP";
	public static final String CREATURES_PATH = "creatures_24x24/creatures.atlas";
    public static final int V_WIDTH=320,V_HEIGHT=240; // Internal dimensions in pixels
	AssetManager assetManager = new AssetManager();
	public static TextureAtlas CREATURES;
	VisibilityProcessor visibilityProcessor;

	Engine ecs; //Ashley Entity-Component System
	Entity wizard;

	@Override
	public void create () {
		ecs = new Engine();
		visibilityProcessor = new VisibilityProcessor();
		ecs.addSystem(visibilityProcessor);
		assetManager.load(CREATURES_PATH, TextureAtlas.class);
		assetManager.finishLoading();
		CREATURES = assetManager.get(CREATURES_PATH, TextureAtlas.class);
		wizard = new Entity();
		wizard.add(new VisibleComponent());
		ecs.addEntity(wizard);
		VisibleComponent wizardVisComp = ECSMapper.visible.get(wizard);
		wizardVisComp.image =CREATURES.findRegion("oryx_m_wizard");
		wizardVisComp.originX = wizardVisComp.image.getRegionWidth() / 2;
		wizardVisComp.originY = wizardVisComp.image.getRegionHeight() / 2;
	}

	@Override
	public void render () {
		visibilityProcessor.process();
	}
}
