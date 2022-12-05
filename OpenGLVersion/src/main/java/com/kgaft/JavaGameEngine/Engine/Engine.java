package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.FrameBufferObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;

public class Engine {

   private Scene currentScene;

    public Engine(){

        GL.createCapabilities();
        GL33.glViewport(0, 0, Window.getWindow().getWidth(), Window.getWindow().getHeight());
    }
    public void start(){

        Window.getWindow().addResizeCallBack((newWidth, newHeight) -> {
            GL33.glViewport(0, 0, newWidth, newHeight);
        });
        Shader.initializeShader("ShadersDefault",Shader.DEFAULT_SHADER);
        Shader.initializeShader("ShadeShader", Shader.SHADES_SHADER);
        Shader.switchToDefaultShader();
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        currentScene.setup();
        FrameBufferObject fbo = FrameBufferObject.createFBO();
        while (Window.getWindow().isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0.0f, 0.0f, 0, 1);
            Shader.attach();
            Window.getWindow().preRenderEvents();
            currentScene.update();
            Window.getWindow().postEvents();
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
    }
}
