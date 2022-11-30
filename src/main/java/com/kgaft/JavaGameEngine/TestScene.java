package com.kgaft.JavaGameEngine;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.Engine;
import com.kgaft.JavaGameEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.JavaGameEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.DirectPbrLight;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Model;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.ModelLoader;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Texture;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Vector3f;

import java.io.IOException;

public class TestScene extends Scene {
    @Override
    public void setup() {
        String modelPath = Engine.class.getClassLoader().getResource("Models/pokedex/pokedex.gltf").getPath().substring(1);
        Model model = new ModelLoader().loadModel(modelPath);
        try {
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_BaseColor_tga.png").getPath(), Texture.ALBEDO_TEXTURE));
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/pokedex/Pokedex_T_ao.png").getPath(), Texture.AMBIENT_OCCLUSION_MAP));
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Metallic.png").getPath(), Texture.METALLIC_TEXTURE));
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Roughness.png").getPath(), Texture.ROUGHNESS_TEXTURE));
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Normal_tga.png").getPath(), Texture.NORMAL_MAP_TEXTURE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        addSceneObject(model);
        Camera camera = new Camera();
        camera.setFov(120);
        camera.setNearPlane(0.01f);
        camera.setFarPlane(1500.0f);
        PlayerNonPhysicsMode playerNonPhysicsMode = new PlayerNonPhysicsMode();
        playerNonPhysicsMode.addDependentObject(camera);

        Window.getWindow().addKeyBoardCallBack(playerNonPhysicsMode);
        Window.getWindow().addMouseMoveCallBack(playerNonPhysicsMode);
        getLightManager().addDirectLight(new DirectPbrLight(new Vector3f(0.5f, 0.0f, 0.5f), new Vector3f(0.1f, 0.5f, 0.4f)));
        getCameraManager().setCurrentCamera(camera);
        super.setup();
    }


}
