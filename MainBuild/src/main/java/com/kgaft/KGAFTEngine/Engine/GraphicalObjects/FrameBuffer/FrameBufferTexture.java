package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL33.*;

import java.nio.ByteBuffer;

public class FrameBufferTexture {
    public static int createTexture(int frameBufferId, int width, int height, int attachment, float[] clampColor){
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        if(clampColor!=null){
            GL33.glTexParameterfv(GL33.GL_TEXTURE_2D, GL_TEXTURE_2D, clampColor);
        }
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, textureID, 0);
        return textureID;
    }
}
