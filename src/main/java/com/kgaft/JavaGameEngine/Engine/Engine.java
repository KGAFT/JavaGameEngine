package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.Camera.CameraManager;
import com.kgaft.JavaGameEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.*;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.*;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import java.io.IOException;

public class Engine {



    public Engine(){

        GL.createCapabilities();
        GL33.glViewport(0, 0, Window.getWindow().getWidth(), Window.getWindow().getHeight());
    }
    public void start(){
        Camera camera = new Camera();
        Window.getWindow().addResizeCallBack((newWidth, newHeight) -> {
            GL33.glViewport(0, 0, newWidth, newHeight);
        });
        Shader.initForPhongLight();
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        CameraManager cameraManager = new CameraManager();
        cameraManager.registerCameraAndSwitchToIt(camera);
        PlayerNonPhysicsMode playerNonPhysicsMode = new PlayerNonPhysicsMode();
        playerNonPhysicsMode.addDependentObject(camera);
        String modelPath = Engine.class.getClassLoader().getResource("Models/pokedex/pokedex.gltf").getPath().substring(0);
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
        Window.getWindow().addKeyBoardCallBack(playerNonPhysicsMode);
        Window.getWindow().addMouseMoveCallBack(playerNonPhysicsMode);

        PbrLightManager.setCamera(camera);
        while (Window.getWindow().isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0.0f, 0.0f, 0, 1);
            Shader.attach();
            PbrLightManager.loadLight();
            //spotLight.rotate(new Vector3f(0f, 0f, 1f));
            model.draw();
            Window.getWindow().preRenderEvents();
            cameraManager.handleCamera();
            Window.getWindow().postEvents();
        }
    }

}
