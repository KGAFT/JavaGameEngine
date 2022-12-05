package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import com.kgaft.JavaGameEngine.Shader.Shader;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class DepthTexture {
    private static final float[] border = {1.0f,0.0f,0.0f,0.0f};
    public static DepthTexture createFBOTexture(){
        int id = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D,id);
        GL33.glTexImage2D(GL33.GL_TEXTURE_2D,0,GL33.GL_DEPTH_COMPONENT,
                2048,2048,0,
                GL33.GL_DEPTH_COMPONENT,GL33.GL_UNSIGNED_BYTE,0);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_MAG_FILTER,
                GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_MIN_FILTER,
                GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_WRAP_S,
                GL33.GL_CLAMP_TO_BORDER);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_WRAP_T,
                GL33.GL_CLAMP_TO_BORDER);
        GL33.glTexParameterfv(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_BORDER_COLOR,
                border);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_COMPARE_MODE,
                GL33.GL_COMPARE_REF_TO_TEXTURE);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D,GL33.GL_TEXTURE_COMPARE_FUNC,
                GL33.GL_LESS);
        GL33.glActiveTexture(GL33.GL_TEXTURE0+Texture.textureCount);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D,id);
        Texture.textureCount++;
        return new DepthTexture(id, Texture.textureCount-1);
    }

    private int id;
    private int slot;

    private final String samplerName = "depthTexture";

    public DepthTexture(int id, int slot) {
        this.id = id;
        this.slot = slot;
    }


    public void attach() {
        Shader.uniformInt(slot, samplerName);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public String getSamplerName() {
        return samplerName;
    }
}
