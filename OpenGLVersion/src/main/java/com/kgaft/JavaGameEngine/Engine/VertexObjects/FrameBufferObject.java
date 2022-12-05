package com.kgaft.JavaGameEngine.Engine.VertexObjects;

import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.DepthTexture;
import org.lwjgl.opengl.GL33;

public class FrameBufferObject {

    public static FrameBufferObject createFBO(){
        int fboId = GL33.glGenFramebuffers();
        return new FrameBufferObject(fboId);
    }

    private int fboId;

    public FrameBufferObject(int fboId) {
        this.fboId = fboId;
    }

    /**
     * Attach shader before writting
     */
    public void writeToTexture(DepthTexture texture){
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, fboId);
        GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER,GL33.GL_DEPTH_ATTACHMENT,
                GL33.GL_TEXTURE_2D,texture.getId(),0);
        GL33.glDrawBuffers(fboId);
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }
    public void delete(){
        GL33.glDeleteFramebuffers(fboId);
    }

}
