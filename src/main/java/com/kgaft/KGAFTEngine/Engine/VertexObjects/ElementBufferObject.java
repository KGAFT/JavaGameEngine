package com.kgaft.KGAFTEngine.Engine.VertexObjects;

import org.lwjgl.opengl.GL33;

public class ElementBufferObject {

    protected boolean attached = false;
    protected boolean destroyed = false;

    public static ElementBufferObject createEbo(int[] indices) {
        int eboId = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, eboId);

        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indices, GL33.GL_STATIC_DRAW);
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
        return new ElementBufferObject(eboId, indices.length);
    }

    private int eboId;
    private int indicesAmount;

    public ElementBufferObject(int eboId, int indicesAmount) {
        this.eboId = eboId;
        this.indicesAmount = indicesAmount;
    }

    public void bind() {
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, eboId);
    }

    public void unBind() {
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getIndicesAmount() {
        return indicesAmount;
    }

    public void delete(){
        GL33.glDeleteBuffers(eboId);
        destroyed = true;
    }

    public boolean isAttached() {
        return attached;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
