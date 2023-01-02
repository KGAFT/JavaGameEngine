package com.kgaft.KGAFTEngine.Engine;

import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.IRenderTarget;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Texture;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Window.Window;
import com.kgaft.KGAFTEngine.Window.WindowResizeCallBack;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

public class Engine implements WindowResizeCallBack {
    private Window outputWindow;
    private Scene currentScene;
    public Engine(Window window){
        this.outputWindow = window;
        GL.createCapabilities();
        GL33.glViewport(0, 0, Window.getWindow().getWidth(), Window.getWindow().getHeight());
        window.addResizeCallBack(this);
        Shader.initializeShader("ShadersDefault", Shader.DEFAULT_SHADER);
        Shader.switchToDefaultShader();
    }
    private void drawRenderTarget(IRenderTarget renderTarget){
        renderTarget.update();
        renderTarget.getTexturesToAttach().forEach(Texture::attach);
        float[] worldPositionData = new float[4 * 4];
        renderTarget.getWorldMatrix().get(worldPositionData);
        Shader.uniformMatrix4f(worldPositionData, "modelMatrix");
        renderTarget.getVertexArrayObject().draw();
        renderTarget.getChildren().forEach(this::drawRenderTarget);
    }
    public void start() throws InterruptedException {
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        currentScene.setWindow(Window.getWindow());
        currentScene.setup();
        while (Window.getWindow().isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0.0f, 0.5f, 0, 1);
            Shader.attach();
            Window.getWindow().preRenderEvents();
            currentScene.getCameraManager().update();
            currentScene.getLightManager().update();
            currentScene.getTargetsToDraw().forEach(this::drawRenderTarget);
            currentScene.update();
            Window.getWindow().postEvents();
        }
        currentScene.cleanUp();
        Thread.sleep(5000);
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