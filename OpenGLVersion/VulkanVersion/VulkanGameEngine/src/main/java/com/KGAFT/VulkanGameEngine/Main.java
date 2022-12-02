package com.KGAFT.VulkanGameEngine;

import com.KGAFT.VulkanGameEngine.VKRenderer.VulkanRenderer;
import com.KGAFT.VulkanGameEngine.Window.Window;

public class Main {
    public static void main(String[] args) {
        Window.prepareWindow(800, 600, "hell");
        new VulkanRenderer();
    }
}