package com.warsheep.scamp.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.warsheep.scamp.AssetDepot;
import com.warsheep.scamp.StateSignal;
import com.warsheep.scamp.TurnSystem;
import com.warsheep.scamp.components.*;
import com.warsheep.scamp.processors.ControlProcessor;
import com.warsheep.scamp.processors.StateProcessor;

import java.util.ArrayList;

public class MainGameScreenUI implements ControlProcessor.ControlListener, StateProcessor.StateListener {
    private SpriteBatch batcher;
    private BitmapFont font;
    ShapeRenderer shapeRenderer;

    private Entity currentEntity;
    private static int damage = 0;
    private static int currentHealth = 0;
    private static int maxHealth = 0;
    private static int level = 0;
    private static int currentExp = 0;
    private static int nextLevelExp = 0;
    private static int previousLevelExp = 0;
    private static int moves = 0;
    private static int range = 0;
    private float selectedActorIconTimer = 0;
    private float selectedActorIconOffsetY = 6;
    private ArrayList<Entity> spells;
    private Array<Vector2> selectedMoves;
    private Array<Vector2> selectedAttacks;
    private TurnSystem turnSystem;
    private TextureAtlas.AtlasRegion currentEntityImage;
    private AssetDepot assets = AssetDepot.getInstance();
    private TextureAtlas.AtlasRegion attackIcon = assets.fetchImage("icons_26x28", "oryx_attack_icon");
    private TextureAtlas.AtlasRegion turnIcon = assets.fetchImage("world_24x24", "blank");
    private TextureAtlas.AtlasRegion selectedActorIcon = assets.fetchImage("icons_26x28", "oryx_round_planning");
    private TextureAtlas.AtlasRegion damageIcon = assets.fetchImage("icons_26x28", "oryx_icon_damage");
    private TextureAtlas.AtlasRegion healthIcon = assets.fetchImage("icons_26x28", "oryx_icon_health");
    private TextureAtlas.AtlasRegion expIcon = assets.fetchImage("icons_26x28", "oryx_icon_exp");
    private TextureAtlas.AtlasRegion rangeIcon = assets.fetchImage("icons_26x28", "oryx_icon_range");
    private TextureAtlas.AtlasRegion levelIcon = assets.fetchImage("icons_26x28", "oryx_icon_level");
    private TextureAtlas.AtlasRegion movesIcon = assets.fetchImage("icons_26x28", "oryx_icon_moves");

    public MainGameScreenUI(TurnSystem turnSystem) {
        this.currentEntity = new Entity();
        this.batcher = new SpriteBatch();
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.turnSystem = turnSystem;
        this.selectedMoves = new Array();
        this.selectedAttacks = new Array();
        update();
    }

