package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class ShaderBuffer implements IFrameBuffer {
    private int id;
    private int depthTexture;
    private int width = 4096;
    private int height = 4096;
    private Matrix4f lightView;
    private Vector3f lightPos;


    public ShaderBuffer(){
        create();
    }

    private void create() {
        id = glGenFramebuffers();
        glBindFramebuffer(GL33.GL_FRAMEBUFFER, id);
        FrameBufferTexture.createTexture(id, width, height, GL_DEPTH_ATTACHMENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    private void calculateLightView(){
        Matrix4f lightProjection = new Matrix4f().identity();
        Matrix4f lightMatrix = new Matrix4f().identity();
        lightProjection.ortho(-10.0f, 10.0f, -10.0f, 10.0f, 1, 7.5f);
        lightMatrix.lookAt(lightPos, new Vector3f(0,0,0),new Vector3f(0,1,0));
        lightView = lightProjection.mul(lightMatrix);
    }
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void render(List<RenderTarget> renderTargets) {
        glViewport(0,0,width,height);
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        glClear(GL_DEPTH_BUFFER_BIT);
        calculateLightView();
        float[] lightViewMatrix = new float[4*4];
        lightView.get(lightViewMatrix);
        Shader.uniformMatrix4f(lightViewMatrix, "lightView");
        renderTargets.forEach(renderTarget -> {
            float[] modelMatrix = new float[4*4];
            renderTarget.getWorldMatrix().get(modelMatrix);
            Shader.uniformMatrix4f(modelMatrix, "modelMatrix");
            renderTarget.getVertexArrayObject().draw();
        });
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void loadDataToShader() {
        float[] lightViewMatrix = new float[4*4];
        lightView.get(lightViewMatrix);
        Shader.uniformMatrix4f(lightViewMatrix, "lightView");
        Shader.uniformVector3f(lightViewMatrix, "lightShadePos");
        Shader.uniformInt(14, "shadowMap");
        glActiveTexture(GL_TEXTURE14);
        glBindTexture(GL_TEXTURE_2D, depthTexture);
    }

    @Override
    public int getShaderType() {
        return Shader.SHADOW_MAPPING_SHADER;
    }
    public Vector3f getLightPos() {
        return lightPos;
    }
    public void setLightPos(Vector3f lightPos) {
        this.lightPos = lightPos;
    }
    
}
