package com.kgaft.JavaGameEngine.Engine.VertexObjects;

import org.lwjgl.opengl.GL33;

public class VertexBufferObject {

    public static VertexBufferObject createVbo(float[] vertices, int stride) {
        int vboId = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboId);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        GL33.glBindVertexArray(0);
        return new VertexBufferObject(vboId, stride);
    }

    private int vboId;
    private int stride;

    private VertexBufferObject(int vboId, int stride) {
        this.vboId = vboId;
        this.stride = stride;
    }

    public int getStride() {
        return stride;
    }

    public void bind() {
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboId);
    }

    public void unBind() {
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }
}
