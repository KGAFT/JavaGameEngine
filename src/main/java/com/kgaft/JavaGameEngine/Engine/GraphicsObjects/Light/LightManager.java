package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private static ArrayList<PointLight> pointLights = new ArrayList<>();
    private static ArrayList<DirectLight> directLights = new ArrayList<>();
    private static ArrayList<SpotLight> spotLights = new ArrayList<>();
    private static Camera camera;

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        LightManager.camera = camera;
    }

    public static void loadLights(){
        Shader.uniformVector3f(camera.getPosition(), "cameraPosition");
        Shader.uniformInt(pointLights.size(), "enabledPointLights");
        Shader.uniformInt(spotLights.size(), "enabledSpotLights");
        Shader.uniformInt(directLights.size(), "enabledDirectionalLights");
        Shader.uniformArrayOfStructs(new ArrayList<>(pointLights), "pointLights");
        Shader.uniformArrayOfStructs(new ArrayList<>(directLights), "directionalLights");
        Shader.uniformArrayOfStructs(new ArrayList<>(spotLights), "spotLights");
    }

    public static void addSpotLight(SpotLight spotLight){
        spotLights.add(spotLight);
    }
    public static void addPointLight(PointLight pointLight){
        pointLights.add(pointLight);
    }
    public static void addDirectLight(DirectLight directLight){
        directLights.add(directLight);
    }



}
