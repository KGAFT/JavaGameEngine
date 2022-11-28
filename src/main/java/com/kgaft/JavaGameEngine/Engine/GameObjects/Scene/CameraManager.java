package com.kgaft.JavaGameEngine.Engine.GameObjects.Scene;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private List<Camera> cameras = new ArrayList<>();
    private List<CameraChangedCallBack> camerasChangedCallBacks = new ArrayList<>();

}
