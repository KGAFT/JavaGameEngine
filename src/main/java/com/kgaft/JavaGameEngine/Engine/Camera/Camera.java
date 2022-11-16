package com.kgaft.JavaGameEngine.Engine.Camera;


import com.kgaft.JavaGameEngine.Engine.GameObjects.NonPhysicMoveAbleObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera implements NonPhysicMoveAbleObject {
    /*
    private Vector3f position = new Vector3f(0.0f, 0.0f, 2.0f);
    private Vector3f orientation = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

     */
    private final Vector3f position = new Vector3f(0.0f, 0.0f, 2.0f);
    private final Vector3f rotation = new Vector3f(0.0f, 0.0f, -1.0f);

    @Override
    public void move(float forwardBackWardAmplifier, float leftRightAmplifier, float upDownAmplifier){

        if ( forwardBackWardAmplifier != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * forwardBackWardAmplifier;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * forwardBackWardAmplifier*-1.0f;
        }
        if ( leftRightAmplifier != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * leftRightAmplifier;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * leftRightAmplifier*-1.0f;
        }
        position.y += upDownAmplifier;
    }
    @Override
    public void rotate(float xSpeed, float ySpeed){

        rotation.x += xSpeed*-1;
        rotation.y += ySpeed*-1;

    }
    Matrix4f getCameraMatrix(float fovInDegrees, float nearPlane, float farPlane, float viewPortWidth, float viewPortHeight){

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-position.x, -position.y, -position.z);
        Matrix4f projectionMatrix = new Matrix4f().identity();
        float aspectRatio = viewPortWidth / viewPortHeight;
        projectionMatrix.identity();
        projectionMatrix.perspective((float) Math.toRadians(fovInDegrees), aspectRatio, nearPlane, farPlane);
        return projectionMatrix.mul(viewMatrix);

    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getOrientation() {
        return rotation;
    }
}
