package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexBufferObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import com.kgaft.JavaGameEngine.Window.WindowResizeCallBack;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.util.HashMap;

public class Engine {
    private Window target;
    public Engine(Window window){
        this.target = window;
        GL.createCapabilities();
        GL33.glViewport(0, 0, window.getStartWidth(), window.getStartHeight());
    }
    public void start(){
        target.addResizeCallBack((newWidth, newHeight) -> {
            GL33.glViewport(0, 0, newWidth, newHeight);
        });
        HashMap<String, Integer> shadersToInit = new HashMap<>();
        shadersToInit.put("Shaders/default.frag", GL33.GL_FRAGMENT_SHADER);
        shadersToInit.put("Shaders/default.vert", GL33.GL_VERTEX_SHADER);
        Shader.initializeShader(shadersToInit);
        float[] v_quad = {0.5f, 0.5f, 0,   -0.5f, 0.5f, 0,   -0.5f, -0.5f, 0,   0.5f, -0.5f, 0 };

        int [] i_quad = {0, 1, 2, 0, 2, 3};

        VertexArrayObject vao = VertexArrayObject.createVao();
        vao.attachEbo(ElementBufferObject.createEbo(i_quad));
        vao.attachVbo(1, VertexBufferObject.createVbo(v_quad, 3), true);

        while (target.isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
            GL33.glClearColor(100, 0, 0, 1);
            Shader.attach();
            vao.draw();
            target.postEvents();
        }
    }
}
