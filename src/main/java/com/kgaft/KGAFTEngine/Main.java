package com.kgaft.KGAFTEngine;


import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanRenderContext;
import com.kgaft.KGAFTEngine.Window.Window;
import java.util.Map;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

public class Main {
    public static void main(String[] args) {

        Window.prepareWindow(1024, 720, "KGAFTEngine", true);
        /*
        Engine engine = new Engine(Window.getWindow());
        TestScene testScene = new TestScene();
        engine.setCurrentScene(testScene);
        try {
            engine.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */
        VulkanRenderContext renderContext = new VulkanRenderContext(true);
        VkPhysicalDevice physicalDevice = null;
        for (Map.Entry<VkPhysicalDevice, VkPhysicalDeviceProperties> entry : renderContext.enumerateSupportedGPUS()
                .entrySet()) {
            if (physicalDevice == null) {
                physicalDevice = entry.getKey();
            }
            System.out.println(entry.getValue().deviceNameString());
        }
        renderContext.initialize(true, physicalDevice, Window.getWindow());
        while(Window.getWindow().isWindowActive()){
            renderContext.update();
            Window.getWindow().postEvents();
        }
    }
}