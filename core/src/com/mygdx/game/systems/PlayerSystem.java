package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.mygdx.game.GameWorld;
import com.mygdx.game.Settings;
import com.mygdx.game.UI.GameUI;
import com.mygdx.game.components.*;
import com.mygdx.game.widgets.ControllerWidget;


public class PlayerSystem extends EntitySystem implements EntityListener, InputProcessor {

    public static Entity dome;
    private Entity player;
    public Entity gun;
    private PlayerComponent playerComponent;
    private GameUI gameUI;

    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private final Vector3 tmp = new Vector3();
    private final Camera camera;
    private GameWorld gameWorld;

    ClosestRayResultCallback rayTestCB;
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();

    public PlayerSystem(GameWorld gameWorld, Camera camera, GameUI gameUI) {
        this.gameWorld= gameWorld;
        this.camera = camera;
        this.gameUI = gameUI;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(),this);
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
    }

    @Override
    public void update(float delta) {
        if (player == null) return;
        updateMovement(delta);
        updateStatus();
        checkGameOver();
    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }

    private void updateStatus(){
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    private void updateMovement(float delta) {
        float deltaX = 0;
        float deltaY = 0;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            deltaX = -ControllerWidget.getWatchVector().x * 1.5f;
            deltaY = ControllerWidget.getWatchVector().y * 1.5f;
        } else {
            deltaX = -Gdx.input.getDeltaX() * 0.5f;
            deltaY = -Gdx.input.getDeltaY() * 0.5f;
        }
        tmp.set(0, 0, 0);
        camera.rotate(camera.up, deltaX);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, deltaY);
        tmp.set(0,0,0);
        //Move
        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0,0,0);

        if (Gdx.app.getType() == Application.ApplicationType.Android){
            if (ControllerWidget.getMovementVector().y > 0) characterComponent.walkDirection.add(camera.direction);
            if (ControllerWidget.getMovementVector().y < 0) characterComponent.walkDirection.sub(camera.direction);
            if (ControllerWidget.getMovementVector().x < 0) tmp.set(camera.direction).crs(camera.up).scl(-1);
            if (ControllerWidget.getMovementVector().x > 0) tmp.set(camera.direction).crs(camera.up);

        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) characterComponent.walkDirection.add(camera.direction);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) characterComponent.walkDirection.sub(camera.direction);
            if (Gdx.input.isKeyPressed(Input.Keys.A)) tmp.set(camera.direction).crs(camera.up).scl(-1);
            if (Gdx.input.isKeyPressed(Input.Keys.D)) tmp.set(camera.direction).crs(camera.up);
        }


        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(10f * delta);
        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);

        Matrix4 ghost = new Matrix4();
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);
        ghost.getTranslation(translation);
        modelComponent.instance.transform.set(translation.x, translation.y, translation.z,
                camera.direction.x, camera.direction.y, camera.direction.z, 0);

        // Lastly, we would like the camera position to be set to the player so that it becomes
        // first person.
        camera.position.set(translation.x, translation.y, translation.z);
        camera.update(true);

        dome.getComponent(ModelComponent.class).instance.transform.setToTranslation(translation.x, translation.y, translation.z);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            characterComponent.characterController.setJumpSpeed(25);
            characterComponent.characterController.jump(new Vector3(0, 25, 0));
        }

        if (Gdx.input.justTouched()) fire();
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    private void fire() {
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom); /* 50 meters max from the origin */
        /* Because we reuse the ClosestRayResultCallback, we need reset it's values */
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);
        gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);
        if (rayTestCB.hasHit()) {
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (((Entity) obj.userData).getComponent(EnemyComponent.class) != null ) {
                if (((Entity) obj.userData).getComponent(StatusComponent.class).alive) {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).setAlive(false);
                    PlayerComponent.score += 100;
                }
//                ((Entity)obj.userData).getComponent(StatusComponent.class).alive = false;

            }
        }

        gun.getComponent(AnimationComponent.class).animate("Armature|shoot", 1, 3);
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
