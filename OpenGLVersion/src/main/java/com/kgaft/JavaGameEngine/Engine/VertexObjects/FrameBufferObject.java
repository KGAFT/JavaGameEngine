package com.kgaft.JavaGameEngine.Engine.VertexObjects;

import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.DepthTexture;
import org.lwjgl.opengl.GL33;

public class FrameBufferObject {
    private int fboId;

    public void writeToTexture(DepthTexture texture){
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, fboId);
        GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER,GL33.GL_DEPTH_ATTACHMENT,
                GL33.GL_TEXTURE_2D,texture.getId(),0);
        GL33.glDrawBuffers(fboId);
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }

}
