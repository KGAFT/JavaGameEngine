package com.kgaft.VulkanRenderer.VulkanContext.VulkanLogger;


import java.util.Date;

import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import static org.lwjgl.vulkan.EXTDebugUtils.*;

public class VulkanLogInfo {
    private int messageSeverity;
    private int messageType;
    private String message;
    private long date;
    public VulkanLogInfo(int messageSeverity, int messageType, long pCallbackData, long pUserDat){
        this.messageSeverity = messageSeverity;
        this.messageType = messageType;
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        this.message = callbackData.pMessageString();
        this.date = new Date().getTime();
    }
    public String getMessage(){
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
        String resultMessage = new Date(date).toString()+" "+type+" ["+severity+"]: "+ message;
        return resultMessage;
    }
    public boolean isError(){
        return messageSeverity == VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
    } 
    public boolean isWarning(){
        return messageSeverity == VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
    }
    public boolean ignore(){
        return message.contains("Semaphore must not be currently signaled");
    }
    public String getVulkanMessage(){
        return message;
    }
}
