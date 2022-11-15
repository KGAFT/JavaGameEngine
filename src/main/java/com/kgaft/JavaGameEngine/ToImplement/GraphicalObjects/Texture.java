package com.kgaft.OpenlGLGameEngine.Engine.GraphicalObjects;

import com.kgaft.OpenlGLGameEngine.Shader.Shader;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

public class Texture {
    public static Texture loadTextureFromFile(String texturePath, int slot, String samplerName) {
        STBImage.stbi_set_flip_vertically_on_load(false);
        int[] width = new int[1];
        int[] height = new int[1];
        int[] colorAmount = new int[1];
        ByteBuffer textureContent = STBImage.stbi_load(texturePath, width, height, colorAmount, STBImage.STBI_rgb);
        int textureId = GL33.glGenTextures();
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + slot);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);

        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
        if (colorAmount[0] >= 3) {
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, width[0], height[0], 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, textureContent);
        } else if (colorAmount[0] == 1) {
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, width[0], height[0], 0, GL33.GL_RED, GL33.GL_UNSIGNED_BYTE, textureContent);
        }
        STBImage.stbi_image_free(textureContent);
        GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
        return new Texture(textureId, samplerName);
    }

    private String samplerName;
    private int textureId;

    private Texture(int textureId, String samplerName) {
        this.textureId = textureId;
        this.samplerName = samplerName;
    }

    public void attach() {
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
    }

    public void destroy() {
        GL33.glDeleteTextures(textureId);
    }

    public void setTextureUnit(int textureUnit) {
        GL33.glUniform1i(GL33.glGetUniformLocation(Shader.getShaderId(), samplerName), textureUnit);
    }
}
