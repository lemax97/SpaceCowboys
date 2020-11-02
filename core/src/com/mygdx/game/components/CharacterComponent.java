package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btActionInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;

public class CharacterComponent implements Component {
    public btKinematicCharacterController characterController;
    public btPairCachingGhostObject ghostObject;
    public btConvexShape ghostShape;

    public Vector3 characterDirection = new Vector3();
    public Vector3 walkDirection = new Vector3();
}
