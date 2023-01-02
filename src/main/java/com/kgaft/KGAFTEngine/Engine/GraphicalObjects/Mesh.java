package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;

import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Mesh extends IRenderTarget{
    private VertexArrayObject vao;
    private String name;
    private List<Texture> textures = new ArrayList<>();
    private List<IRenderTarget> children = new ArrayList<>();
    private float emissiveIntensity = 2;
    private float emissiveShininess = 1;
    private float gammaCorrect = 1.0f/2.2f;
    private float ambientIntensity = 0.03f;


    public Mesh(VertexArrayObject vao){
        this.vao = vao;
    }

    public void addChild(Mesh mesh){
        children.add(mesh);
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
    public List<IRenderTarget> getChildren() {
        return children;
    }

    @Override
    public void update() {
        Shader.uniformFloat(emissiveIntensity, "emissiveIntensity");
        Shader.uniformFloat(emissiveShininess, "emissiveShininess");
        Shader.uniformFloat(gammaCorrect, "gammaCorrect");
        Shader.uniformFloat(ambientIntensity, "ambientIntensity");
    }
    public void addTexture(Texture texture, boolean includeChildren){
        textures.add(texture);
        if(includeChildren){
            children.forEach(child->((Mesh)child).addTexture(texture, true));
        }
    }

    public void setRotation(Vector3f rotation, boolean includeChildren){
        this.rotation = rotation;
        if(includeChildren){
            children.forEach(child->((Mesh)child).setRotation(rotation, true));
        }
    }
    public void setScale(Vector3f scale, boolean includeChildren){
        this.scale = scale;
        if(includeChildren){
            children.forEach(child->((Mesh)child).setScale(scale, true));
        }
    }
    public void setPosition(Vector3f position, boolean includeChildren){
        this.position = position;
        if(includeChildren){
            children.forEach(child->((Mesh)child).setPosition(position, true));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Vector3f getPosition(){
        return this.position;
    }
    public Vector3f getScale(){
        return this.scale;
    }
    public Vector3f getRotation(){
        return this.rotation;
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
    public void destroy(){
        vao.destroy();
        textures.forEach(Texture::destroy);
        children.forEach(mesh->((Mesh)mesh).destroy());
    }
}
