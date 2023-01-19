package com.kgaft.KGAFTEngine.Engine;

import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Texture;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer.GBuffer;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Window.Window;
import com.kgaft.KGAFTEngine.Window.WindowResizeCallBack;

import java.util.spi.CurrencyNameProvider;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

public class Engine implements WindowResizeCallBack {
    private Window outputWindow;
    private Scene currentScene;
    private GBuffer gBuffer;
    public Engine(Window window){
        this.outputWindow = window;
        GL.createCapabilities();
        GL33.glViewport(0, 0, Window.getWindow().getWidth(), Window.getWindow().getHeight());
        window.addResizeCallBack(this);
        Shader.initializeShader("OpenGLShaders/ShadersDefault", Shader.DEFAULT_SHADER);
        Shader.initializeShader("OpenGLShaders/GBufferShaders", Shader.GBUFFER_SHADER);
        Shader.switchToDefaultShader();
        gBuffer = new GBuffer(window);
    }
    private void drawRenderTarget(RenderTarget renderTarget, boolean enableTextures){
        renderTarget.update();
        if(enableTextures){
            renderTarget.getTexturesToAttach().forEach(Texture::attach);
        }
        float[] worldPositionData = new float[4 * 4];
        renderTarget.getWorldMatrix().get(worldPositionData);
        Shader.uniformMatrix4f(worldPositionData, "modelMatrix");
        renderTarget.getVertexArrayObject().draw();

    }
    public void start() throws InterruptedException {
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glEnable(GL33.GL_MULTISAMPLE);
        currentScene.setWindow(Window.getWindow());
        currentScene.setup();
        while (Window.getWindow().isWindowActive()){
            Window.getWindow().preRenderEvents();
            Shader.switchToShadesShader();
            Shader.attach();
            currentScene.getCameraManager().update();
            gBuffer.render(currentScene.getTargetsToDraw());
            
            Shader.switchToDefaultShader();
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0.0f, 0.0f, 0, 0);
            Shader.attach();
            currentScene.getLightManager().update();
            currentScene.getPhysicsManager().update();
            gBuffer.loadDataToShader();
            Window.getWindow().postEvents();
        }
        currentScene.cleanUp();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
    }

    @Override
    public void resized(int newWidth, int newHeight) {
        GL33.glViewport(0, 0, newWidth, newHeight);
    }
}