package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;

import java.util.ArrayList;

public class PbrLightManager {

    private static Camera camera;
    private static ArrayList<ShaderStruct> pbrLights = new ArrayList<>();

    public static void addLight(PbrLight light){
        pbrLights.add(light);
    }

    public static void loadLight(){
        if(Shader.getCurrentWorkMode()!=Shader.PBR_MODE){
            Shader.initForPbrLight();
        }
        Shader.uniformInt(pbrLights.size(), "enabledPbrLights");
        Shader.uniformVector3f(camera.getPosition(), "cameraPosition");
        Shader.uniformArrayOfStructs(pbrLights, "lights");
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        PbrLightManager.camera = camera;
    }
}
