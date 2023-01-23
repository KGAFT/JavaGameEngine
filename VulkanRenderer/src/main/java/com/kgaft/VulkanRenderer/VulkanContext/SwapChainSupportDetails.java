package com.kgaft.VulkanRenderer.VulkanContext;

import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.util.ArrayList;
import java.util.List;

public class SwapChainSupportDetails {
    private VkSurfaceCapabilitiesKHR capabilitiesKHR;
    private List<VkSurfaceFormatKHR> surfaceFormats = new ArrayList<>();
    private List<Integer> presentModes = new ArrayList<>();

    public SwapChainSupportDetails(VkSurfaceCapabilitiesKHR capabilitiesKHR, List<VkSurfaceFormatKHR> surfaceFormats, List<Integer> presentModes) {
        this.capabilitiesKHR = capabilitiesKHR;
        this.surfaceFormats = surfaceFormats;
        this.presentModes = presentModes;
    }

    public SwapChainSupportDetails() {
    }

    public VkSurfaceCapabilitiesKHR getCapabilitiesKHR() {
        return capabilitiesKHR;
    }

    public void setCapabilitiesKHR(VkSurfaceCapabilitiesKHR capabilitiesKHR) {
        this.capabilitiesKHR = capabilitiesKHR;
    }

    public List<VkSurfaceFormatKHR> getSurfaceFormats() {
        return surfaceFormats;
    }

    public void setSurfaceFormats(List<VkSurfaceFormatKHR> surfaceFormats) {
        this.surfaceFormats = surfaceFormats;
    }

    public List<Integer> getPresentModes() {
        return presentModes;
    }

    public void setPresentModes(List<Integer> presentModes) {
        this.presentModes = presentModes;
    }
}
