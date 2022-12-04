package com.kgaft.JavaGameEngine;

import com.kgaft.JavaGameEngine.Engine.Engine;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Window.Window;


import java.util.HashMap;


public class Main {
    public static void main(String[] args) {
        Window.prepareWindow(800, 600, "HelloWorld!");
        Engine engine = new Engine();
        engine.setCurrentScene(new TestScene());
        engine.start();
    }
}