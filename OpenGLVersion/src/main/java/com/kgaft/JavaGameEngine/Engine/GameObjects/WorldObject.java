package com.kgaft.JavaGameEngine.Engine.GameObjects;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class WorldObject implements NonPhysicMoveAbleObject{
    protected Matrix4f worldMatrix = new Matrix4f().identity();
    private Vector3f position = new Vector3f();


    public Vector3f getPosition(){
        Vector4f tempPosition = new Vector4f(position, 1.0f).mul(worldMatrix);
        position = new Vector3f(tempPosition.x, tempPosition.y, tempPosition.z);
        return position;
    }
    @Override
    public void rotate(Vector3f rotation) {
        worldMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
    }

    @Override
    public void move(Vector3f move) {
        worldMatrix.translate(position);
    }

    public void scale(Vector3f scale) {
        worldMatrix.scale(scale);
    }
}
