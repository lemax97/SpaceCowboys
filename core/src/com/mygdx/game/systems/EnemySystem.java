package com.mygdx.game.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.GameWorld;
import com.mygdx.game.components.*;
import com.mygdx.game.managers.EntityFactory;

import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class EnemySystem extends EntitySystem implements EntityListener {

    private ImmutableArray<Entity> entities;
    private Entity player;
    private Quaternion quat = new Quaternion();
    private Engine engine;
    private GameWorld gameWorld;
    ComponentMapper<CharacterComponent> cm = ComponentMapper.getFor(CharacterComponent.class);
    public EnemySystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    private float[] xSpawns = {12, -12, 40, -40};
    private float[] zSpawns = {-40, 40, -12, 12};

    @Override
    public void addedToEngine(Engine e) {
        entities = e.getEntitiesFor(Family.all(EnemyComponent.class, CharacterComponent.class).get());
        e.addEntityListener(Family.one(PlayerComponent.class).get(), this);
        this.engine = e;
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    @Override
    public void update(float delta) {

        if (entities.size() < 1) spawnEnemy(getRandomSpawnIndex());

        for (Entity e : entities) {
            ModelComponent mod =
                    e.getComponent(ModelComponent.class);
            ModelComponent playerModel =
                    player.getComponent(ModelComponent.class);

            //if it's not alive, we will update the model component and it will starting fading
            //out the enemy model

            if (!e.getComponent(StatusComponent.class).alive)
                mod.update(delta);

            if (!e.getComponent(StatusComponent.class).alive && !e.getComponent(DieParticleComponent.class).used){
                //it will play the same effect a bunch of times
                e.getComponent(DieParticleComponent.class).used = true;
                ParticleEffect effect = e.getComponent(DieParticleComponent.class).originalEffect.copy();
                //set the emission mode to play once -- for its live time
                //(set in the particle editor by default to 3 seconds)
                ((RegularEmitter)effect.getControllers().first().emitter)
                        .setEmissionMode(RegularEmitter.EmissionMode.EnabledUntilCycleEnd);
                effect.setTransform(e.getComponent(ModelComponent.class).instance.transform);
                effect.scale(3.25f, 1, 1.5f);
                effect.init();
                effect.start();
                RenderSystem.particleSystem.add(effect);
            }

            if (!e.getComponent(StatusComponent.class).alive) return;

            Vector3 playerPosition = new Vector3();
            Vector3 enemyPosition = new Vector3();
            playerPosition = playerModel.instance.transform.getTranslation(playerPosition);
            enemyPosition = mod.instance.transform.getTranslation(enemyPosition);
            float dX = playerPosition.x - enemyPosition.x;
            float dZ = playerPosition.z  - enemyPosition.z;
            float theta = (float) (Math.atan2(dX, dZ));

            // Calculate the transforms
            Quaternion rot = quat.setFromAxis(0, 1, 0, (float) Math.toDegrees(theta) + 90);

            // Walk
            Matrix4 ghost = new Matrix4();
            Vector3 translation = new Vector3();
            cm.get(e).ghostObject.getWorldTransform(ghost);
            ghost.getTranslation(translation);
            mod.instance.transform.set(translation.x, translation.y, translation.z,
                    rot.x, rot.y, rot.z, rot.w);

            cm.get(e).characterDirection.set(-1, 0, 0).rot(mod.instance.transform);
            cm.get(e).walkDirection.set(0,0,0);
            cm.get(e).walkDirection.add(cm.get(e).characterDirection);
            cm.get(e).walkDirection.scl(3f * delta);
            cm.get(e).characterController.setWalkDirection(cm.get(e).walkDirection);
        }


    }

    public int getRandomSpawnIndex() {
        return random.nextInt(xSpawns.length);
    }

    private void spawnEnemy(int randomSpawnIndex){

        engine.addEntity(EntityFactory.createEnemy(gameWorld.bulletSystem, xSpawns[randomSpawnIndex], 33, zSpawns[randomSpawnIndex]));
//        engine.addEntity(EntityFactory.createEnemy(gameWorld.bulletSystem,random.nextInt(40) - 20, 10, random.nextInt(40) - 20));
    }


}
