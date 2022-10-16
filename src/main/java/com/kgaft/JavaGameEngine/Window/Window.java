package com.kgaft.JavaGameEngine.Window;

import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Window {

    private static Window windowInstance;

    public static Window getWindow() {
        return windowInstance;
    }


    public static void prepareWindow(int width, int height, String windowTitle) {
        if (windowInstance == null) {
            if (glfwInit()) {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                long windowHandle = glfwCreateWindow(width, height, windowTitle, 0, 0);
                if (windowHandle != 0) {
                    glfwMakeContextCurrent(windowHandle);

                    windowInstance = new Window(windowHandle, width, height);
                }
            }
        }

    }

    private long windowHandle;

    private List<WindowResizeCallBack> resizeCallBackList = new ArrayList<>();

    private int startWidth;
    private int startHeight;

    private Window(long windowHandle, int startWidth, int startHeight) {
        this.windowHandle = windowHandle;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.windowHandle = windowHandle;
        glfwSetWindowSizeCallback(windowHandle, (l, i, i1) -> {
            checkResizeCallBacks(i, i1);
        });
    }

    private void checkResizeCallBacks(int newWidth, int newHeight) {
        resizeCallBackList.forEach(resizeCallBack -> {
            resizeCallBack.resized(newWidth, newHeight);
        });
    }


    public long getWindowHandle() {
        return windowHandle;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }


    public void postEvents() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public void addResizeCallBack(WindowResizeCallBack resizeCallBack) {
        resizeCallBackList.add(resizeCallBack);
    }

    public boolean isWindowActive() {
        return !glfwWindowShouldClose(windowHandle);
    }

    public int getStartWidth() {
        return startWidth;
    }

    public int getStartHeight() {
        return startHeight;
    }
}
