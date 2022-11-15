package com.kgaft.JavaGameEngine.Engine.Camera;

import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.KeyBoardCallBack;
import com.kgaft.JavaGameEngine.Window.MouseMovementCallBack;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CameraManager implements MouseMovementCallBack, KeyBoardCallBack {
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
        if(!callBackRegistered){
            Window.getWindow().addKeyBoardCallBack(this);
            Window.getWindow().addMouseMoveCallBack(this);
        }
    }
    public void handleCamera(){
        new Thread(()->{
            Matrix4f matrix4f = currentCamera.getCameraMatrix(fov, nearPlane, farPlane, Window.getWindow().getWidth(), Window.getWindow().getHeight());
            matrix4f.get(currentCamData);
        }).start();
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

    @Override
    public int[] getKeyCodes() {
        return new int[]{GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_C};
    }

    @Override
    public void keyPressed(int keyCodeId) {
        switch(keyCodeId){
            case GLFW.GLFW_KEY_ENTER:
                Window.getWindow().setCursorMode(Window.FIXED_HIDDEN_CURSOR_MODE);
                break;
            case GLFW.GLFW_KEY_ESCAPE:
                Window.getWindow().setCursorMode(Window.DYNAMIC_CURSOR);
                break;
            case GLFW.GLFW_KEY_W:
                currentCamera.move(1, 0, 0);
                break;
            case GLFW.GLFW_KEY_A:
                currentCamera.move(0, -1, 0);
                break;
            case GLFW.GLFW_KEY_S:
                currentCamera.move(-1, 0, 0);
                break;
            case GLFW.GLFW_KEY_D:
                currentCamera.move(0, 1, 0);
                break;
            case GLFW.GLFW_KEY_SPACE:
                currentCamera.move(0, 0, 1);
                break;
            case GLFW.GLFW_KEY_C:
                currentCamera.move(0, 0, -1);
                break;

        }
    }

    @Override
    public int getWorkMode() {
        return Window.FIXED_HIDDEN_CURSOR_MODE;
    }

    @Override
    public void mouseMoved(double x, double y) {
        currentCamera.rotate((float) x*10, (float)y*10);
    }
}
