package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import java.util.List;

import org.lwjgl.opengl.GL33;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Window.Window;



public class GBuffer implements IFrameBuffer{
    private int id;
    private int positionsTexture;
    private int normalTexture;
    private int albedoTexture;
    private int metallicTexture;
    private int roughnessTexture;
    private int aoTexture;
    private int emissiveTexture;
    private int rboDepth;
    

    public GBuffer(Window window) {
        id = GL33.glGenFramebuffers();
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, id);
        positionsTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT0);
        normalTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT1);
        albedoTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT2);
        metallicTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT3);
        roughnessTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT4);
        aoTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT5);
        emissiveTexture = FrameBufferTexture.createTexture(id, window.getWidth(), window.getHeight(), GL33.GL_COLOR_ATTACHMENT6);
        GL33.glDrawBuffers(new int[]{GL33.GL_COLOR_ATTACHMENT0, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_COLOR_ATTACHMENT2,  GL33.GL_COLOR_ATTACHMENT3, GL33.GL_COLOR_ATTACHMENT4, GL33.GL_COLOR_ATTACHMENT5, GL33.GL_COLOR_ATTACHMENT6});
        rboDepth=GL33.glGenRenderbuffers();
        GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, rboDepth);
        GL33.glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL33.GL_DEPTH_COMPONENT, window.getWidth(), window.getHeight());
        GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, rboDepth);
    
        if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Failed to create frame buffer");
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int getId() {
       
        return id;
    }

    @Override
    public void render(List<RenderTarget> renderTargets) {
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
        renderTargets.forEach(renderTarget->{
            float[] worldMat = new float[4*4];
            renderTarget.getWorldMatrix().get(worldMat);
            Shader.uniformMatrix4f(worldMat, "modelMatrix");
            renderTarget.getTexturesToAttach().forEach(texture->{
                texture.attach();
            });
            renderTarget.getVertexArrayObject().draw();
        });
    }

    @Override
    public void loadDataToShader() {
        Shader.uniformInt(0, "gPosition");
        Shader.uniformInt(1, "gNormal");
        Shader.uniformInt(2, "gAlbedo");
    
    }

    @Override
    public int getShaderType() {
        // TODO Auto-generated method stub
        return 0;
    }
}
