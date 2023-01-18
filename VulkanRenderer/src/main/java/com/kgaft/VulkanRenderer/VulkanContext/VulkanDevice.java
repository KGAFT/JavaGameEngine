package com.kgaft.VulkanRenderer.VulkanContext;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanDevice {
    private VkPhysicalDevice deviceToCreate;
    private VulkanInstance vulkanInstance;
    private VkQueue graphicsQueue;
    private VkQueue presentQueue;
    private long commandPool;
    private VkDevice vkDevice;

    public VulkanDevice(VulkanInstance vulkanInstance) {
        this.vulkanInstance = vulkanInstance;
    }

    public HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> enumerateSupportedDevices() {
        try (MemoryStack stack = stackPush()) {
            HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> result = new HashMap<>();
            int[] deviceCount = new int[1];
            VK13.vkEnumeratePhysicalDevices(vulkanInstance.getVkInstance(), deviceCount, null);
            PointerBuffer devicesPointers = stack.mallocPointer(deviceCount[0]);
            VK13.vkEnumeratePhysicalDevices(vulkanInstance.getVkInstance(), deviceCount, devicesPointers);
            while (devicesPointers.hasRemaining()) {
                VkPhysicalDevice physicalDevice = new VkPhysicalDevice(devicesPointers.get(),
                        vulkanInstance.getVkInstance());
                if (isDeviceSuitable(physicalDevice)) {
                    VkPhysicalDeviceProperties features = VkPhysicalDeviceProperties.malloc();
                    VK13.vkGetPhysicalDeviceProperties(physicalDevice, features);
                    result.put(physicalDevice, features);
                }
            }
            return result;
        }
    }

    public long createImageView(long image, int format) {

        try (MemoryStack stack = stackPush()) {

            VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.callocStack(stack);
            viewInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            viewInfo.image(image);
            viewInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
            viewInfo.format(format);
            viewInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
            viewInfo.subresourceRange().baseMipLevel(0);
            viewInfo.subresourceRange().levelCount(1);
            viewInfo.subresourceRange().baseArrayLayer(0);
            viewInfo.subresourceRange().layerCount(1);

            LongBuffer pImageView = stack.mallocLong(1);

            if (vkCreateImageView(vkDevice, viewInfo, null, pImageView) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create texture image view");
            }

            return pImageView.get(0);
        }
    }

    public void setDeviceToCreate(VkPhysicalDevice deviceToCreate) {
        this.deviceToCreate = deviceToCreate;
    }

    public boolean load(boolean logDevice) {
        try (MemoryStack stack = stackPush()) {
            if (isDeviceSuitable(deviceToCreate)) {
                QueueFamilyIndices queueFamilyIndices = findDeiceQueues(deviceToCreate);
                Set<Integer> uniqueQueues = new HashSet<>();
                uniqueQueues.add(queueFamilyIndices.getGraphicsFamily());
                uniqueQueues.add(queueFamilyIndices.getPresentFamily());
                VkDeviceQueueCreateInfo.Buffer queuesCreateInfo = VkDeviceQueueCreateInfo
                        .callocStack(uniqueQueues.size(), stack);
                uniqueQueues.forEach(q -> {
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
                if (logDevice) {
                    PointerBuffer layers = stack.mallocPointer(1);
                    layers.put(stack.UTF8Safe("VK_LAYER_KHRONOS_validation"));
                    layers.rewind();
                    createInfo.ppEnabledLayerNames(layers);
                }
                PointerBuffer pDevice = stack.mallocPointer(1);
                if (VK13.vkCreateDevice(deviceToCreate, createInfo, null, pDevice) == VK13.VK_SUCCESS) {
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

    private boolean createCommandPool(MemoryStack stack) {
        QueueFamilyIndices indices = findDeiceQueues(deviceToCreate);
        VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.callocStack(stack);
        commandPoolCreateInfo.sType(VK13.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
        commandPoolCreateInfo.queueFamilyIndex(indices.getGraphicsFamily());
        commandPoolCreateInfo.flags(
                VK13.VK_COMMAND_POOL_CREATE_TRANSIENT_BIT | VK13.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);
        long[] commandPoolResult = new long[1];
        if (VK13.vkCreateCommandPool(vkDevice, commandPoolCreateInfo, null, commandPoolResult) == VK13.VK_SUCCESS) {
            commandPool = commandPoolResult[0];
            return true;
        }
        return false;
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device) {
        QueueFamilyIndices queueFamilyIndices = findDeiceQueues(device);
        boolean extensionSupport = checkDeviceExtensionsSupport(device);
        boolean swapChainState = false;
        if (extensionSupport) {
            SwapChainSupportDetails swapChainSupportDetails = getSwapChainSupportDetails(device);
            swapChainState = swapChainSupportDetails.getPresentModes().size() > 0
                    && swapChainSupportDetails.getSurfaceFormats().size() > 0;
        }
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.malloc();
        VK13.vkGetPhysicalDeviceFeatures(device, features);
        return queueFamilyIndices.getPresentFamily() > -1 && queueFamilyIndices.getGraphicsFamily() > -1
                && extensionSupport && swapChainState && features.samplerAnisotropy();
    }

    public QueueFamilyIndices getDeviceQueueIndices() {
        return findDeiceQueues(deviceToCreate);
    }

    private QueueFamilyIndices findDeiceQueues(VkPhysicalDevice physicalDevice) {
        QueueFamilyIndices result = new QueueFamilyIndices();
        int[] queuesCount = new int[1];
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queuesCount, null);
        VkQueueFamilyProperties.Buffer properties = VkQueueFamilyProperties.malloc(queuesCount[0]);
        VK13.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, queuesCount, properties);
        int i = 0;
        while (properties.hasRemaining()) {
            VkQueueFamilyProperties property = properties.get();
            if (property.queueCount() > 0 && (property.queueFlags() & VK13.VK_QUEUE_GRAPHICS_BIT) > 0) {
                result.setGraphicsFamily(i);
            }
            int[] presentSupport = new int[1];
            vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, vulkanInstance.getSurface(), presentSupport);
            if (property.queueCount() > 0 && presentSupport[0] == 1) {
                result.setPresentFamily(i);
            }
            if (result.getGraphicsFamily() > -1 && result.getPresentFamily() > -1) {
                break;
            }
            i++;
        }
        properties.free();
        return result;
    }

    public SwapChainSupportDetails getSwapChainSupportDetails() {
        return getSwapChainSupportDetails(deviceToCreate);
    }

    private SwapChainSupportDetails getSwapChainSupportDetails(VkPhysicalDevice device) {
        SwapChainSupportDetails result = new SwapChainSupportDetails();
        result.setCapabilitiesKHR(VkSurfaceCapabilitiesKHR.malloc());
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, vulkanInstance.getSurface(), result.getCapabilitiesKHR());

        int[] formatCount = new int[1];
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, vulkanInstance.getSurface(), formatCount, null);
        if (formatCount[0] > 0) {
            VkSurfaceFormatKHR.Buffer surfacesBuffer = VkSurfaceFormatKHR.calloc(formatCount[0]);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, vulkanInstance.getSurface(), formatCount, surfacesBuffer);
            while (surfacesBuffer.hasRemaining()) {
                result.getSurfaceFormats().add(surfacesBuffer.get());
            }
        }
        int[] presentModeCount = new int[1];
        vkGetPhysicalDeviceSurfacePresentModesKHR(device, vulkanInstance.getSurface(), presentModeCount, null);
        if (presentModeCount[0] > 0) {
            int[] presentModes = new int[presentModeCount[0]];
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, vulkanInstance.getSurface(), presentModeCount,
                    presentModes);
            for (int presentMode : presentModes) {
                result.getPresentModes().add(presentMode);
            }
        }
        return result;
    }

    private boolean checkDeviceExtensionsSupport(VkPhysicalDevice device) {
        int[] extensionsCount = new int[1];
        VK13.vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, extensionsCount, null);
        VkExtensionProperties.Buffer propsBuffer = VkExtensionProperties.malloc(extensionsCount[0]);
        VK13.vkEnumerateDeviceExtensionProperties(device, (ByteBuffer) null, extensionsCount, propsBuffer);
        while (propsBuffer.hasRemaining()) {
            if (propsBuffer.get().extensionNameString().equals(VK_KHR_SWAPCHAIN_EXTENSION_NAME)) {
                return true;
            }
        }
        propsBuffer.free();
        return false;
    }

    public VkCommandBuffer beginSingleTimeCommands() {

        try (MemoryStack stack = stackPush()) {

            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandPool(commandPool);
            allocInfo.commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(vkDevice, allocInfo, pCommandBuffer);
            VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), vkDevice);

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.callocStack(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            vkBeginCommandBuffer(commandBuffer, beginInfo);

            return commandBuffer;
        }
    }

    public void endSingleTimeCommands(VkCommandBuffer commandBuffer) {

        try (MemoryStack stack = stackPush()) {

            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo.Buffer submitInfo = VkSubmitInfo.callocStack(1, stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(commandBuffer));

            vkQueueSubmit(graphicsQueue, submitInfo, VK_NULL_HANDLE);
            vkQueueWaitIdle(graphicsQueue);

            vkFreeCommandBuffers(vkDevice, commandPool, commandBuffer);
        }
    }

    public int findMemoryType(int typeFilter, int properties){
        VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.malloc();
        memoryProperties.clear();
        vkGetPhysicalDeviceMemoryProperties(deviceToCreate, memoryProperties);
        for (int i = 0; i < memoryProperties.memoryTypeCount(); i++) {
            if((typeFilter & (1 << i))>=1 &&
                    (memoryProperties.memoryTypes(i).propertyFlags() & properties) == properties){
                memoryProperties.free();
                return i;
            }
        }
        memoryProperties.free();
        return -1;
    }

    public VulkanInstance getVulkanInstance() {
        return vulkanInstance;
    }

    public void createImageWithInfo(VkImageCreateInfo createInfo, long[] imageOutput, int memProps,
            long[] deviceMemory) {
        if (VK13.vkCreateImage(vkDevice, createInfo, null, imageOutput) != VK13.VK_SUCCESS) {
            throw new RuntimeException("Failed to create resource");
        }
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.malloc();
        memoryRequirements.clear();
        VK13.vkGetImageMemoryRequirements(vkDevice, imageOutput[0], memoryRequirements);

        VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.malloc();
        allocInfo.clear();
        allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
        allocInfo.allocationSize(memoryRequirements.size());
        allocInfo.memoryTypeIndex(findMemoryType(memoryRequirements.memoryTypeBits(), memProps));

        if (VK13.vkAllocateMemory(vkDevice, allocInfo, null, deviceMemory) != VK_SUCCESS) {
            throw new RuntimeException("Failed to allocate memory");
        }
        if (VK13.vkBindImageMemory(vkDevice, imageOutput[0], deviceMemory[0], 0) != VK_SUCCESS) {
            throw new RuntimeException("Error: cannot bind image memory");
        }
        allocInfo.free();
        memoryRequirements.free();
    }

    public VkDevice getVkDevice() {
        return vkDevice;
    }

    public int findSupportedFormat(List<Integer> formats, int tiling, int features) {
        for (int format : formats) {
            VkFormatProperties formatProperties = VkFormatProperties.malloc();
            VK13.vkGetPhysicalDeviceFormatProperties(deviceToCreate, format, formatProperties);
            if (tiling == VK_IMAGE_TILING_LINEAR && (formatProperties.linearTilingFeatures() & features) == features) {
                return format;
            } else if (tiling == VK_IMAGE_TILING_OPTIMAL
                    && (formatProperties.optimalTilingFeatures() & features) == features) {
                return format;
            }
        }
        return -1;
    }

    public VkQueue getGraphicsQueue() {
        return graphicsQueue;
    }

    public VkQueue getPresentQueue() {
        return presentQueue;
    }

    public long getCommandPool() {
        return commandPool;
    }

    public void createBuffer(long size, int usage, int properties, LongBuffer pBuffer, LongBuffer pBufferMemory) {

        try (MemoryStack stack = stackPush()) {

            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.callocStack(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            bufferInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            if (vkCreateBuffer(vkDevice, bufferInfo, null, pBuffer) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create vertex buffer");
            }

            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
            vkGetBufferMemoryRequirements(vkDevice, pBuffer.get(0), memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(memRequirements.memoryTypeBits(), properties));

            if (vkAllocateMemory(vkDevice, allocInfo, null, pBufferMemory) != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate vertex buffer memory");
            }

            vkBindBufferMemory(vkDevice, pBuffer.get(0), pBufferMemory.get(0), 0);
        }
    }

    public void storeFloatDataInBuffer(List<Float> dataToStore, long bufferMemory, int bufferSize) {
        PointerBuffer data = stackPush().mallocPointer(1);
        vkMapMemory(vkDevice, bufferMemory, 0, bufferSize, 0, data);

        ByteBuffer bb = data.getByteBuffer(0, bufferSize);

        dataToStore.forEach(iData -> {
            bb.putFloat(iData);
        });

        vkUnmapMemory(vkDevice, bufferMemory);
    }

    public void storeIntDataInBuffer(List<Integer> toStore, long bufferMemory, int bufferSize) {
        PointerBuffer data = stackPush().mallocPointer(1);
        vkMapMemory(vkDevice, bufferMemory, 0, bufferSize, 0, data);

        ByteBuffer bb = data.getByteBuffer(0, bufferSize);

        toStore.forEach(iData -> {
            bb.putInt(iData);
        });

        vkUnmapMemory(vkDevice, bufferMemory);
    }

    public void copyBuffer(long srcBuffer, long dstBuffer, long size) {

        try (MemoryStack stack = stackPush()) {

            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandPool(commandPool);
            allocInfo.commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(vkDevice, allocInfo, pCommandBuffer);
            VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), vkDevice);

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.callocStack(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            vkBeginCommandBuffer(commandBuffer, beginInfo);
            {
                VkBufferCopy.Buffer copyRegion = VkBufferCopy.callocStack(1, stack);
                copyRegion.size(size);
                vkCmdCopyBuffer(commandBuffer, srcBuffer, dstBuffer, copyRegion);
            }
            vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(pCommandBuffer);

            if (vkQueueSubmit(graphicsQueue, submitInfo, VK_NULL_HANDLE) != VK_SUCCESS) {
                throw new RuntimeException("Failed to submit copy command buffer");
            }

            vkQueueWaitIdle(graphicsQueue);

            vkFreeCommandBuffers(vkDevice, commandPool, pCommandBuffer);
        }
    }
}
