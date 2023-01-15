package com.kgaft.KGAFTEngine;

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.GraphicsPipeline;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfigStruct;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanInstance;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanLogger;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanRenderingContext;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanSwapChain;
import com.kgaft.KGAFTEngine.Window.Window;

import java.net.URISyntaxException;
import java.util.Map;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

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

        VulkanRenderingContext renderingContext = new VulkanRenderingContext(true);
        VkPhysicalDevice physicalDevice = null;
        for (Map.Entry<VkPhysicalDevice, VkPhysicalDeviceProperties> entry : renderingContext.enumerateSupportedGPUS()
                .entrySet()) {
            if (physicalDevice == null) {
                physicalDevice = entry.getKey();
            }
            System.out.println(entry.getValue().deviceNameString());
        }
        renderingContext.initialize(false, physicalDevice, Window.getWindow());
        renderingContext.updateDrawData();
        while (Window.getWindow().isWindowActive()) {
            renderingContext.update();
            Window.getWindow().postEvents();
        }
    }
}