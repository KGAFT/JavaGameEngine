package com.kgaft.JavaGameEngine.Engine.Camera;

import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.KeyBoardCallBack;
import com.kgaft.JavaGameEngine.Window.MouseMovementCallBack;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private List<Camera> cameras = new ArrayList<>();
    private Camera currentCamera;

    private boolean callBackRegistered = false;

    private int fov = 75;
    private float nearPlane = 0.1f;
    private float farPlane = 100.0f;

    private float[] currentCamData = new float[4*4];

    public void switchToCamera(int index) {
        currentCamera = cameras.get(index);
    }

    public void registerCamera(Camera camera) {
        cameras.add(camera);
    }

    public void registerCameraAndSwitchToIt(Camera camera) {
        cameras.add(camera);
        currentCamera = camera;
    }
    public void handleCamera(){
        Matrix4f matrix4f = currentCamera.getCameraMatrix(fov, nearPlane, farPlane, Window.getWindow().getWidth(), Window.getWindow().getHeight());
        matrix4f.get(currentCamData);

        Shader.uniformMatrix4f(currentCamData, "cameraMatrix");

    }

    public int getFov() {
        return fov;
    }

    public void setFov(int fov) {
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
