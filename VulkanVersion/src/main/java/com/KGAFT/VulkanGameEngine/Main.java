package com.KGAFT.VulkanGameEngine;

import com.KGAFT.VulkanGameEngine.VKRenderer.VulkanRenderer;
import com.KGAFT.VulkanGameEngine.Window.Window;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

public class Main {
    public static void main(String[] args) {
        Window.prepareWindow(800, 600, "hell");
        VulkanRenderer vulkanRenderer = new VulkanRenderer();
        vulkanRenderer.createInstance();

        vulkanRenderer.getAvailableToRenderPhysicalDevices().forEach((device)->{
            System.out.println(device.getSupportedQueueFamiliesWithGraphics());
        });
        while(Window.getWindow().isWindowActive()){
            Window.getWindow().postEvents();
        }
    }
}