package com.kgaft.KGAFTEngine.Engine.Utils;

import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.RigidBody;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class CollisionLoader {
    public List<RigidBody> loadColission(String modelPath, float mass, Vector3f inertiaDirections, Vector3f position, Quat4f rotation) {
        AIScene scene = Assimp.aiImportFile(modelPath, Assimp.aiProcess_FlipUVs | Assimp.aiProcess_Triangulate);
        System.out.println(Assimp.aiGetErrorString());
        return processScene(scene, mass, inertiaDirections, position, rotation);
    }

    private List<RigidBody> processScene(AIScene scene, float mass, Vector3f inertiaDirections, Vector3f position, Quat4f rotation) {
        List<RigidBody> rigidBodies = new ArrayList<>();
        PointerBuffer meshes = scene.mMeshes();
        for (int c = 0; c < scene.mNumMeshes(); c++) {
            AIMesh mesh = AIMesh.create(meshes.get(c));
            rigidBodies.add(processMesh(mesh, mass, inertiaDirections, position, rotation));
        }
        return rigidBodies;
    }

    private RigidBody processMesh(AIMesh mesh, float mass, Vector3f inertiaDirections, Vector3f position, Quat4f rotation) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        AIVector3D.Buffer verticesBuf = mesh.mVertices();
        while (verticesBuf.remaining() > 0) {
            AIVector3D vector3D = verticesBuf.get();
            positions.add(vector3D.x());
            positions.add(vector3D.y());
            positions.add(vector3D.z());
        }
        AIFace.Buffer buffer = mesh.mFaces();
        while (buffer.remaining() > 0) {
            IntBuffer index = buffer.get().mIndices();
            while (index.remaining() > 0) {
                indices.add(index.get());
            }
        }

        ObjectArrayList<Vector3f> collisionShapeDescription = prepareVerticesForBullet(positions, indices);
        ConvexHullShape convexHullShape = new ConvexHullShape(collisionShapeDescription);
        convexHullShape.calculateLocalInertia(mass, inertiaDirections);
        MotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(rotation, position, 1.0f)));
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, convexHullShape, inertiaDirections);
        constructionInfo.restitution = 0.5f;
        constructionInfo.angularDamping = 0.95f;
        return new RigidBody(motionState, new com.bulletphysics.dynamics.RigidBody(constructionInfo), constructionInfo);
    }

    private ObjectArrayList<Vector3f> prepareVerticesForBullet(List<Float> positions, List<Integer> indices) {
        ObjectArrayList<Vector3f> result = new ObjectArrayList<>();
        for (Integer index : indices) {
            Vector3f vertex = new Vector3f();
            vertex.x = positions.get(index);
            vertex.y = positions.get(index + 1);
            vertex.z = positions.get(index + 2);
            result.add(vertex);
        }
        return result;
    }

}
