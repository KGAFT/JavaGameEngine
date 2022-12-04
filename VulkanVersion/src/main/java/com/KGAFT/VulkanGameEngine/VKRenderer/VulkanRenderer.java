package com.KGAFT.VulkanGameEngine.VKRenderer;

import com.KGAFT.VulkanGameEngine.Utils.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;


import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class VulkanRenderer {

    private VkInstance vkInstance;
    private Logger logger = new Logger();
    private static final Set<String> VALIDATION_LAYERS;


    static {
        VALIDATION_LAYERS = new HashSet<>();
        VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
    }
    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {

        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        System.err.println("Validation layer: " + callbackData.pMessageString());

        return VK_FALSE;
    }

    public void createInstance() {

        try (MemoryStack stack = stackPush()) {

            VkApplicationInfo appInfo = VkApplicationInfo.callocStack(stack);

            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe("Hello Triangle"));
            appInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.pEngineName(stack.UTF8Safe("No Engine"));
            appInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.apiVersion(VK_API_VERSION_1_3);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.callocStack(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);
            createInfo.ppEnabledExtensionNames(getRequiredExtensions());


            createInfo.ppEnabledLayerNames(getEnabledValidationLayers());
            VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);
            logger.getDebugMessengerInfo(debugCreateInfo);
            createInfo.pNext(debugCreateInfo.address());
            PointerBuffer instancePtr = stack.mallocPointer(1);

            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create instance");
            }

            vkInstance = new VkInstance(instancePtr.get(0), createInfo);
            logger.start(vkInstance);
        }
    }

    public List<PhysicalDevice> getAvailableToRenderPhysicalDevices(){
        try(MemoryStack stack = stackPush()){
            List<PhysicalDevice> results = new ArrayList<>();
            IntBuffer deviceCount = stack.ints(1);
            VK13.vkEnumeratePhysicalDevices(vkInstance, deviceCount, null);
            PointerBuffer pointerBuffer = PointerBuffer.allocateDirect(deviceCount.get());
            deviceCount.rewind();
            VK13.vkEnumeratePhysicalDevices(vkInstance, deviceCount, pointerBuffer);
            for(int c = 0; c<deviceCount.get(0); c++){
                VkPhysicalDevice device = new VkPhysicalDevice(pointerBuffer.get(), vkInstance);
                results.add(getDeviceInfo(device));

            }
            return results;
        }
    }
    public PhysicalDevice getDeviceInfo(VkPhysicalDevice device){
        int score = 0;
        VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.create();
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc();
        VK13.vkGetPhysicalDeviceProperties(device, properties);
        VK13.vkGetPhysicalDeviceFeatures(device, features);
        if(!features.geometryShader()){
            return new PhysicalDevice(properties, features, device, 0);
        }
        score+=properties.deviceType()==VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU?2:1;
        score+=properties.limits().maxImageDimension2D();
        int[] familiesAmount = new int[1];
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(device, familiesAmount, null);
        VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.create(familiesAmount[0]);
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(device, familiesAmount, queueFamilies);
        AtomicInteger suitableQueues = new AtomicInteger();
        for(int c = 0; c<familiesAmount[0]; c++){
            if((queueFamilies.get().queueFlags()& VK_QUEUE_GRAPHICS_BIT)!=0){
                suitableQueues.getAndIncrement();
            }
        }
        PhysicalDevice physicalDevice = new PhysicalDevice(properties, features, device, score);
        physicalDevice.setSupportedQueueFamiliesWithGraphics(suitableQueues.get());
        queueFamilies.free();
        return physicalDevice;
    }
    private PointerBuffer getEnabledValidationLayers() {

        MemoryStack stack = stackGet();

        PointerBuffer buffer = stack.mallocPointer(VALIDATION_LAYERS.size());

        VALIDATION_LAYERS.stream()
                .map(stack::UTF8)
                .forEach(buffer::put);

        return buffer.rewind();
    }

    private PointerBuffer getRequiredExtensions() {

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();


        MemoryStack stack = stackGet();

        PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);

        extensions.put(glfwExtensions);
        extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

        return extensions.rewind();


    }

    private boolean checkValidationLayerSupport() {

        try (MemoryStack stack = stackPush()) {

            IntBuffer layerCount = stack.ints(0);

            vkEnumerateInstanceLayerProperties(layerCount, null);

            VkLayerProperties.Buffer availableLayers = VkLayerProperties.mallocStack(layerCount.get(0), stack);

            vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

            Set<String> availableLayerNames = availableLayers.stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(toSet());

            return availableLayerNames.containsAll(VALIDATION_LAYERS);
        }
    }

}

