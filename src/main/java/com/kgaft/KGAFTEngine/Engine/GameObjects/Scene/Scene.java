package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene;

import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Camera.CameraManager;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Lighting.LightManager;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.PhysicsManager;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Window.Window;


import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    private CameraManager cameraManager = new CameraManager();
    private LightManager lightManager = new LightManager();
    private List<RenderTarget> targetsToDraw = new ArrayList<>();
    private Window window;
    private PhysicsManager physicsManager = new PhysicsManager();


    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public LightManager getLightManager() {
        return lightManager;
    }

    protected void addRenderTarget(RenderTarget renderTarget) {
        targetsToDraw.add(renderTarget);
    }

    protected void excludeRenderTarget(RenderTarget renderTarget) {
        targetsToDraw.remove(renderTarget);
    }

    public List<RenderTarget> getTargetsToDraw() {
        return targetsToDraw;
    }

    public void setup() {

    }

    public PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    public void update(){
        cameraManager.update();
        lightManager.update();
        physicsManager.update();
    }

    public void cleanUp() {
        targetsToDraw.forEach(RenderTarget::destroy);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }


}
