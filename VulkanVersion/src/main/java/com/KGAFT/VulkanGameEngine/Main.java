package com.KGAFT.VulkanGameEngine;

import com.KGAFT.VulkanGameEngine.VKRenderer.PhysicalDevice;
import com.KGAFT.VulkanGameEngine.VKRenderer.VulkanRenderer;
import com.KGAFT.VulkanGameEngine.Window.Window;

public class Main {
    public static void main(String[] args) {
        Window.prepareWindow(800, 600, "hell");
        VulkanRenderer vulkanRenderer = new VulkanRenderer();
        vulkanRenderer.createInstance();

        PhysicalDevice device = vulkanRenderer.getAvailableToRenderPhysicalDevices().get(0);
        vulkanRenderer.createLogicalDevice(device);
        while(Window.getWindow().isWindowActive()){
            Window.getWindow().postEvents();
        }
    }
}