package com.kgaft.KGAFTEngine.Window;

import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;

public class Window {

    public static final int FIXED_HIDDEN_CURSOR_MODE = 0;
    public static final int DYNAMIC_CURSOR = 1;
    public static final int BOTH_TYPES_CALLBACK = 2;
    private static Window windowInstance;


    public static Window getWindow() {
        return windowInstance;
    }

    public static void prepareWindow(int width, int height, String windowTitle, boolean vulkan) {
        if (windowInstance == null) {
            if (glfwInit()) {
                if(!vulkan){
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
                    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                    glfwWindowHint(GLFW_SAMPLES, 4);
                }
                else{
                    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
                }

              //  glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
                long windowHandle = glfwCreateWindow(width, height, windowTitle, 0, 0);
                if (windowHandle != 0) {
                    glfwMakeContextCurrent(windowHandle);
                    //glfwSetWindowAttrib(windowHandle, GLFW_DECORATED, GLFW_FALSE);
                    glfwSwapInterval(0);
                    windowInstance = new Window(windowHandle, width, height);
                    windowInstance.vulkan = vulkan;
                    windowInstance.windowTitle = windowTitle;
                }
            }
        }

    }

    private long windowHandle;

    private boolean vulkan;

    private List<WindowResizeCallBack> resizeCallBackList = new ArrayList<>();

    private List<KeyBoardCallBack> keyBoardsCallBacks = new ArrayList<>();

    private List<MouseMovementCallBack> mouseCallBacks = new ArrayList<>();

    private double lastTime;

    private double lastMouseX;
    private double lastMouseY;
    private int width;
    private int height;
    private int counter;
    private int cursorMode = FIXED_HIDDEN_CURSOR_MODE;
    private String windowTitle;
    private Window(long windowHandle, int width, int height) {
        this.windowHandle = windowHandle;
        this.width = width;
        this.height = height;
        this.windowHandle = windowHandle;
        glfwSetWindowSizeCallback(windowHandle, (l, i, i1) -> {
            this.width = i;
            this.height = i1;
            checkResizeCallBacks(i, i1);
        });
    }

    private void checkResizeCallBacks(int newWidth, int newHeight) {
        resizeCallBackList.forEach(resizeCallBack -> {
            resizeCallBack.resized(newWidth, newHeight);
        });
    }

    private void checkKeyBoardsCallBacks() {
        keyBoardsCallBacks.forEach(callBack -> {
            for (int keyCode : callBack.getKeyCodes()) {
                if (glfwGetKey(windowHandle, keyCode) == GLFW_PRESS) {
                    callBack.keyPressed(keyCode);
                }
            }
        });
    }

    public void preRenderEvents(){
        checkMouseCallBacks();
        checkKeyBoardsCallBacks();
        double currentTime = glfwGetTime();
        double timeDiff = currentTime-lastTime;
        counter++;
        if (timeDiff >= 1.0 / 30.0){
            double fps = (1.0 / timeDiff) * counter;
            glfwSetWindowTitle(windowHandle, windowTitle+" FPS: "+(int)fps+" ms: "+(int)((timeDiff / counter) * 1000));
            lastTime = currentTime;
            counter = 0;
        }
    }
    private void checkMouseCallBacks() {
        if (cursorMode == FIXED_HIDDEN_CURSOR_MODE) {
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            double[] cursorX = new double[1];
            double[] cursorY = new double[1];
            glfwGetCursorPos(windowHandle, cursorX, cursorY);
            int xChange = 0;
            int yChange = 0;
            if (Math.abs(cursorX[0] - width / 2) > 0) {
                xChange = (int) ((cursorX[0] - width / 2) / Math.abs(cursorX[0] - width / 2));
            }
            if (Math.abs(cursorY[0] - height / 2) > 0) {
                yChange = ((int) ((cursorY[0] - (double) height / 2) / Math.abs(cursorY[0] - (double) height / 2)));
            }
            if (xChange != 0 || yChange != 0) {
                glfwSetCursorPos(windowHandle, width / 2, height / 2);
                int finalXChange = xChange;
                int finalYChange = yChange;
                mouseCallBacks.forEach(callBack -> {
                    if (callBack.getWorkMode() == FIXED_HIDDEN_CURSOR_MODE || callBack.getWorkMode()==BOTH_TYPES_CALLBACK) {
                        callBack.mouseMoved(finalXChange, finalYChange);
                    }
                });
            }
        }
        else if(cursorMode== DYNAMIC_CURSOR){
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            double[] cursorX = new double[1];
            double[] cursorY = new double[1];
            glfwGetCursorPos(windowHandle, cursorX, cursorY);
            double xChange = cursorX[0]-lastMouseX;
            double yChange = cursorY[0]-lastMouseY;
            lastMouseX = cursorX[0];
            lastMouseY = cursorX[0];
            if(xChange!=0 || yChange!=0){
                mouseCallBacks.forEach(callBack->{
                    if(callBack.getWorkMode()== DYNAMIC_CURSOR || callBack.getWorkMode() == BOTH_TYPES_CALLBACK){
                        callBack.mouseMoved(xChange, yChange);
                    }
                });
            }

        }


    }

    public long getSurface(VkInstance instance){
        long[] result = new long[1];
        glfwCreateWindowSurface(instance, windowHandle, null, result);
        return result[0];
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
        windowTitle = title;
    }


    public void postEvents() {
        if(!vulkan){
            glfwSwapBuffers(windowHandle);
        }
        glfwPollEvents();
    }

    public void addResizeCallBack(WindowResizeCallBack resizeCallBack) {
        resizeCallBackList.add(resizeCallBack);
    }

    public boolean isWindowActive() {
        return !glfwWindowShouldClose(windowHandle);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCursorMode() {
        return cursorMode;
    }

    public void setCursorMode(int cursorMode) {
        this.cursorMode = cursorMode;
    }
    public void addMouseMoveCallBack(MouseMovementCallBack mouseMovementCallBack){
        mouseCallBacks.add(mouseMovementCallBack);
    }
    public void addKeyBoardCallBack(KeyBoardCallBack keyBoardCallBack){
        keyBoardsCallBacks.add(keyBoardCallBack);
    }
}
