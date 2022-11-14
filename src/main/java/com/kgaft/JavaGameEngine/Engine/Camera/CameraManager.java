package com.kgaft.JavaGameEngine.Engine.Camera;

import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private static List<Camera> cameras = new ArrayList<>();
    private static Camera currentCamera;

    private static float[] currentCamData = new float[4*4];

    public static void switchToCamera(int index) {
        currentCamera = cameras.get(index);
    }

    public static void registerCamera(Camera camera) {
        cameras.add(camera);
    }

    public static void registerCameraAndSwitchToIt(Camera camera) {
        cameras.add(camera);
        currentCamera = camera;
    }
    public static void handleCamera(){
        new Thread(()->{
            Matrix4f matrix4f = currentCamera.getCameraMatrix(75, 0.1f, 100.0f, Window.getWindow().getWidth(), Window.getWindow().getHeight());
            matrix4f.get(currentCamData);

        }).start();
        Shader.uniformMatrix4f(currentCamData, "cameraMatrix");
    }
}
