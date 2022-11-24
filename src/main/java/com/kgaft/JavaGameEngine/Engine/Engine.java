package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.Camera.CameraManager;
import com.kgaft.JavaGameEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.*;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.DirectLight;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.LightManager;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.PointLight;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light.SpotLight;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        String modelPath = Engine.class.getClassLoader().getResource("Models/PokeBall/Pokeball.obj").getPath();
        Model model = new ModelLoader().loadModel(modelPath);
        try {
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_BaseColor.png").getPath(), Texture.BASE_COLOR_TEXTURE));
            model.addTexture(Texture.loadTexture(Engine.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_Normal.png").getPath(), Texture.NORMAL_MAP_TEXTURE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Window.getWindow().addKeyBoardCallBack(playerNonPhysicsMode);
        Window.getWindow().addMouseMoveCallBack(playerNonPhysicsMode);
        DirectLight directLight = new DirectLight(new Vector4f(1f, 1f, 1f, 1f));
        PointLight pointLight = new PointLight(new Vector4f(0.5f, 0.0f, 0.5f, 1.0f));
        SpotLight spotLight = new SpotLight(new Vector4f(0.5f, 0.0f, 0.5f, 1f));
        //LightManager.addSpotLight(spotLight);
        LightManager.addDirectLight(directLight);
        LightManager.setCamera(camera);
        while (Window.getWindow().isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0.0f, 0.0f, 0, 1);
            Shader.attach();
            LightManager.loadLights();
            //spotLight.rotate(new Vector3f(0f, 0f, 1f));
            model.draw();
            Window.getWindow().preRenderEvents();
            cameraManager.handleCamera();
            Window.getWindow().postEvents();
        }
    }

}
