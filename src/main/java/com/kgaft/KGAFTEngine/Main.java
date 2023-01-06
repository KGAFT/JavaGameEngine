package com.kgaft.KGAFTEngine;

import com.kgaft.KGAFTEngine.Engine.Engine;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanInstance;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanLogger;
import com.kgaft.KGAFTEngine.Window.Window;
import org.lwjgl.vulkan.VkPhysicalDevice;

public class Main {
    public static void main(String[] args) {

        Window.prepareWindow(1920, 1080, "KGAFTEngine", true);
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
        System.out.println(device.load(true));
    }
}