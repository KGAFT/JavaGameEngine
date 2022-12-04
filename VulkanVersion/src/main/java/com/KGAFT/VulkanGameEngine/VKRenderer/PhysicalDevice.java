package com.KGAFT.VulkanGameEngine.VKRenderer;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

public class PhysicalDevice {
    private VkPhysicalDeviceProperties properties;
    private VkPhysicalDeviceFeatures features;
    private VkPhysicalDevice device;

    private int graphicsQueueFamilyIndex = -1;
    private int score;

    public PhysicalDevice(VkPhysicalDeviceProperties properties, VkPhysicalDeviceFeatures features, VkPhysicalDevice device, int score) {
        this.properties = properties;
        this.features = features;
        this.device = device;
        this.score = score;
    }

    public VkPhysicalDeviceProperties getProperties() {
        return properties;
    }

    public void setProperties(VkPhysicalDeviceProperties properties) {
        this.properties = properties;
    }

    public VkPhysicalDeviceFeatures getFeatures() {
        return features;
    }

    public void setFeatures(VkPhysicalDeviceFeatures features) {
        this.features = features;
    }

    public VkPhysicalDevice getDevice() {
        return device;
    }

    public void setDevice(VkPhysicalDevice device) {
        this.device = device;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGraphicsQueueFamilyIndex() {
        return graphicsQueueFamilyIndex;
    }

    public void setGraphicsQueueFamilyIndex(int graphicsQueueFamilyIndex) {
        this.graphicsQueueFamilyIndex = graphicsQueueFamilyIndex;
    }
}
