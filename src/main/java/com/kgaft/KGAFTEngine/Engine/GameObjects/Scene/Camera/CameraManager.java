package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Camera;


import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Window.Window;
import org.joml.Matrix4f;



public class CameraManager {
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


    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public void setCurrentCamera(Camera currentCamera) {
        this.currentCamera = currentCamera;
    }
}
