package com.kgaft.OpenlGLGameEngine.Engine.GraphicalObjects;

import com.kgaft.OpenlGLGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.OpenlGLGameEngine.Shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private VertexArrayObject vertexArrayObject;
    private List<Texture> meshTextures = new ArrayList<>();
    private Matrix4f worldMatrix = new Matrix4f().identity();

    private Vector3f worldPos = new Vector3f();

    public void updateAndLoadToGameWorld() {
        Shader.attach();
        float[] worldPositionData = new float[4 * 4];
        worldMatrix.get(worldPositionData);
        Shader.uniformMatrix4f(worldPositionData, "worldPosition");
        for (int i = 0; i < meshTextures.size(); i++) {
            meshTextures.get(i).setTextureUnit(i);
            meshTextures.get(i).attach();
        }
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
}
