package com.kgaft.KGAFTEngine.VulkanRenderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;
import java.util.Date;

import static org.lwjgl.vulkan.EXTDebugUtils.*;

public class VulkanLogger{

    private long debugMessenger;
    public VkDebugUtilsMessengerCreateInfoEXT getRequiredInfo(MemoryStack stack){
        VkDebugUtilsMessengerCreateInfoEXT result = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);
        result.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        result.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        result.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        result.pfnUserCallback(this::invoke);
        return result;
    }
    public void load(VkInstance instance, MemoryStack stack){
        VkDebugUtilsMessengerCreateInfoEXT info = getRequiredInfo(stack);
        LongBuffer longBuffer = stack.mallocLong(1);
        createDebugUtilsMessenger(instance, info, longBuffer);
        this.debugMessenger = longBuffer.get();
    }
    private boolean createDebugUtilsMessenger(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT createInfoEXT, LongBuffer longBuffer){
        long functionHandle = VK13.vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT");
        if(functionHandle!=VK13.VK_NULL_HANDLE){
            return vkCreateDebugUtilsMessengerEXT(instance, createInfoEXT, null, longBuffer)==VK13.VK_SUCCESS;
        }
        return false;
    }


    public int invoke(int messageSeverity, int messageType, long pCallbackData, long pUserDat) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        String severity = "";
        switch(messageSeverity){
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT:
                severity = "VERBOSE";
                break;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT:
                severity = "WARNING";
                break;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT:
                severity = "ERROR";
                break;
        }
        String type = "";
        switch (messageType){
            case VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT:
                type = "GENERAL";
                break;
            case VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT:
                type = "PERFORMANCE";
                break;
            case VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT:
                type = "VALIDATION";
                break;
        }
        String resultMessage = new Date().toString()+" "+type+" ["+severity+"]: "+ callbackData.pMessageString();
        if(messageSeverity==VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT){
            System.err.println(resultMessage);
        }
        else{
            System.out.println(resultMessage);
        }
        return 0;
    }
}
