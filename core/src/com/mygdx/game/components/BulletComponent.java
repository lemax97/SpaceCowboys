package com.mygdx.game.components;

import com.mygdx.game.bullet.MotionState;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;


public class BulletComponent implements Component {
    public MotionState motionState;
    public btRigidBody.btRigidBodyConstructionInfo bodyInfo;
    public btCollisionObject body;
}
