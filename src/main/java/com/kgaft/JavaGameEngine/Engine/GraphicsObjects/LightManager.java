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
        for (int i = 0; i < lights.size(); i++) {
            loadLightToShader(lights.get(i), i);
        }
    }
    private static void loadLightToShader(Light light, int index){
        Shader.uniformVector3fInArrayOfStructs(index, light.getPositionLight(), "lights", "position");
        Shader.uniformVector3fInArrayOfStructs(index, light.getLightColor(), "lights", "color");
        Shader.uniformFloatValueInArrayOfStructs(index, light.getShininess(), "lights", "specularShininess");
        Shader.uniformFloatValueInArrayOfStructs(index, light.getAmbientStrength(), "lights", "ambientStrength");
        Shader.uniformFloatValueInArrayOfStructs(index, light.getSpecularStrength(), "lights", "specularStrength");
        Shader.uniformMatrix4fInArrayOfStructs(index, light.getLightMatrix(), "lights", "lightMatrix");
    }

}
