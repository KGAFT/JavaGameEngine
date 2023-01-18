package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import org.joml.Matrix4f;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class PhysicsManager {

    private BroadphaseInterface broadphaseInterface;
    private CollisionConfiguration collisionConfiguration;
    private CollisionDispatcher collisionDispatcher;
    private ConstraintSolver constraintSolver;
    private DiscreteDynamicsWorld discreteDynamicsWorld;

    private List<IObjectWithPhysic> physicsObjects = new ArrayList<>();

    public PhysicsManager() {
        broadphaseInterface = new DbvtBroadphase();
        collisionConfiguration = new DefaultCollisionConfiguration();
        collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
        constraintSolver = new SequentialImpulseConstraintSolver();
        discreteDynamicsWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphaseInterface, constraintSolver, collisionConfiguration);
        discreteDynamicsWorld.setGravity(new Vector3f(0, -9.8f, 0));
    }

    public void addPhysicsObject(IObjectWithPhysic physicsObject) {
        if (!physicsObject.getRigidBody().isLoaded()) {
            discreteDynamicsWorld.addRigidBody(physicsObject.getRigidBody().getRigidBody());
        } else {
            throw new ConcurrentModificationException("Error: this object allready attached to another physics manager");
        }
        physicsObjects.add(physicsObject);
    }

    public void update() {
        physicsObjects.forEach(physicsObject -> {
            if (!physicsObject.getRigidBody().isLoaded()) {
                discreteDynamicsWorld.addRigidBody(physicsObject.getRigidBody().getRigidBody());
                physicsObject.getRigidBody().setLoaded(true);
            }
        });
        discreteDynamicsWorld.stepSimulation(1 / 60.0f);
        physicsObjects.forEach(physicsObject -> {
            if (!physicsObject.isExclude()) {
                float[] matrix = new float[4 * 4];
                physicsObject.getRigidBody().getMotionState().getWorldTransform(new Transform()).getOpenGLMatrix(matrix);
                physicsObject.setWorldMatrix(new Matrix4f(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], matrix[6], matrix[7], matrix[8], matrix[9], matrix[10], matrix[11], matrix[12], matrix[13], matrix[14], matrix[15]));
            }
        });
    }
}
