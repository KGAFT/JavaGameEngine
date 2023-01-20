package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;


import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;


public class Texture {

    public static List<Integer> freeSlots = new ArrayList<>();
    public static final String NORMAL_MAP_TEXTURE = "normalMap";
    public static final String ALBEDO_TEXTURE = "albedoMap";
    public static final String METALLIC_TEXTURE = "metallicMap";
    public static final String ROUGHNESS_TEXTURE = "roughnessMap";
    public static final String AMBIENT_OCCLUSION_MAP = "aoMap";
    public static final String EMISSIVE_MAP = "emissiveMap";
    private static Texture defaultTexture = null;


    public static Texture loadTexture(String filePath, String textureType) throws IOException {
        PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        int textureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0+acquireTextureSlot(textureType));
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D);
      
        return new Texture(textureId, acquireTextureSlot(textureType), textureType);
    }
    private static int acquireTextureSlot(String samplerName){
        switch(samplerName){
            case NORMAL_MAP_TEXTURE:
                return 0;
            case ALBEDO_TEXTURE:
                return 1;
            case METALLIC_TEXTURE:
                return 2;
            case ROUGHNESS_TEXTURE:
                return 3;
            case AMBIENT_OCCLUSION_MAP:
                return 4;
            case EMISSIVE_MAP:
                return 5;
        }
        return -1;
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
        glActiveTexture(GL_TEXTURE0+slot);
        glBindTexture(GL_TEXTURE_2D, textureId);
        Shader.uniformInt(slot, samplerName);

    }

    public void destroy() {
        glDeleteTextures(textureId);
        freeSlots.add(slot);
    }

    public String getSamplerName() {
        return samplerName;
    }


}
