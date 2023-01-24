package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;

import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;


import java.util.ArrayList;
import java.util.List;

public class Mesh extends RenderTarget {
    private VertexArrayObject vao;
    private String name;
    private List<Texture> textures = new ArrayList<>();

    private float emissiveIntensity = 2;
    private float emissiveShininess = 1;
    private float gammaCorrect = 1.0f / 2.2f;
    private float ambientIntensity = 0.03f;


    public Mesh(VertexArrayObject vao) {
        this.vao = vao;
    }


    @Override
    public VertexArrayObject getVertexArrayObject() {
        return vao;
    }

    @Override
    public List<Texture> getTexturesToAttach() {
        return textures;
    }


    @Override
    public void update() {
        Shader.uniformFloat(emissiveIntensity, "emissiveIntensity");
        Shader.uniformFloat(emissiveShininess, "emissiveShininess");
        Shader.uniformFloat(gammaCorrect, "gammaCorrect");
        Shader.uniformFloat(ambientIntensity, "ambientIntensity");
    }

    public void addTexture(Texture texture) {
        textures.add(texture);

    }

    public void setWorldMatrix(Matrix4f matrix) {
        this.worldMatrix = matrix;

    }

    public Matrix4f getWorldMatrix() {
        return worldMatrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Vector3f position){
        worldMatrix.translate(position);
    }
    public void setRotation(Vector3f rotation){
        worldMatrix.rotate(rotation.x, 1, 0, 0);
        worldMatrix.rotate(rotation.y, 0, 1,0 );
        worldMatrix.rotate(rotation.z, 0, 0, 1);
    }
    public void scale(Vector3f scale){
        worldMatrix.scale(scale);
    }


    public float getEmissiveIntensity() {
        return emissiveIntensity;
    }

    public void setEmissiveIntensity(float emissiveIntensity) {
        this.emissiveIntensity = emissiveIntensity;
    }

    public float getEmissiveShininess() {
        return emissiveShininess;
    }

    public void setEmissiveShininess(float emissiveShininess) {
        this.emissiveShininess = emissiveShininess;
    }

    public float getGammaCorrect() {
        return gammaCorrect;
    }

    public void setGammaCorrect(float gammaCorrect) {
        this.gammaCorrect = gammaCorrect;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }

    @Override
    public void destroy() {
        vao.destroy();
        textures.forEach(Texture::destroy);

    }

}
