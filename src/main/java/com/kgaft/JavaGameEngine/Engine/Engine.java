package com.kgaft.JavaGameEngine.Engine;

import com.kgaft.JavaGameEngine.Engine.Camera.Camera;
import com.kgaft.JavaGameEngine.Engine.Camera.CameraManager;
import com.kgaft.JavaGameEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Mesh;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Model;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.ModelLoader;
import com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Texture;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexBufferObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class Engine {



    public Engine(){

        GL.createCapabilities();
        GL33.glViewport(0, 0, Window.getWindow().getWidth(), Window.getWindow().getHeight());
    }
    public void start(){
        Camera camera = new Camera();
        Window.getWindow().addResizeCallBack((newWidth, newHeight) -> {
            GL33.glViewport(0, 0, newWidth, newHeight);
        });
        HashMap<String, Integer> shadersToInit = new HashMap<>();
        shadersToInit.put("Shaders/default.frag", GL33.GL_FRAGMENT_SHADER);
        shadersToInit.put("Shaders/default.vert", GL33.GL_VERTEX_SHADER);
        Shader.initializeShader(shadersToInit);
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        float[] positions = new float[]{
// VO
                -0.5f, 0.5f, 0.5f,
// V1
                -0.5f, -0.5f, 0.5f,
// V2
                0.5f, -0.5f, 0.5f,
// V3
                0.5f, 0.5f, 0.5f,
// V4
                -0.5f, 0.5f, -0.5f,
// V5
                0.5f, 0.5f, -0.5f,
// V6
                -0.5f, -0.5f, -0.5f,
// V7
                0.5f, -0.5f, -0.5f
        };

        int[] indices = new int[] {
// Front face
                0, 1, 3, 3, 1, 2,
// Top Face
                4, 0, 3, 5, 4, 3,
// Right face
                3, 2, 7, 5, 3, 7,
// Left face
                6, 1, 0, 6, 0, 4,
// Bottom face
                2, 1, 6, 2, 6, 7,
// Back face
                7, 6, 4, 7, 4, 5,
        };

        float[] UVs = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f
        };

        Mesh mesh = Mesh.createMesh(positions, UVs, new float[8*3], indices);
        CameraManager cameraManager = new CameraManager();
        cameraManager.registerCameraAndSwitchToIt(camera);
        Texture texture = null;
        Texture secondTexture = null;
        Texture thirdTexture = null;
        try {
            texture = Texture.loadTexture(Engine.class.getClassLoader().getResource("textures/Texture.png").getPath(), Texture.BASE_COLOR_TEXTURE);
            secondTexture = Texture.loadTexture(Engine.class.getClassLoader().getResource("textures/secondTexture.png").getPath(), Texture.BASE_COLOR_TEXTURE);
            thirdTexture = Texture.loadTexture(Engine.class.getClassLoader().getResource("textures/thirdTexture.png").getPath(),Texture.BASE_COLOR_TEXTURE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PlayerNonPhysicsMode playerNonPhysicsMode = new PlayerNonPhysicsMode();
        playerNonPhysicsMode.addDependentObject(camera);
        Window.getWindow().addKeyBoardCallBack(playerNonPhysicsMode);
        Window.getWindow().addMouseMoveCallBack(playerNonPhysicsMode);
        long framesAmount = 0;
        while (Window.getWindow().isWindowActive()){
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
            GL33.glClearColor(0, 0.5f, 0, 1);
            Shader.attach();
            if(framesAmount%2==0){
                secondTexture.attach();
            }
            else if(framesAmount%3==0){
                thirdTexture.attach();
            }
            else{
                texture.attach();

            }
            mesh.updateAndLoadToGameWorld();
            Window.getWindow().preRenderEvents();
            cameraManager.handleCamera();
            Window.getWindow().postEvents();
            framesAmount++;
        }
    }

}
