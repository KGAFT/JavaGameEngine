package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import org.lwjgl.opengl.GL33;

import java.awt.*;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class ShaderBuffer implements IFrameBuffer{

    private int id;
    private int depthTexture;

    private int width = 4096;
    private int height = 4096;

    private void create(){
        id = glGenFramebuffers();
        glBindFramebuffer(GL33.GL_FRAMEBUFFER, id);
        FrameBufferTexture.createTexture(id, width, height, GL_DEPTH_ATTACHMENT,  new float[]{1.0f, 1.0f, 1.0f, 1.0f}, true);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void render(List<RenderTarget> renderTargets) {

    }

    @Override
    public void loadDataToShader() {

    }

    @Override
    public int getShaderType() {
        return Shader.SHADOW_MAPPING_SHADER;
    }
}
