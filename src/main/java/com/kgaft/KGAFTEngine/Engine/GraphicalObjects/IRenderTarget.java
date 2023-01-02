package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;

import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class IRenderTarget {

    protected Vector3f rotation = new Vector3f(0, 0, 0);
    protected Vector3f position = new Vector3f(1.0f, 1.0f, 1.0f);
    protected Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);


    public abstract VertexArrayObject getVertexArrayObject();
    public abstract List<Texture> getTexturesToAttach();

    public abstract List<IRenderTarget> getChildren();
    public abstract void update();

    public abstract void destroy();

    public Matrix4f getWorldMatrix(){
        Matrix4f result = new Matrix4f().identity();
        result.translate(position);

        result.rotate(rotation.x, 1, 0, 0);
        result.rotate(rotation.y, 0, 1, 0);
        result.rotate(rotation.z, 0, 0, 1);

        result.scale(scale);
        return result;
    }

}