    public void drawHud(float delta) {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batcher.enableBlending();

        batcher.begin();
        addSpellGrid();
        addActiveActorIcon(delta);
        addPlayerStats();
        addMoveToPos();
        batcher.end();

        addTimeCircle(delta);

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void changeEntity(Entity entity) {
        this.currentEntity = entity;
        this.currentEntityImage = ECSMapper.visible.get(entity).image;
    }

    @Override
    public void selectMove(StateSignal signal) {
        int x;
        int y;
        if (this.selectedMoves.size == 0) {
            TileComponent t = ECSMapper.tile.get(signal.entity);
            x = t.x;
            y = t.y;
        } else {
            x = (int) this.selectedMoves.get(this.selectedMoves.size - 1).x / 24;
            y = (int) this.selectedMoves.get(this.selectedMoves.size - 1).y / 24;
        }

        switch (signal.direction) {
            case UP:
                y++;
                break;
            case DOWN:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        this.selectedMoves.add(new Vector2(x * 24.0f, y * 24.0f));
    }

    @Override
    public void selectAttack(StateSignal signal) {
        int x;
        int y;
        if (this.selectedMoves.size == 0) {
            TileComponent t = ECSMapper.tile.get(signal.entity);
            x = t.x;
            y = t.y;
        } else {
            x = (int) this.selectedMoves.get(this.selectedMoves.size - 1).x / 24;
            y = (int) this.selectedMoves.get(this.selectedMoves.size - 1).y / 24;
        }
        switch (signal.direction) {
            case UP:
                y++;
                break;
            case DOWN:
                y--;
                break;
            case LEFT:
                x--;
                break;
            case RIGHT:
                x++;
                break;
        }

        this.selectedAttacks.add(new Vector2(x * 24.0f, y * 24.0f));
    }

    @Override
    public void movingRoundEnd() {
        update();
        this.selectedMoves = new Array();
        this.turnIcon = assets.fetchImage("icons_26x28", "oryx_round_attack");
    }

    @Override
    public void combatRoundEnd() {
        update();
        this.selectedAttacks = new Array();
    }

    @Override
    public void planningRoundEnd() {
        update();
        this.turnIcon = assets.fetchImage("icons_26x28", "oryx_round_move");
    }

    @Override
    public void turnEnd() {
        update();
        this.turnIcon = assets.fetchImage("icons_26x28", "oryx_round_planning");
    }

    private void update() {

        LevelComponent lvlCmp = ECSMapper.level.get(currentEntity);
        DamageableComponent dmgCmp = ECSMapper.damage.get(currentEntity);
        AttackerComponent atkCmp = ECSMapper.attack.get(currentEntity);
        SpellbookComponent book = ECSMapper.spellBook.get(currentEntity);
        ControllableComponent control = ECSMapper.control.get(currentEntity);
        if (control != null) {
            moves = control.movementBonus + 1;
        }

        if (book != null && book.spellbook != null) {
            this.spells = book.spellbook;
        }

        if (lvlCmp != null) {
            if(lvlCmp.level > level) {
                previousLevelExp = nextLevelExp;
            }
            level = lvlCmp.level;
            currentExp = lvlCmp.experiencePoints;
            nextLevelExp = lvlCmp.nextLevelExp;
        }

        if (dmgCmp != null) {
            maxHealth = dmgCmp.maxHealth;
            currentHealth = dmgCmp.currentHealth;
        }

        if (atkCmp != null) {
            damage = atkCmp.baseDamage;
            range = atkCmp.attackRange;
        }
    }

    private void addMoveToPos() {

        batcher.enableBlending();
        for (Vector2 move : this.selectedMoves) {
            Vector3 pack = new Vector3(move.x, move.y, 0);
            pack = MainGameScreen.worldToScreen(pack);
            batcher.setColor(1, 1, 1, .5f);
            batcher.draw(currentEntity.getComponent(VisibleComponent.class).image, pack.x, pack.y + 5);
            batcher.setColor(1, 1, 1, 1);
        }
        for (Vector2 attack : this.selectedAttacks) {
            Vector3 pack = new Vector3(attack.x, attack.y, 0);
            pack = MainGameScreen.worldToScreen(pack);
            batcher.setColor(1, 1, 1, .5f);
            batcher.draw(attackIcon, pack.x, pack.y);
            batcher.setColor(1, 1, 1, 1);
        }
        batcher.disableBlending();
    }

    private void addActiveActorIcon(float delta) {
        this.selectedActorIconTimer += delta;
        if (this.selectedActorIconTimer >= .3) {
            if (selectedActorIconOffsetY == 6) {
                selectedActorIconOffsetY = 8;
            } else {
                selectedActorIconOffsetY = 6;
            }
            this.selectedActorIconTimer = 0;
        }
        if (turnSystem.isPlanningTurn()) {
            Vector3 actorCoord = ECSMapper.transform.get(this.currentEntity).position;

            Vector3 trans = new Vector3(actorCoord.x, actorCoord.y, 0);
            Vector3 screenCoord = MainGameScreen.worldToScreen(trans);

            batcher.setColor(1, 1, 1, .8f);
            batcher.draw(this.selectedActorIcon, screenCoord.x + 4, screenCoord.y + 28.0f + selectedActorIconOffsetY);
            batcher.setColor(1, 1, 1, 1);
        }
    }

    private void addSpellGrid() {

        if (this.spells != null) {

            for (int i = 0; i < this.spells.size(); i++) {
                IconComponent icon = ECSMapper.icon.get(spells.get(i));
                if (icon != null) {
                    EffectCooldownComponent cooldown = ECSMapper.cooldown.get(spells.get(i));

                    TextureAtlas.AtlasRegion tex = assets.fetchImage(icon.dir, icon.file);
                    if (cooldown.currentCooldown != 0) {
                        batcher.setColor(.5f, .5f, .5f, .5f);
                    }
                    batcher.draw(tex, Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 7, Gdx.graphics.getHeight() / 7 * (5 - i));
                    batcher.setColor(1, 1, 1, 1);
                    if (cooldown.currentCooldown != 0) {
                        font.draw(batcher, cooldown.currentCooldown + "",
                                Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 7, Gdx.graphics.getHeight() / 7 * (5 - i) + 10);
                    }
                }
            }
        }

    }

    private void addTimeCircle(float delta) {

        batcher.enableBlending();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int size = 12;

        if (turnSystem.isPlanningTurn()) {
            shapeRenderer.setColor(0, 1, 0, 0.5f);
        } else if (turnSystem.isMoveTurn()) {
            shapeRenderer.setColor(1, 0, 0, 0.5f);
        } else {
            shapeRenderer.setColor(0, 0, 0, 0.5f);
        }
        shapeRenderer.circle(Gdx.graphics.getWidth() - 15, Gdx.graphics.getHeight() - 15, size);
        shapeRenderer.end();
        batcher.begin();
        if (this.turnIcon != null) {
            batcher.draw(this.turnIcon, Gdx.graphics.getWidth() - 22, Gdx.graphics.getHeight() - 22);
        }
        batcher.end();
        batcher.disableBlending();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void addPlayerStats() {
        int i = 2;

        batcher.draw(levelIcon, 10, Gdx.graphics.getHeight() - 16 * i);
        font.draw(batcher, level + "", 28, Gdx.graphics.getHeight() - 16 * (i - 1));
        i++;

        batcher.draw(healthIcon, 10, Gdx.graphics.getHeight() - 16 * i);
        font.draw(batcher, currentHealth + "/" + maxHealth, 28, Gdx.graphics.getHeight() - 16 * (i - 1));
        i++;

        batcher.draw(damageIcon, 10, Gdx.graphics.getHeight() - 16 * i);
        font.draw(batcher, damage + "", 28, Gdx.graphics.getHeight() - 16 * (i - 1));
        i++;

        batcher.draw(rangeIcon, 10, Gdx.graphics.getHeight() - 16 * i);
        font.draw(batcher, range + "", 28, Gdx.graphics.getHeight() - 16 * (i - 1));
        i++;

        batcher.draw(movesIcon, 10, Gdx.graphics.getHeight() - 16 * i);
        font.draw(batcher, moves + "", 28, Gdx.graphics.getHeight() - 16 * (i - 1));
        i++;

        if(currentExp != 0 && nextLevelExp != 0) {
            batcher.draw(expIcon, 10, Gdx.graphics.getHeight() - 16 * i);
            font.draw(batcher, (int)(((float) (currentExp - previousLevelExp) / (nextLevelExp - previousLevelExp)) * 100) / 1 + "%", 28, Gdx.graphics.getHeight() - 16 * (i - 1));
            i++;
        }
    }
}
