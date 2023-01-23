package com.kgaft.KGAFTEngine;


import com.kgaft.KGAFTEngine.Engine.Engine;
import com.kgaft.KGAFTEngine.Window.Window;

public class Main {
    public static void main(String[] args) {

        Window.prepareWindow(1024, 720, "KGAFTEngine", false);
        
        Engine engine = new Engine(Window.getWindow());
        TestScene testScene = new TestScene();
        engine.setCurrentScene(testScene);
        try {
            engine.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        
    }
}