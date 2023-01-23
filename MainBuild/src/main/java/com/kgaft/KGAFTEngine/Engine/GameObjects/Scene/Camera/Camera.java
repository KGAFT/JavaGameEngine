package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Camera;



import com.kgaft.KGAFTEngine.Engine.GameObjects.NonPhysicMoveAbleObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera implements NonPhysicMoveAbleObject {
    private float fov;
    private float nearPlane;
    private float farPlane;
    private Vector3f position = new Vector3f(0.0f, 0.0f, 2.0f);
    private Vector3f rotation = new Vector3f(0.0f, 0.0f, -1.0f);

    @Override
    public void move(Vector3f move){

        if ( move.x != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * move.x;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * move.x*-1.0f;
        }
        if ( move.z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * move.z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * move.z*-1.0f;
        }
        position.y += move.y;
    }
    @Override
    public void rotate(Vector3f rotation){
        this.rotation = new Vector3f(this.rotation.x+rotation.x, this.rotation.y+rotation.y, -1.0f);

    }
    public Matrix4f getCameraMatrix(float viewPortWidth, float viewPortHeight){

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-position.x, -position.y, -position.z);
        Matrix4f projectionMatrix = new Matrix4f().identity();
        float aspectRatio = viewPortWidth / viewPortHeight;
        projectionMatrix.identity();
        projectionMatrix.perspective((float) Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
        return projectionMatrix.mul(viewMatrix);

    }


    public void setPosition(Vector3f position) {
        this.position = position;
    }


    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }




    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getPosition() {
        return position;
    }



    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }
}
