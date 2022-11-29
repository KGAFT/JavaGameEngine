package com.kgaft.JavaGameEngine.Engine.GameObjects.Scene;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private List<CameraChangedCallBack> camerasChangedCallBacks = new ArrayList<>();
    private Camera currentCamera;

    public CameraManager(Camera camera) {
        this.currentCamera = camera;
    }

    public CameraManager() {
        this.currentCamera = new Camera();
        this.currentCamera.setFov(75);
        this.currentCamera.setNearPlane(0.1f);
        this.currentCamera.setFarPlane(150.0f);
    }

    public void update() {
        float[] currentCameraData = new float[4 * 4];
        Matrix4f matrix4f = currentCamera.getCameraMatrix(Window.getWindow().getWidth(), Window.getWindow().getHeight());
        matrix4f.get(currentCameraData);
        Shader.uniformVector3f(currentCamera.getPosition(), "cameraPosition");
        Shader.uniformMatrix4f(currentCameraData, "cameraMatrix");
    }

    public List<CameraChangedCallBack> getCamerasChangedCallBacks() {
        return camerasChangedCallBacks;
    }

    public void setCamerasChangedCallBacks(List<CameraChangedCallBack> camerasChangedCallBacks) {
        this.camerasChangedCallBacks = camerasChangedCallBacks;
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public void setCurrentCamera(Camera currentCamera) {
        this.currentCamera = currentCamera;
    }
}
