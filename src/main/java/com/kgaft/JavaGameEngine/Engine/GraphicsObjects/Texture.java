package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import com.kgaft.JavaGameEngine.Shader.Shader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;


public class Texture {
    private static int slotCount = 0;
    private static final int BYTES_PER_PIXEL = 4;
    public static Texture loadTexture(String filePath){
        STBImage.stbi_set_flip_vertically_on_load(false);
        int[] width = new int[1];
        int[] height = new int[1];
        int[] colorAmount = new int[1];
        ByteBuffer textureContent = STBImage.stbi_load(filePath, width, height, colorAmount, STBImage.STBI_rgb);
        System.out.println(STBImage.stbi_failure_reason());
        int textureId = GL33.glGenTextures();
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + 0);
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
        slotCount++;
        return new Texture(slotCount-1, textureId);
    }


    private int slot;
    private int textureId;

    private String samplerName;

    public Texture(int slot, int textureId) {
        this.slot = slot;
        this.textureId = textureId;
    }
    public void attach(String samplerName) {
        glActiveTexture(GL_TEXTURE0+slot);
        int samplerLocation = glGetUniformLocation(Shader.getShaderId(), samplerName);
        glUniform1i(samplerLocation, slot);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
    public void attach() {
       // glActiveTexture(GL_TEXTURE0+0);
        int samplerLocation = glGetUniformLocation(Shader.getShaderId(), samplerName);
        glUniform1i(samplerLocation, 0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
    public void destroy(){
        glDeleteTextures(textureId);
    }

    public String getSamplerName() {
        return samplerName;
    }

    public void setSamplerName(String samplerName) {
        this.samplerName = samplerName;
    }
}
