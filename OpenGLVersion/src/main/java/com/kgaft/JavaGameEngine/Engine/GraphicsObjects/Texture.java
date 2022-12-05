package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import com.kgaft.JavaGameEngine.Shader.Shader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import de.matthiasmann.twl.utils.PNGDecoder;
import static org.lwjgl.opengl.GL33.*;


public class Texture {
    protected static int textureCount = 0;
    public static final String BASE_COLOR_TEXTURE = "baseColorTexture";

    public static final String NORMAL_MAP_TEXTURE = "normalMap";

    public static final String ALBEDO_TEXTURE = "albedoMap";

    public static final String METALLIC_TEXTURE = "metallicMap";

    public static final String ROUGHNESS_TEXTURE = "roughnessMap";

    public static final String AMBIENT_OCCLUSION_MAP = "aoMap";

    public static final String EMISSIVE_MAP = "emissiveMap";
    public static Texture loadTexture(String filePath, String textureType) throws IOException {
        PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        int textureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0+textureCount);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D);
        textureCount++;
        return new Texture(textureId, textureCount-1, textureType);
    }


    private int textureId;
    private int slot;
    private String samplerName;

    public Texture(int textureId, int slot, String samplerName) {
        this.textureId = textureId;
        this.slot = slot;
        this.samplerName = samplerName;
    }

    public void attach() {
        Shader.uniformInt(slot, samplerName);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }
    public void destroy(){
        glDeleteTextures(textureId);
    }

    public String getSamplerName() {
        return samplerName;
    }


}
