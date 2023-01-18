package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Lighting;



import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Engine.Shader.ShaderStruct;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private List<ShaderStruct> pointLights = new ArrayList<>();
    private List<ShaderStruct> directLights = new ArrayList<>();


    public void update() {
        Shader.uniformInt(pointLights.size(), "enabledPointLights");
        Shader.uniformInt(directLights.size(), "enabledDirectionalLights");

        Shader.uniformArrayOfStructs(pointLights, "pointLights");
        Shader.uniformArrayOfStructs(directLights, "directLights");
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
