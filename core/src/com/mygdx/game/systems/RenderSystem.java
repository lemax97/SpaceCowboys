package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.mygdx.game.Core;
import com.mygdx.game.Settings;
import com.mygdx.game.components.AnimationComponent;
import com.mygdx.game.components.GunComponent;
import com.mygdx.game.components.ModelComponent;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private ModelBatch batch;
    private Environment environment;

    private static final float FOV = 67F;
    public static PerspectiveCamera perspectiveCamera =
            new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
    public PerspectiveCamera gunCamera;
    public Entity gun;

    public RenderSystem(){
        setPerspectiveCamera();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1));


        batch = new ModelBatch();
        gunCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        gunCamera.far = 100f;
    }

    private void setPerspectiveCamera(){
        perspectiveCamera.far = 10000f;
        PerspectiveCamera tempCam = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);

        perspectiveCamera.up.set(tempCam.up);
        perspectiveCamera.position.set(tempCam.position);
        perspectiveCamera.view.set(tempCam.view);
        perspectiveCamera.combined.set(tempCam.combined);
        perspectiveCamera.direction.set(tempCam.direction);
        perspectiveCamera.invProjectionView.set(tempCam.invProjectionView);
        perspectiveCamera.near = tempCam.near;
        perspectiveCamera.projection.set(tempCam.projection);

    }

    @Override
    public void addedToEngine(Engine engine){
        //get a list of all the entities containing 'ModelComponent'
        entities = engine.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        drawModels(deltaTime);
    }

    private void drawModels(float delta) {
        batch.begin(perspectiveCamera);
//        for (int i = 0; i < entities.size(); i++) {
//            if (entities.get(i).getComponent(GunComponent.class) == null) {
//                ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
//                batch.render(mod.instance, environment);
//            }
//        }
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getComponent(GunComponent.class) == null) {
                ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
                batch.render(mod.instance, environment);
                if (entities.get(i).getComponent(AnimationComponent.class) != null && !Settings.Paused) {
                    entities.get(i).getComponent(AnimationComponent.class).update(delta);
                }
            }
        }
        batch.end();
//        renderParticleEffects();
        drawGun(delta);
    }

    private void drawGun(float delta){
        // clear the depth buffer; this is needed in order to display the gun with a different camera.

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(gunCamera);
        batch.render(gun.getComponent(ModelComponent.class).instance);
        gun.getComponent(AnimationComponent.class).update(delta);
        batch.end();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
        gunCamera.viewportHeight = height;
        gunCamera.viewportWidth = width;
    }

    public void dispose() {
        batch.dispose();
        batch = null;
    }
}
