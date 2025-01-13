package com.kgaft.KGAFTEngine.Window;

import com.kgaft.VulkanLib.Device.Synchronization.IResizeCallback;
import com.kgaft.VulkanLib.Utils.VkErrorException;
import org.lwjgl.vulkan.VkInstance;

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
                if (!vulkan) {
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
                    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
                    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                    glfwWindowHint(GLFW_SAMPLES, 4);
                } else {
                    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
                }

                //  glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
                long windowHandle = glfwCreateWindow(width, height, windowTitle, 0, 0);
                if (windowHandle != 0) {
                    glfwMakeContextCurrent(windowHandle);
                    //glfwSetWindowAttrib(windowHandle, GLFW_DECORATED, GLFW_FALSE);
                    windowInstance = new Window(windowHandle, width, height);
                    windowInstance.vulkan = vulkan;
                }
            }
        }

    }
    private long windowHandle;
    private boolean vulkan;
    private List<IResizeCallback> resizeCallBackList = new ArrayList<>();
    private int width;
    private int height;
    private int cursorMode = FIXED_HIDDEN_CURSOR_MODE;

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
            try{
                resizeCallBack.resized(newWidth, newHeight);
            }catch (Exception | VkErrorException e){
                e.printStackTrace();

            }
        });
    }


    public long getSurface(VkInstance instance) {
        long[] result = new long[1];
        glfwCreateWindowSurface(instance, windowHandle, null, result);
        return result[0];
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowHandle, title);
    }


    public void postEvents() {
        if (!vulkan) {
            glfwSwapBuffers(windowHandle);
        }
        glfwPollEvents();
    }

    public void addResizeCallBack(IResizeCallback resizeCallBack) {
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
}