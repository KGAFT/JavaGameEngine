package com.kgaft.KGAFTEngine.VulkanRenderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;

public class VulkanDevice {
    private VkPhysicalDevice deviceToCreate;
    private VulkanInstance vulkanInstance;
    private VkQueue graphicsQueue;
    private VkQueue presentQueue;

    private long commandPool;
    private VkDevice vkDevice;
    public VulkanDevice(VulkanInstance vulkanInstance){
        this.vulkanInstance = vulkanInstance;
    }
    public HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> enumerateSupportedDevices(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> result = new HashMap<>();
            int[] deviceCount = new int[1];
            VK13.vkEnumeratePhysicalDevices(vulkanInstance.getVkInstance(), deviceCount, null);
            PointerBuffer devicesPointers = stack.mallocPointer(deviceCount[0]);
            VK13.vkEnumeratePhysicalDevices(vulkanInstance.getVkInstance(), deviceCount, devicesPointers);
            while(devicesPointers.hasRemaining()){
                VkPhysicalDevice physicalDevice = new VkPhysicalDevice(devicesPointers.get(), vulkanInstance.getVkInstance());
                if(isDeviceSuitable(physicalDevice)){
                    VkPhysicalDeviceProperties features = VkPhysicalDeviceProperties.malloc();
                    VK13.vkGetPhysicalDeviceProperties(physicalDevice, features);
                    result.put(physicalDevice, features);
                }
            }
            return result;
        }
    }

    public void setDeviceToCreate(VkPhysicalDevice deviceToCreate) {
        this.deviceToCreate = deviceToCreate;
    }
    public boolean load(boolean logDevice){
        try(MemoryStack stack = MemoryStack.stackPush()){
            if(isDeviceSuitable(deviceToCreate)){
                QueueFamilyIndices queueFamilyIndices = findDeiceQueues(deviceToCreate);
                Set<Integer> uniqueQueues = new HashSet<>();
                uniqueQueues.add(queueFamilyIndices.getGraphicsFamily());
                uniqueQueues.add(queueFamilyIndices.getPresentFamily());
                VkDeviceQueueCreateInfo.Buffer queuesCreateInfo = VkDeviceQueueCreateInfo.callocStack(uniqueQueues.size(), stack);
                uniqueQueues.forEach(q->{
                    VkDeviceQueueCreateInfo queueCreateInfo = VkDeviceQueueCreateInfo.callocStack(stack);
                    queueCreateInfo.sType(VK13.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                    queueCreateInfo.queueFamilyIndex(q);
                    queueCreateInfo.pQueuePriorities(stack.floats(1));
                    queuesCreateInfo.put(queueCreateInfo);
                });
                queuesCreateInfo.rewind();
                VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);
                deviceFeatures.samplerAnisotropy(true);

                VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.callocStack(stack);
                createInfo.sType(VK13.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
                createInfo.pQueueCreateInfos(queuesCreateInfo);
                createInfo.pEnabledFeatures(deviceFeatures);
                PointerBuffer extensions = stack.mallocPointer(1);
                extensions.put(stack.UTF8Safe(VK_KHR_SWAPCHAIN_EXTENSION_NAME));
                extensions.rewind();
                createInfo.ppEnabledExtensionNames(extensions);
                if(logDevice){
                    PointerBuffer layers = stack.mallocPointer(1);
                    layers.put(stack.UTF8Safe("VK_LAYER_KHRONOS_validation"));
                    layers.rewind();
                    createInfo.ppEnabledLayerNames(layers);
                }
                PointerBuffer pDevice = stack.mallocPointer(1);
                if(VK13.vkCreateDevice(deviceToCreate, createInfo, null, pDevice)==VK13.VK_SUCCESS){
                    vkDevice = new VkDevice(pDevice.get(), deviceToCreate, createInfo);
                    pDevice.rewind();
                    VK13.vkGetDeviceQueue(vkDevice, queueFamilyIndices.getGraphicsFamily(), 0, pDevice);
                    graphicsQueue = new VkQueue(pDevice.get(), vkDevice);
                    pDevice.rewind();
                    VK13.vkGetDeviceQueue(vkDevice, queueFamilyIndices.getPresentFamily(), 0, pDevice);
                    presentQueue = new VkQueue(pDevice.get(), vkDevice);
                    return createCommandPool(stack);
                }
            }
            return false;
        }

    }
    private boolean createCommandPool(MemoryStack stack){
        QueueFamilyIndices indices = findDeiceQueues(deviceToCreate);
        VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.callocStack(stack);
        commandPoolCreateInfo.sType(VK13.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
        commandPoolCreateInfo.queueFamilyIndex(indices.getGraphicsFamily());
        commandPoolCreateInfo.flags(VK13.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK13.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
        long[] commandPoolResult = new long[1];
        if(VK13.vkCreateCommandPool(vkDevice, commandPoolCreateInfo, null, commandPoolResult)==VK13.VK_SUCCESS){
            commandPool = commandPoolResult[0];
            return true;
        }
        return false;
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device){
        QueueFamilyIndices queueFamilyIndices = findDeiceQueues(device);
        boolean extensionSupport = checkDeviceExtensionsSupport(device);
        boolean swapChainState = false;
        if(extensionSupport){
            SwapChainSupportDetails swapChainSupportDetails = getSwapChainSupportDetails(device);
            swapChainState = swapChainSupportDetails.getPresentModes().size()>0 && swapChainSupportDetails.getSurfaceFormats().size()>0;
        }
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.malloc();
        VK13.vkGetPhysicalDeviceFeatures(device, features);
        return queueFamilyIndices.getPresentFamily()>-1 && queueFamilyIndices.getGraphicsFamily()>-1 && extensionSupport && swapChainState && features.samplerAnisotropy();
    }
    private QueueFamilyIndices findDeiceQueues(VkPhysicalDevice physicalDevice){
        QueueFamilyIndices result = new QueueFamilyIndices();
        int[] queuesCount= new int[1];
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queuesCount, null);
        VkQueueFamilyProperties.Buffer properties = VkQueueFamilyProperties.malloc(queuesCount[0]);
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queuesCount, properties);
        int i = 0;
        while (properties.hasRemaining()){
            VkQueueFamilyProperties property = properties.get();
            if(property.queueCount()>0 && ( property.queueFlags() & VK13.VK_QUEUE_GRAPHICS_BIT)>0){
                result.setGraphicsFamily(i);
            }
            int[] presentSupport = new int[1];
            vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, vulkanInstance.getSurface(), presentSupport);
            if(property.queueCount()>0 && presentSupport[0]==1){
                result.setPresentFamily(i);
            }
            if(result.getGraphicsFamily()>-1 && result.getPresentFamily()>-1){
                break;
            }
            i++;
        }
        properties.free();
        return result;
    }
    private SwapChainSupportDetails getSwapChainSupportDetails(VkPhysicalDevice device){
        SwapChainSupportDetails result = new SwapChainSupportDetails();
        result.setCapabilitiesKHR(VkSurfaceCapabilitiesKHR.malloc());
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, vulkanInstance.getSurface(), result.getCapabilitiesKHR());

        int[] formatCount = new int[1];
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, vulkanInstance.getSurface(), formatCount, null);
        if(formatCount[0]>0){
            VkSurfaceFormatKHR.Buffer surfacesBuffer = VkSurfaceFormatKHR.malloc(formatCount[0]);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, vulkanInstance.getSurface(), formatCount, surfacesBuffer);
            while(surfacesBuffer.hasRemaining()){
                result.getSurfaceFormats().add(surfacesBuffer.get());
            }
            surfacesBuffer.free();
        }
        int[] presentModeCount = new int[1];
        vkGetPhysicalDeviceSurfacePresentModesKHR(device, vulkanInstance.getSurface(), presentModeCount, null);
        if(presentModeCount[0]>0){
            int[] presentModes = new int[presentModeCount[0]];
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, vulkanInstance.getSurface(), presentModeCount, presentModes);
            for (int presentMode : presentModes) {
                result.getPresentModes().add(presentMode);
            }
        }
        return result;
    }
    private boolean checkDeviceExtensionsSupport(VkPhysicalDevice device){
        int[] extensionsCount = new int[1];
        VK13.vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, extensionsCount, null);
        VkExtensionProperties.Buffer propsBuffer = VkExtensionProperties.malloc(extensionsCount[0]);
        VK13.vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, extensionsCount, propsBuffer);
        while (propsBuffer.hasRemaining()){
            if(propsBuffer.get().extensionNameString().equals(VK_KHR_SWAPCHAIN_EXTENSION_NAME)){
                return true;
            }
        }
        propsBuffer.free();
        return false;
    }
}
