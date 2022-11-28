package com.kgaft.JavaGameEngine.Engine.GameObjects.Scene;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.DirectPbrLight;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.PointPbrLight;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private List<ShaderStruct> pointLights = new ArrayList<>();
    private List<ShaderStruct> directLights = new ArrayList<>();

    private Camera camera;

    protected void update() {
        Shader.uniformInt(pointLights.size(), "enabledPointLights");
        Shader.uniformInt(directLights.size(), "enabledDirectionalLights");
        Shader.uniformVector3f(camera.getPosition(), "cameraPosition");
        Shader.uniformArrayOfStructs(pointLights, "pointLights");
        Shader.uniformArrayOfStructs(directLights, "directLights");
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void addPointLight(PointPbrLight light) {
        this.pointLights.add(light);
    }

    public void addDirectLight(DirectPbrLight directPbrLight) {
        this.directLights.add(directPbrLight);
    }

    public void removePointLight(PointPbrLight light) {
        this.pointLights.remove(light);
    }

    public void removeDirectLight(DirectPbrLight light) {
        this.directLights.remove(light);
    }
}
