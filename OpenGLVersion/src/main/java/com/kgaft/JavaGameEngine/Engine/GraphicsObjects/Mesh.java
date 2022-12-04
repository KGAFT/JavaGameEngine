package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;


import com.kgaft.JavaGameEngine.Engine.GameObjects.Scene.SceneObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexBufferObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Mesh extends SceneObject {


    public static Mesh createMesh(float[] vertices, float[] UVs, float[] normals, int[] indices){
        VertexArrayObject vao = VertexArrayObject.createVao();
        vao.attachEbo(ElementBufferObject.createEbo(indices));
        vao.attachVbo(0, VertexBufferObject.createVbo(vertices, 3));
        vao.attachVbo(1, VertexBufferObject.createVbo(UVs, 2));
        vao.attachVbo(2, VertexBufferObject.createVbo(normals, 3));
        Mesh mesh = new Mesh();
        mesh.setVertexArrayObject(vao);
        return mesh;
    }
    public static Mesh createMesh(float[] vertices, float[] UVs, float[] normals, int[] indices, List<Texture> textures){
        VertexArrayObject vao = VertexArrayObject.createVao();
        vao.attachEbo(ElementBufferObject.createEbo(indices));
        vao.attachVbo(0, VertexBufferObject.createVbo(vertices, 3));
        vao.attachVbo(1, VertexBufferObject.createVbo(UVs, 2));
        vao.attachVbo(2, VertexBufferObject.createVbo(normals, 3));
        Mesh mesh = new Mesh();
        mesh.setVertexArrayObject(vao);
        mesh.setMeshTextures(textures);
        return mesh;
    }


    private VertexArrayObject vertexArrayObject;
    private List<Texture> meshTextures = new ArrayList<>();
    private Matrix4f worldMatrix = new Matrix4f().identity();
    private Vector3f worldPos = new Vector3f();

    private float emissiveIntensity = 2;
    private float emissiveShininess = 1;

    private float gammaCorrect = 1.0f/2.2f;
    private float ambientIntensity = 0.03f;
    public void updateAndLoadToGameWorld() {
        Shader.attach();
        float[] worldPositionData = new float[4 * 4];
        worldMatrix.get(worldPositionData);
        Shader.uniformMatrix4f(worldPositionData, "modelMatrix");
        Shader.uniformFloat(emissiveIntensity, "emissiveIntensity");
        Shader.uniformFloat(emissiveShininess, "emissiveShininess");
        Shader.uniformFloat(gammaCorrect, "gammaCorrect");
        Shader.uniformFloat(ambientIntensity, "ambientIntensity");
        meshTextures.forEach(Texture::attach);
        vertexArrayObject.draw();
    }

    public void setPosition(Vector3f position) {
        worldMatrix.translate(position);
        worldPos = position;
    }

    public void rotate(Vector3f rotation) {
        worldMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
    }

    public void scale(Vector3f scale) {
        worldMatrix.scale(scale);
    }

    public void destroy() {
        meshTextures.forEach(Texture::destroy);
        vertexArrayObject.destroy();
    }

    public void setVertexArrayObject(VertexArrayObject vertexArrayObject) {
        this.vertexArrayObject = vertexArrayObject;
    }

    public void setMeshTextures(List<Texture> meshTextures) {
        this.meshTextures = meshTextures;
    }

    public List<Texture> getMeshTextures() {
        return meshTextures;
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

    @Override
    public void draw() {
        updateAndLoadToGameWorld();
    }
}
