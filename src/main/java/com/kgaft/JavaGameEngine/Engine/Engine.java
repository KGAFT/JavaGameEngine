package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.Camera.CameraManager;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexBufferObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import java.util.HashMap;

public class Engine {
    private Window target;
    public Engine(Window window){
        this.target = window;
        GL.createCapabilities();
        GL33.glViewport(0, 0, window.getWidth(), window.getHeight());
    }
    public void start(){
        Camera camera = new Camera();
        target.addResizeCallBack((newWidth, newHeight) -> {
            GL33.glViewport(0, 0, newWidth, newHeight);
        });
        HashMap<String, Integer> shadersToInit = new HashMap<>();
        shadersToInit.put("Shaders/default.frag", GL33.GL_FRAGMENT_SHADER);
        shadersToInit.put("Shaders/default.vert", GL33.GL_VERTEX_SHADER);
        Shader.initializeShader(shadersToInit);
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        VertexArrayObject vao = VertexArrayObject.createVao();
        vao.attachEbo(ElementBufferObject.createEbo(indices));
        vao.attachVbo(0, VertexBufferObject.createVbo(vertices, 3), true);
        CameraManager.registerCameraAndSwitchToIt(camera);
        Matrix4f position = new Matrix4f().identity();
        float[] posData = new float[4*4];
        posData = position.get(posData);

        while (target.isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0, 0.5f, 0, 1);
            Shader.attach();
            CameraManager.handleCamera();
            Shader.uniformMatrix4f(posData, "modelMatrix");
            vao.draw();
            target.postEvents();
        }
    }
}
