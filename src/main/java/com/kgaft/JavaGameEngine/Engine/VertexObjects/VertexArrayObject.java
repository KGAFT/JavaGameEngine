package com.kgaft.JavaGameEngine.Engine.VertexObjects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class VertexArrayObject {

    public static VertexArrayObject createVao() {
        return new VertexArrayObject(GL33.glGenVertexArrays());
    }

    private int vaoId;

    private int layoutLocationDataToDraw;

    private ElementBufferObject drawEbo;

    private VertexArrayObject(int vaoId) {
        this.vaoId = vaoId;
    }

    public void attachVbo(int layoutLocation, VertexBufferObject vbo, boolean isDrawTarget) {
        bind();
        vbo.bind();
        GL33.glVertexAttribPointer(layoutLocation, vbo.getStride(), GL11.GL_FLOAT, false, 0, 0);
        unBind();
        vbo.unBind();
        if (isDrawTarget) {
            layoutLocationDataToDraw = layoutLocation;
        }
    }

    public void attachEbo(ElementBufferObject ebo) {
        bind();
        ebo.bind();
        unBind();
        ebo.unBind();
        drawEbo = ebo;
    }

    public void bind() {
        GL33.glBindVertexArray(vaoId);
    }

    public void draw() {
        bind();
        GL33.glEnableVertexAttribArray(layoutLocationDataToDraw);
        GL33.glDrawElements(GL11.GL_TRIANGLES, drawEbo.getIndicesAmount(), GL11.GL_UNSIGNED_INT, 0);
        GL33.glDisableVertexAttribArray(layoutLocationDataToDraw);
        GL33.glBindVertexArray(vaoId);
    }

    public void unBind() {
        GL33.glBindVertexArray(0);
    }
}
