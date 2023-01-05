package com.kgaft.KGAFTEngine.Editor;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;

public class EditorMain extends JFrame{
    private JPanel root;
    private JPanel viewPort;
    private JList graphicalObjectsList;
    private AWTGLCanvas awtglCanvas;

    public EditorMain(){
        add(root);
        setSize(1920, 1080);
        setTitle("KGAFTEngineEditor");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GLData data = new GLData();
        data.majorVersion = 3;
        data.minorVersion = 3;
        data.profile = GLData.Profile.CORE;
        data.samples = 4;
        awtglCanvas = new AWTGLCanvas() {
            @Override
            public void initGL() {

            }

            @Override
            public void paintGL() {

                this.swapBuffers();

            }
        };
        viewPort.setLayout(new GridLayout(1, 1));
        viewPort.add(awtglCanvas);
        awtglCanvas.setVisible(true);
        setVisible(true);
        new Thread(()->{
            while(true){
                awtglCanvas.render();
            }

        }).start();
    }
}
