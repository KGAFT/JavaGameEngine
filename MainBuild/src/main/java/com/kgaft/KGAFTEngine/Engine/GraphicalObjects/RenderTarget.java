package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;

import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import org.joml.Matrix4f;

import java.util.List;

public abstract class RenderTarget {

    protected Matrix4f worldMatrix = new Matrix4f().identity();


    public abstract VertexArrayObject getVertexArrayObject();

    public abstract List<Texture> getTexturesToAttach();


    public abstract void update();

    public abstract void destroy();

    public Matrix4f getWorldMatrix() {

        return worldMatrix;
    }

    public void setWorldMatrix(Matrix4f matrix) {
        this.worldMatrix = matrix;
    }


}
