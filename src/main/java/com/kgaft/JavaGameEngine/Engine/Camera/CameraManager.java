package com.kgaft.JavaGameEngine.Engine.Camera;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private List<Camera> cameras = new ArrayList<>();
    private Camera currentCamera;

    public void switchToCamera(int index){
        currentCamera = cameras.get(index);
    }

    public void registerCamera(Camera camera){
        cameras.add(camera);
    }
    public void registerA
}
