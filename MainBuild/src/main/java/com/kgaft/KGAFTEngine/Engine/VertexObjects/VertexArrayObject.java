package com.kgaft.KGAFTEngine.Engine.VertexObjects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.util.HashMap;

public class VertexArrayObject {

    public static VertexArrayObject createVao() {
        return new VertexArrayObject(GL33.glGenVertexArrays());
    }

    private int vaoId;


    private ElementBufferObject drawEbo;

    private HashMap<Integer, VertexBufferObject> VBOs = new HashMap<Integer, VertexBufferObject>();

    private VertexArrayObject(int vaoId) {
        this.vaoId = vaoId;
    }

    public void attachVbo(int layoutLocation, VertexBufferObject vbo) {

        if (!vbo.attached) {
            if (!vbo.destroyed) {
                bind();
                vbo.bind();
                GL33.glVertexAttribPointer(layoutLocation, vbo.getStride(), GL11.GL_FLOAT, false, 0, 0);
                unBind();
                vbo.unBind();

                vbo.attached = true;
                VBOs.put(layoutLocation, vbo);
            } else {
                throw new RuntimeException("Error: you cannot attach destroyed vbo");
            }
        } else {
            throw new RuntimeException("Error: you cannot attach already attached vbo to another vao...");
        }

    }

    public void attachEbo(ElementBufferObject ebo) {
        if (!ebo.attached) {
            if (!ebo.destroyed) {
                bind();
                ebo.bind();
                unBind();
                ebo.unBind();
                drawEbo = ebo;
                ebo.attached = true;
            } else {
                throw new RuntimeException("Error: you cannot attach destroyed ebo");
            }
        } else {
            throw new RuntimeException("Error: you cannot attach already attached ebo to another vao...");
        }

    }

    public void bind() {
        GL33.glBindVertexArray(vaoId);
    }

    public void draw() {
        if (!drawEbo.destroyed) {
            bind();
            VBOs.keySet().forEach(GL33::glEnableVertexAttribArray);
            GL33.glDrawElements(GL11.GL_TRIANGLES, drawEbo.getIndicesAmount(), GL11.GL_UNSIGNED_INT, 0);
            VBOs.keySet().forEach(GL33::glDisableVertexAttribArray);
            GL33.glBindVertexArray(vaoId);
        } else {
            throw new RuntimeException("Error: ebo destroyed");
        }

    }

    public void putData() {
        if (!drawEbo.destroyed) {
            bind();
            VBOs.keySet().forEach(GL33::glEnableVertexAttribArray);
        } else {
            throw new RuntimeException("Error: ebo destroyed");
        }
    }

    public void removeData() {
        VBOs.keySet().forEach(GL33::glDisableVertexAttribArray);
        GL33.glBindVertexArray(vaoId);
    }

    public void unBind() {
        GL33.glBindVertexArray(0);
    }

    public void destroy() {
        GL33.glDeleteVertexArrays(vaoId);
        drawEbo.delete();
        VBOs.values().forEach(VertexBufferObject::delete);
        VBOs.clear();
    }
}
