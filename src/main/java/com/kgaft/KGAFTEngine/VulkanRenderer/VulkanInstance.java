package com.kgaft.KGAFTEngine.VulkanRenderer;

import com.kgaft.KGAFTEngine.Window.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ConcurrentModificationException;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME;

public class VulkanInstance {

    private static VulkanInstance vulkanInstance;

    public static void createInstance(VulkanLogger vulkanLogger){
        vulkanInstance = new VulkanInstance(vulkanLogger);
    }

    public static VulkanInstance getVulkanInstance() {
        return vulkanInstance;
    }

    private final String validationLayerName = "VK_LAYER_KHRONOS_validation";
    private VkInstance vkInstance;
    private VulkanLogger vulkanLogger;
    private long surface;
    private VulkanInstance(VulkanLogger vulkanLogger) {
        if (vulkanLogger != null && !checkValidationLayersSupport()) {
            throw new ConcurrentModificationException("Error: this device does not support validation layers, try to install vulkan sdk to fix this...");
        }
        try(MemoryStack stack = MemoryStack.stackPush()){
            VkApplicationInfo vkApplicationInfo = VkApplicationInfo.callocStack(stack);
            vkApplicationInfo.sType(VK13.VK_STRUCTURE_TYPE_APPLICATION_INFO);
            vkApplicationInfo.pApplicationName(stack.UTF8Safe("KGAFTEngine"));
            vkApplicationInfo.applicationVersion(VK13.VK_MAKE_VERSION(1, 0, 0));
            vkApplicationInfo.pEngineName(stack.UTF8Safe("KGAFTEngine"));
            vkApplicationInfo.engineVersion(VK13.VK_MAKE_VERSION(1, 0, 0));
            vkApplicationInfo.apiVersion(VK13.VK_API_VERSION_1_3);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.callocStack(stack);
            createInfo.sType(VK13.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(vkApplicationInfo);
            PointerBuffer pointerBuffer = getRequiredExtensions(vulkanLogger!=null, stack);
            createInfo.ppEnabledExtensionNames(pointerBuffer);
            if(vulkanLogger!=null){
                ByteBuffer layerName = stack.UTF8Safe(validationLayerName);
                PointerBuffer layerPointer = stack.mallocPointer(1);
                layerPointer.put(layerName);
                layerPointer.rewind();
                createInfo.ppEnabledLayerNames(layerPointer);
                VkDebugUtilsMessengerCreateInfoEXT debugInfo = vulkanLogger.getRequiredInfo(stack);
                createInfo.pNext(debugInfo.address());
            }
            PointerBuffer instance = stack.mallocPointer(1);
            if(VK13.vkCreateInstance(createInfo, null, instance)==VK13.VK_SUCCESS){
                vkInstance = new VkInstance(instance.get(0), createInfo);
            }
            else{
                throw new RuntimeException("Error while creating instance");
            }
            if(vulkanLogger!=null){
                vulkanLogger.load(vkInstance, stack);
            }
            this.surface = Window.getWindow().getSurface(vkInstance);
        }
    }


    private PointerBuffer getRequiredExtensions(boolean enabledDebugLayers, MemoryStack stack) {

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if(enabledDebugLayers) {
            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity()+1);
            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8Safe(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            return extensions.rewind();
        }

        return glfwExtensions;
    }

    private boolean checkValidationLayersSupport() {
        int[] propertyCount = new int[1];
        VK13.vkEnumerateInstanceLayerProperties(propertyCount, null);
        VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(propertyCount[0]);
        VK13.vkEnumerateInstanceLayerProperties(propertyCount, availableLayers);
        Set<String> availableLayerNames = availableLayers.stream()
                .map(VkLayerProperties::layerNameString)
                .collect(toSet());
        availableLayers.free();
        return availableLayerNames.contains(validationLayerName);
    }

    public String getValidationLayerName() {
        return validationLayerName;
    }

    public VkInstance getVkInstance() {
        return vkInstance;
    }

    public VulkanLogger getVulkanLogger() {
        return vulkanLogger;
    }

    public long getSurface() {
        return surface;
    }
}
