package com.kgaft.KGAFTEngine;

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.GraphicsPipeline;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfigStruct;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanInstance;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanLogger;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanSwapChain;
import com.kgaft.KGAFTEngine.Window.Window;

import java.net.URISyntaxException;

import org.lwjgl.vulkan.VkPhysicalDevice;

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
        VulkanInstance.createInstance(new VulkanLogger());
        VulkanDevice device = new VulkanDevice(VulkanInstance.getVulkanInstance());
        device.setDeviceToCreate((VkPhysicalDevice) device.enumerateSupportedDevices().keySet().toArray()[0]);
        device.load(true);
        VulkanSwapChain swapChain = new VulkanSwapChain(device);
        swapChain.load(true);
        GraphicsPipeline graphicsPipeline = new GraphicsPipeline(device, swapChain);
        try {
            graphicsPipeline.load(PipelineConfigStruct.defaultConfig(Window.getWindow(), device, swapChain));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        graphicsPipeline.createCommandBuffers();

        while(Window.getWindow().isWindowActive()){
            graphicsPipeline.update();
            Window.getWindow().postEvents();
        }
    }
}