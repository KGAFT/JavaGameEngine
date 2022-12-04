package com.KGAFT.VulkanGameEngine.Utils;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_ERROR_EXTENSION_NOT_PRESENT;

public class Logger extends VkDebugUtilsMessengerCallbackEXT {
    private List<ReportFromVulkan> reportsToDisplay = new ArrayList<>();
    private VkInstance vkInstance;
    private long debugMessenger;


    public void start(VkInstance instance) {
        this.vkInstance = instance;
        try (MemoryStack stack = stackPush()) {

            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);

            getDebugMessengerInfo(createInfo);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

            if (createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                throw new RuntimeException("Failed to set up debug messenger");
            }

            debugMessenger = pDebugMessenger.get(0);
        }
    }

    public void getDebugMessengerInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        debugCreateInfo.pfnUserCallback(this);
    }

    private static int createDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT createInfo,
                                                    VkAllocationCallbacks allocationCallbacks, LongBuffer pDebugMessenger) {

        if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != 0) {
            return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        }

        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }
    public void shutDown() {
        if (vkGetInstanceProcAddr(vkInstance, "vkDestroyDebugUtilsMessengerEXT") != 0) {
            vkDestroyDebugUtilsMessengerEXT(vkInstance, debugMessenger, null);
        }
    }

    private String translateSeverity(int messageSeverity) {
        String severity;
        if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT) != 0) {
            severity = "VERBOSE";
        } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
            severity = "INFO";
        } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
            severity = "WARNING";
        } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
            severity = "ERROR";
        } else {
            severity = "UNKNOWN";
        }
        return severity;
    }

    private String translateType(int messageType) {
        String type;
        if ((messageType & VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT) != 0) {
            type = "GENERAL";
        } else if ((messageType & VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT) != 0) {
            type = "VALIDATION";
        } else if ((messageType & VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT) != 0) {
            type = "PERFORMANCE";
        } else {
            type = "UNKNOWN";
        }
        return type;
    }

    @Override
    public int invoke(int messageType, int messageSeverity, long callBackData, long userData) {
        Date date = new Date(System.currentTimeMillis());
        VkDebugUtilsMessengerCallbackDataEXT data = VkDebugUtilsMessengerCallbackDataEXT.createSafe(callBackData);
        String message = date + " [" + translateSeverity(messageSeverity) + "] " + translateType(messageType) + " ";
        message += data.pMessageString();
        if (messageSeverity== VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) {
            System.err.println(message);
        } else {
            System.out.println(message);
        }
        return 0;
    }
}
