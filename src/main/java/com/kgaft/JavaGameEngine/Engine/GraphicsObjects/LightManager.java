package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import com.kgaft.JavaGameEngine.Shader.Shader;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private static List<Light> lights = new ArrayList<>();
    private static Vector3f cameraPosition;

    public static void setCameraPosition(Vector3f cameraPosition) {
        LightManager.cameraPosition = cameraPosition;
    }
    public static void addLight(Light light){
        lights.add(light);
    }
    public static void removeLight(Light light){
        lights.remove(light);
    }
    public static void loadLights(){
        Shader.uniformInt(lights.size(), "activatedLightBlocks");
        lights.forEach(light->{

        });
    }
}
