package com.kgaft.KGAFTEngine.VulkanRenderer;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.GraphicsPipeline;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfigStruct;
import com.kgaft.KGAFTEngine.Window.Window;

public class VulkanRenderContext {
    private GraphicsPipeline graphicsPipeline;
    private VulkanSwapChain swapChain;
    private VulkanDevice device;
    private VulkanInstance vulkanInstance;
    private VulkanLogger vulkanLogger;
    private Window window;
    private boolean vSync;
    public VulkanRenderContext(boolean needToLogVulkan) {
        if (needToLogVulkan) {
            vulkanLogger = new VulkanLogger();
        }
        VulkanInstance.createInstance(vulkanLogger);
        vulkanInstance = VulkanInstance.getVulkanInstance();
        device = new VulkanDevice(vulkanInstance);
    }
    public void initialize(boolean vSync, VkPhysicalDevice physicalDevice, Window window) {
        this.window = window;
        this.vSync = vSync;
        Configuration.STACK_SIZE.set(1024*8196);
        device.setDeviceToCreate(physicalDevice);
        device.load(vulkanLogger != null);
        swapChain = new VulkanSwapChain(device, window);
        swapChain.load(vSync);
        graphicsPipeline = new GraphicsPipeline(device, swapChain);
        try {
            graphicsPipeline.load(PipelineConfigStruct.defaultConfig(window, device, swapChain));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load graphics pipeline");
        }
        graphicsPipeline.createCommandBuffers();
    }
    public void update(){
        graphicsPipeline.renderCommandBuffers();
        graphicsPipeline.update();
        
    }
    public HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> enumerateSupportedGPUS() {
        return device.enumerateSupportedDevices();
    }
}
