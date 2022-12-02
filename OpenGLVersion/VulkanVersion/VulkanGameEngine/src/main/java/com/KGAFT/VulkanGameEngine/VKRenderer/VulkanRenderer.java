package com.KGAFT.VulkanGameEngine.VKRenderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class VulkanRenderer {

    private VkInstance vkInstance;
    public VulkanRenderer(){
        VkApplicationInfo vkApplicationInfo = VkApplicationInfo.create();
        vkApplicationInfo.sType(VK13.VK_STRUCTURE_TYPE_APPLICATION_INFO);
       // vkApplicationInfo.pApplicationName(ByteBuffer.wrap("JavaGameEngine".getBytes(StandardCharsets.UTF_8)));
        vkApplicationInfo.applicationVersion(VK13.VK_MAKE_VERSION(1, 0, 0));
      //  vkApplicationInfo.pEngineName(ByteBuffer.wrap("JavaGameEngine".getBytes(StandardCharsets.UTF_8)));
        vkApplicationInfo.engineVersion(VK13.VK_MAKE_VERSION(1, 0, 0));
        vkApplicationInfo.apiVersion(VK13.VK_API_VERSION_1_3);
        VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.create();
        createInfo.sType(VK13.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
        createInfo.pApplicationInfo(vkApplicationInfo);
        PointerBuffer requiredExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        createInfo.ppEnabledExtensionNames(requiredExtensions);
        PointerBuffer instanceHandle = PointerBuffer.allocateDirect(1);

        VK13.vkCreateInstance(createInfo, null, instanceHandle);
        VkInstance instance = new VkInstance(instanceHandle.get(0), createInfo);
        instanceHandle.free();
        this.vkInstance = instance;
    }
}
