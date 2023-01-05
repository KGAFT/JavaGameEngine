package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics;

import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class RigidBody {
    private boolean loaded = false;
    private MotionState motionState;
    private com.bulletphysics.dynamics.RigidBody rigidBody;
    private RigidBodyConstructionInfo constructionInfo;

    public RigidBody(MotionState motionState, com.bulletphysics.dynamics.RigidBody rigidBody, RigidBodyConstructionInfo constructionInfo) {
        this.motionState = motionState;
        this.rigidBody = rigidBody;
        this.constructionInfo = constructionInfo;
    }
    public void setMass(float mass, Vector3f inertiaDirections){
        constructionInfo.collisionShape.calculateLocalInertia(mass, inertiaDirections);
        constructionInfo.mass = mass;
        rigidBody.destroy();
        rigidBody = new com.bulletphysics.dynamics.RigidBody(constructionInfo);
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public MotionState getMotionState() {
        return motionState;
    }

    public com.bulletphysics.dynamics.RigidBody getRigidBody() {
        return rigidBody;
    }
    public void setRotation(org.joml.Vector3f rotation){
        Transform transform = new Transform(new Matrix4f(new Quat4f(rotation.x, rotation.y, rotation.z, 0), rigidBody.getWorldTransform(new Transform()).origin, 1.0f));
        rigidBody.setWorldTransform(transform);
    }
    public void setPosition(org.joml.Vector3f position){
        Transform transform = new Transform(new Matrix4f(rigidBody.getOrientation(new Quat4f()), new Vector3f(position.x, position.y, position.z), 1.0f));
        rigidBody.setWorldTransform(transform);
    }
}
