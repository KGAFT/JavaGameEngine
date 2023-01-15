package com.kgaft.KGAFTEngine.VulkanRenderer;

import java.net.URISyntaxException;
import java.util.HashMap;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkClearDepthStencilValue;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkOffset2D;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.GraphicsPipeline;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfigStruct;
import com.kgaft.KGAFTEngine.Window.Window;
import com.kgaft.KGAFTEngine.Window.WindowResizeCallBack;

public class VulkanRenderingContext implements WindowResizeCallBack {
    private GraphicsPipeline graphicsPipeline;
    private VulkanSwapChain swapChain;
    private VulkanDevice device;
    private VulkanInstance vulkanInstance;
    private VulkanLogger vulkanLogger;
    private Window window;
    private boolean vSync;

    public VulkanRenderingContext(boolean needToLogVulkan) {
        if (needToLogVulkan) {
            vulkanLogger = new VulkanLogger();
        }
        VulkanInstance.createInstance(vulkanLogger);
        vulkanInstance = VulkanInstance.getVulkanInstance();
        device = new VulkanDevice(vulkanInstance);
       
    }

    public void initialize(boolean vSync, VkPhysicalDevice physicalDevice, Window window) {
        this.window = window;
        this.vSync = vSync;
        Configuration.STACK_SIZE.set(1024*8196);
        device.setDeviceToCreate(physicalDevice);
        device.load(vulkanLogger != null);
        swapChain = new VulkanSwapChain(device, window);
        swapChain.load(vSync);
        window.addResizeCallBack(this);
        graphicsPipeline = new GraphicsPipeline(device, swapChain);
        try {
            graphicsPipeline.load(PipelineConfigStruct.defaultConfig(window, device, swapChain, 6));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load graphics pipeline");
        }
        graphicsPipeline.createCommandBuffers();
    }

    public HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> enumerateSupportedGPUS() {
        return device.enumerateSupportedDevices();
    }

    @Override
    public void resized(int newWidth, int newHeight) {
        update();
        VK13.vkDeviceWaitIdle(device.getVkDevice());
        graphicsPipeline.destroy();
        swapChain.destroy();
        swapChain = new VulkanSwapChain(device, window);
        swapChain.load(vSync);
        
        graphicsPipeline = new GraphicsPipeline(device, swapChain);
        try {
            graphicsPipeline.load(PipelineConfigStruct.defaultConfig(window, device, swapChain, 6));
        } catch (URISyntaxException e) {
            
            throw new RuntimeException("Failed to recreate graphics pipeline");
        }
        graphicsPipeline.createCommandBuffers();
        updateDrawData();
    }

    public void updateDrawData() {
        VK13.vkDeviceWaitIdle(device.getVkDevice());
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.malloc();
        beginInfo.clear();
        beginInfo.sType(VK13.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

        VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.malloc();
        renderPassInfo.clear();
        renderPassInfo.sType(VK13.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);
        renderPassInfo.renderPass(swapChain.getRenderPass());

        VkRect2D renderArea = prepareRenderArea();
        VkClearValue.Buffer clearValues = prepareClearValues();
        renderPassInfo.clearValueCount(2);
        renderPassInfo.pClearValues(clearValues);
        for (int i = 0; i < graphicsPipeline.getCommandBuffers().size(); i++) {
            renderPassInfo.framebuffer(swapChain.getFrameBuffer(i));
            
            VkCommandBuffer commandBuffer = graphicsPipeline.getCommandBuffers().get(i);
            loadCommandBuffer(commandBuffer, beginInfo, renderPassInfo);
            // uniformBuffer.writeToBuffer(i, Float.SIZE, 2.0f);
            // descriptorSet.bind(commandBuffer, i, pipelineLayout);

            // pushConstant.loadData(commandBuffer);
            // vertexBuffer.loadDataToCommandBuffer(commandBuffer);

            // indexBuffer.draw(commandBuffer);
            unloadCommandBuffer(commandBuffer);
        }
        clearValues.free();
        renderArea.free();
        renderPassInfo.free();
        beginInfo.free();
    }

    public void update() {
        int imageIndex = swapChain.nextImage();
        swapChain.submitCommandBuffer(graphicsPipeline.getCommandBuffers().get(imageIndex), imageIndex);
        MemoryStack.stackPop();
    }

    public VkClearValue.Buffer prepareClearValues() {
        VkClearValue.Buffer clearValues = VkClearValue.malloc(2);
        clearValues.clear();
        clearValues.color().float32(MemoryStack.stackPush().floats(0.0f, 0.0f, 0.0f, 1.0f));
        clearValues.get();
        VkClearDepthStencilValue clearDepthStencilValue = VkClearDepthStencilValue.malloc();
        clearDepthStencilValue.clear();
        clearDepthStencilValue.depth(1);
        clearDepthStencilValue.stencil(0);
        clearValues.depthStencil(clearDepthStencilValue);
        clearValues.rewind();
        return clearValues;
    }

    public VkRect2D prepareRenderArea() {
        VkRect2D renderArea = VkRect2D.malloc();
        renderArea.clear();
        renderArea.offset(VkOffset2D.malloc().set(0, 0));
        renderArea.extent(swapChain.getSwapChainExtent());
        return renderArea;
    }

    public void loadCommandBuffer(VkCommandBuffer commandBuffer, VkCommandBufferBeginInfo beginInfo,
            VkRenderPassBeginInfo renderPassInfo) {
        if (VK13.vkBeginCommandBuffer(commandBuffer, beginInfo) != VK13.VK_SUCCESS) {
            throw new RuntimeException("Failed to begin recording command buffer");
        }
        VK13.vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK13.VK_SUBPASS_CONTENTS_INLINE);
        VK13.vkCmdBindPipeline(commandBuffer, VK13.VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline.getPipeline());
    }

    public void unloadCommandBuffer(VkCommandBuffer commandBuffer) {
        VK13.vkCmdEndRenderPass(commandBuffer);
        if (VK13.vkEndCommandBuffer(commandBuffer) != VK13.VK_SUCCESS) {
            throw new RuntimeException("Failed to record command buffer");
        }
    }

}
