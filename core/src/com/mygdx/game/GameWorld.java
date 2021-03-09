package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.components.CharacterComponent;
import com.mygdx.game.managers.EntityFactory;
import com.mygdx.game.systems.*;

public class GameWorld {

    private static final boolean debug = false;
    private DebugDrawer debugDrawer;
    private Engine engine;
    private Entity character, gun;
    public BulletSystem bulletSystem;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public PlayerSystem playerSystem;
    private RenderSystem renderSystem;
    private Entity dome;

    Model wallHorizontal = modelBuilder.createBox(40, 20, 1,
            new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.RED),
                    FloatAttribute.createShininess(16f)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    Model wallVertical = modelBuilder.createBox(1, 20, 40,
            new Material(ColorAttribute.createDiffuse(Color.GREEN), ColorAttribute.createSpecular(Color.WHITE),
                    FloatAttribute.createShininess(16f)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    Model groundModel = modelBuilder.createBox(40, 1, 40,
            new Material(ColorAttribute.createDiffuse(Color.YELLOW), ColorAttribute.createSpecular(Color.BLUE),
                    FloatAttribute.createShininess(16f)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

    public GameWorld(GameUI gameUI) {
        Bullet.init();
        setDebug();
        addSystems(gameUI);
        addEntities();
    }

    private void setDebug() {
        if (debug) {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    private void addEntities(){
        loadLevel();
        createPlayer(0, 0, 0);
    }

    private void loadLevel() {
//        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        engine.addEntity(dome = EntityFactory.loadDome(0, 0, 0));
        PlayerSystem.dome = dome;
    }

    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(bulletSystem, x, y, z);
        engine.addEntity(character);
        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4.0f));
        playerSystem.gun = gun;
        renderSystem.gun = gun;
    }

//    private void createGround(){
//        engine.addEntity(EntityFactory.createStaticEntity(groundModel, 0, 0, 0));
//        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, -20));
//        engine.addEntity(EntityFactory.createStaticEntity(wallHorizontal, 0, 10, 20));
//        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, 20, 10, 0));
//        engine.addEntity(EntityFactory.createStaticEntity(wallVertical, -20, 10, 0));
//    }

    private void addSystems(GameUI gameUI){
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        EntityFactory.renderSystem = renderSystem;
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(playerSystem = new PlayerSystem(this, renderSystem.perspectiveCamera, gameUI));
        engine.addSystem(new EnemySystem(this));
        engine.addSystem(new StatusSystem(this));
        if (debug) bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    public void render(float delta){
        renderWorld(delta);
        checkPause();
    }

    private void checkPause() {

        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
            engine.getSystem(EnemySystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        }
        else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
            engine.getSystem(EnemySystem.class).setProcessing(true);
            engine.getSystem(StatusSystem.class).setProcessing(true);
            engine.getSystem(BulletSystem.class).setProcessing(true);
        }
    }

    protected void renderWorld(float delta){
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.perspectiveCamera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();
        bulletSystem = null;
        renderSystem.dispose();
        wallHorizontal.dispose();
        wallVertical.dispose();
        groundModel.dispose();
        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}
