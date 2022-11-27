package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;

import java.util.ArrayList;

public class PbrLightManager {

    private static Camera camera;
    private static ArrayList<ShaderStruct> pointLights = new ArrayList<>();
    private static ArrayList<ShaderStruct> directLights = new ArrayList<>();

    private static ArrayList<ShaderStruct> spotLights = new ArrayList<>();

    public static void addPointLight(PointPbrLight light){
        pointLights.add(light);
    }

    public static void addDirectLight(DirectPbrLight directPbrLight){
        directLights.add(directPbrLight);
    }

    public static void addSpotLight(SpotPbrLight spotLight){
        spotLights.add(spotLight);
    }

    public static void loadLight(){
        if(Shader.getCurrentWorkMode()!=Shader.PBR_MODE){
            Shader.initForPbrLight();
        }
        Shader.uniformInt(pointLights.size(), "enabledPointLights");
        Shader.uniformInt(directLights.size(), "enabledDirectionalLights");
        Shader.uniformInt(spotLights.size(), "enabledSpotLights");
        Shader.uniformVector3f(camera.getPosition(), "cameraPosition");
        Shader.uniformArrayOfStructs(pointLights, "pointLights");
        Shader.uniformArrayOfStructs(directLights, "directLights");
        Shader.uniformArrayOfStructs(spotLights, "spotLights");
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        PbrLightManager.camera = camera;
    }
}
