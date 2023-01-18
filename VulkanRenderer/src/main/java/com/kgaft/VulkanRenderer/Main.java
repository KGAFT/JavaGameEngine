package com.kgaft.VulkanRenderer;

import java.util.Map;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import com.kgaft.VulkanRenderer.VulkanContext.VulkanDevice;
import com.kgaft.VulkanRenderer.VulkanContext.VulkanInstance;
import com.kgaft.VulkanRenderer.VulkanContext.VulkanRenderContext;
import com.kgaft.VulkanRenderer.VulkanContext.VulkanLogger.VulkanLogger;
import com.kgaft.VulkanRenderer.Window.Window;

public class Main {
    public static void main(String[] args) {

        Window.prepareWindow(1024, 720, "KGAFTEngine", true);
        /*
         * Engine engine = new Engine(Window.getWindow());
         * TestScene testScene = new TestScene();
         * engine.setCurrentScene(testScene);
         * try {
         * engine.start();
         * } catch (InterruptedException e) {
         * e.printStackTrace();
         * }
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
        while (Window.getWindow().isWindowActive()) {
            renderContext.update();
            Window.getWindow().postEvents();
        }
    }
}
