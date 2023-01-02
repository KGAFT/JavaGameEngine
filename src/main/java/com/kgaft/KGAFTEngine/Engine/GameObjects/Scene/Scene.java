package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene;

import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Camera.CameraManager;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Lighting.LightManager;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.IRenderTarget;
import com.kgaft.KGAFTEngine.Window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    private CameraManager cameraManager = new CameraManager();
    private LightManager lightManager = new LightManager();
    private List<IRenderTarget> targetsToDraw = new ArrayList<>();

    private Window window;

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public LightManager getLightManager() {
        return lightManager;
    }
    protected void addRederTarget(IRenderTarget renderTarget){
        targetsToDraw.add(renderTarget);
    }
    protected void excludeRenderTarget(IRenderTarget renderTarget){
        targetsToDraw.remove(renderTarget);
    }

    public List<IRenderTarget> getTargetsToDraw() {
        return targetsToDraw;
    }
    public abstract void setup();
    public abstract void update();
    public void cleanUp(){
        targetsToDraw.forEach(IRenderTarget::destroy);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
