package com.kgaft.JavaGameEngine.Engine.Camera;


import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private Vector3f position = new Vector3f(0.0f, 0.0f, 2.0f);
    private Vector3f orientation = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);


    public void move(float forwardBackWardAmplifier, float leftRightAmplifier, float upDownAmplifier){
        position.add(new Vector3f(orientation).mul(forwardBackWardAmplifier));
        position.add(new Vector3f(orientation).cross(new Vector3f(up)).normalize().mul(leftRightAmplifier*-1));
        position.add(new Vector3f(up).mul(upDownAmplifier));
    }
    public void rotate(float xSpeed, float ySpeed){
        Vector3f newOrientation = new Vector3f(orientation).rotateY((float) Math.toRadians(ySpeed), new Vector3f(orientation).cross(new Vector3f(up)).normalize());
        if(Math.abs(newOrientation.angle(up))<=90){
            orientation = newOrientation;
        }
        orientation = orientation.rotateX((float) Math.toRadians(xSpeed), new Vector3f(up));
    }
    Matrix4f getCameraMatrix(float fovInDegrees, float nearPlane, float farPlane, float viewPortWidth, float viewPortHeight){
        Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fovInDegrees), viewPortWidth/viewPortHeight, nearPlane, farPlane);
        Matrix4f viewMatrix = new Matrix4f().lookAt(new Vector3f(position), new Vector3f(position).add(new Vector3f(orientation)), new Vector3f(up));
        return projectionMatrix.mul(viewMatrix);
    }

}
