package com.kgaft.JavaGameEngine.Engine.GameObjects.Scene;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<SceneObject> sceneObjects = new ArrayList<>();
    private LightManager lightManager = new LightManager();

    private CameraManager cameraManager = new CameraManager();

    public void setup(){
        sceneObjects.forEach(object->{
            object.setup();
        });
    }

    public void update(){
        cameraManager.update();
        lightManager.update();
        sceneObjects.forEach(object->{
            if(object.enabled){
                object.draw();
            }
        });
    }

    protected void addSceneObject(SceneObject sceneObject){
        sceneObjects.add(sceneObject);
    }

    protected LightManager getLightManager(){
        return lightManager;
    }

    protected CameraManager getCameraManager(){
        return cameraManager;
    }
    protected void registerSceneObject(SceneObject object){
        sceneObjects.add(object);
    }
    protected void removeSceneObject(SceneObject object){
        sceneObjects.remove(object);
    }
    protected void turnOffObjectFromRenderStack(SceneObject object){
        if(sceneObjects.contains(object)){
            object.enabled = false;
        }
    }
    protected void turnOnObjectFromRenderStack(SceneObject object){
        if(sceneObjects.contains(object)){
            object.enabled = true;
        }
    }


}
