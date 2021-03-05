package com.mygdx.game.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.JsonReader;
import com.mygdx.game.bullet.MotionState;
import com.mygdx.game.components.*;
import com.mygdx.game.systems.BulletSystem;
import com.mygdx.game.systems.RenderSystem;

public class EntityFactory {

    private static ModelBuilder modelBuilder;
    private static Texture playerTexture;
    private static Model playerModel;
    private static Model enemyModel;
    public static RenderSystem renderSystem;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        Material material = new Material(TextureAttribute.createDiffuse(playerTexture),
                ColorAttribute.createSpecular(1, 1, 1, 1),
                FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates);
    }

    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z){
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
//        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject,
//                                                    characterComponent.ghostShape, .35f);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject,
                characterComponent.ghostShape, .35f, new Vector3(0, 1f, 0));
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).ghostObject,
                (short)btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short)(btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).characterController);
        return entity;
    }

    public static Entity createPlayer(BulletSystem bulletSystem, float x, float y, float z){
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    public static Entity createEnemy(BulletSystem bulletSystem, float x, float y, float z){
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));
        entity.add(new StatusComponent());
        return entity;
    }

    public static Entity loadGun(float x, float y, float z){
        ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/gunmodel.g3dj"));
        Model model = new Model(modelData, new TextureProvider.FileTextureProvider());
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        modelComponent.instance.transform.rotate(0, 1, 0, 180);
//        modelComponent.instance.transform.translate(-2.5f, -2.5f, 4);
//        modelComponent.instance.transform.scale(0.008f, 0.008f, 0.008f);
        modelComponent.instance.transform.scale(0.02f, 0.02f, 0.02f);
        Entity gunEntity = new Entity();
        gunEntity.add(modelComponent);
        gunEntity.add(new GunComponent());
        gunEntity.add(new AnimationComponent(modelComponent.instance));
        return gunEntity;

//        ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
//        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/gunmodel.g3dj"));
//        Model model = new Model(modelData, new TextureProvider.FileTextureProvider());
//        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
//        modelComponent.instance.transform.rotate(0, 1, 0, 180);
////        modelComponent.instance.transform.translate(-2.5f, -2.5f, 4);
////        modelComponent.instance.transform.scale(0.008f, 0.008f, 0.008f);
//        modelComponent.instance.transform.scale(0.02f, 0.02f, 0.02f);
//        Entity gunEntity = new Entity();
//        gunEntity.add(modelComponent);
//        gunEntity.add(new GunComponent());
//        gunEntity.add(new AnimationComponent(modelComponent.instance));
//        return gunEntity;
    }

    public static Entity createStaticEntity(Model model, float x, float y, float z) {

        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f,
                boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col,
                Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody)bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }
}
