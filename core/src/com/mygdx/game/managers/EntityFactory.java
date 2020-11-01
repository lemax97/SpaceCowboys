package com.mygdx.game.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.bullet.MotionState;
import com.mygdx.game.components.BulletComponent;
import com.mygdx.game.components.ModelComponent;

public class EntityFactory {

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
