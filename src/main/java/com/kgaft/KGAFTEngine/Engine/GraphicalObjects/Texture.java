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

    private static List<Integer> freeSlots = new ArrayList<>();
    public static int textureCount = 0;
    public static final String NORMAL_MAP_TEXTURE = "normalMap";
    public static final String ALBEDO_TEXTURE = "albedoMap";
    public static final String METALLIC_TEXTURE = "metallicMap";
    public static final String ROUGHNESS_TEXTURE = "roughnessMap";
    public static final String AMBIENT_OCCLUSION_MAP = "aoMap";
    public static final String EMISSIVE_MAP = "emissiveMap";
    private static Texture defaultTexture = null;


    public static Texture loadTexture(String filePath, String textureType) throws IOException {
        int textureSlot = 0;
        boolean newSlot = false;
        if(freeSlots.size()>0){
            textureSlot = freeSlots.get(0)+GL_TEXTURE0;
        }
        else{
            textureSlot = GL_TEXTURE0 + textureCount;
            newSlot = true;
        }

        PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buf.flip();

        int textureId = glGenTextures();
        glActiveTexture(textureSlot);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glGenerateMipmap(GL_TEXTURE_2D);
        if(!newSlot){
            freeSlots.remove(0);
        }
        textureCount+=newSlot?1:0;
        return new Texture(textureId, textureSlot-GL_TEXTURE0, textureType);
    }
    public static void cleanUpSamplers(){
        if(defaultTexture==null){
            try {
                defaultTexture = loadTexture(Texture.class.getClassLoader().getResource("textures/baseBlackColor.png").getPath(), ALBEDO_TEXTURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            glActiveTexture(defaultTexture.slot);
            Shader.uniformInt(defaultTexture.slot, ALBEDO_TEXTURE);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
            Shader.uniformInt(defaultTexture.slot, NORMAL_MAP_TEXTURE);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
            Shader.uniformInt(defaultTexture.slot, METALLIC_TEXTURE);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
            Shader.uniformInt(defaultTexture.slot, ROUGHNESS_TEXTURE);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
            Shader.uniformInt(defaultTexture.slot, AMBIENT_OCCLUSION_MAP);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
            Shader.uniformInt(defaultTexture.slot, EMISSIVE_MAP);
            glBindTexture(GL_TEXTURE_2D, defaultTexture.textureId);
        }
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
        glActiveTexture(slot);
        glBindTexture(GL_TEXTURE_2D, textureId);
        Shader.uniformInt(slot, samplerName);

    }

    public void deAttach(){
        glActiveTexture(slot);
        glBindTexture(GL_TEXTURE_2D, 0);
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
