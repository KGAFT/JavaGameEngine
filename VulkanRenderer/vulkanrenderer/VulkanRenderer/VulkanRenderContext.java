package com.kgaft.KGAFTEngine.VulkanRenderer;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
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

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.DescriptorPool;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.DescriptorSet;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.GraphicsPipeline;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.IndexBuffer;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PushConstant;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.ShaderMeshInputStruct;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.Texture;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.UniformBuffer;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.VertexBuffer;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfiguration.PipelineConfigStruct;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanLogger.VulkanLogger;
import com.kgaft.KGAFTEngine.Window.Window;


public class VulkanRenderContext {
    private GraphicsPipeline graphicsPipeline;
    private VulkanSwapChain swapChain;
    private VulkanDevice device;
    private VulkanInstance vulkanInstance;
    private VulkanLogger vulkanLogger;
    private Window window;
    private boolean vSync;
    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
    private PushConstant pushConstant;
    private DescriptorPool descriptorPool;
    private UniformBuffer uniformBuffer;
    private Texture texture;
    private PipelineConfigStruct pipelineConfigStruct;
    public VulkanRenderContext(boolean needToLogVulkan) {
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
        graphicsPipeline = new GraphicsPipeline(device, swapChain);
        this.pipelineConfigStruct = PipelineConfigStruct.defaultConfig(window, device, swapChain);
        try {
            graphicsPipeline.load(pipelineConfigStruct);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to load graphics pipeline");
        }
        graphicsPipeline.createCommandBuffers();
        prepareData();
    }
    public void update(){
        renderFrame();
        graphicsPipeline.update();
        
    }
    
    private void renderFrame(){
        swapChain.waitFences();
        VK13.vkDeviceWaitIdle(device.getVkDevice());
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.malloc();
        beginInfo.clear();
        beginInfo.sType(VK13.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
        VkRenderPassBeginInfo renderPassInfo = prepareRenderPass();
        for (int i = 0; i < graphicsPipeline.getCommandBuffers().size(); i++) {
            VkCommandBuffer commandBuffer = graphicsPipeline.getCommandBuffers().get(i);

            if (VK13.vkBeginCommandBuffer(commandBuffer, beginInfo) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to begin recording command buffer");
            }

            renderPassInfo.framebuffer(swapChain.getFrameBuffer(i));
            uniformBuffer.writeToBuffer(i, Float.SIZE, 2.0f);
            uniformBuffer.attach(commandBuffer, i, pipelineConfigStruct.pipelineLayout);
            texture.attach(commandBuffer, i, pipelineConfigStruct.pipelineLayout);
            
            VK13.vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK13.VK_SUBPASS_CONTENTS_INLINE);
            {
                VK13.vkCmdBindPipeline(commandBuffer, VK13.VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline.getPipeline());

                pushConstant.loadData(commandBuffer);
                vertexBuffer.loadDataToCommandBuffer(commandBuffer);

                indexBuffer.draw(commandBuffer);

            }
            VK13.vkCmdEndRenderPass(commandBuffer);

            if (VK13.vkEndCommandBuffer(commandBuffer) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to record command buffer");
            }
        }
        renderPassInfo.pClearValues().free();
        renderPassInfo.free();
        beginInfo.free();
    }

    public HashMap<VkPhysicalDevice, VkPhysicalDeviceProperties> enumerateSupportedGPUS() {
        return device.enumerateSupportedDevices();
    }

    private VkRenderPassBeginInfo prepareRenderPass(){
        VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.malloc();
        renderPassInfo.clear();
        renderPassInfo.sType(VK13.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);
        renderPassInfo.renderPass(swapChain.getRenderPass());
        renderPassInfo.renderArea(prepareRenderArea());
        renderPassInfo.pClearValues(prepareClearValues());
        return renderPassInfo;
    }
   
    private VkClearValue.Buffer prepareClearValues(){
        VkClearValue.Buffer clearValues = VkClearValue.malloc(2);
        clearValues.clear();
        clearValues.color().float32(MemoryStack.stackPush().floats(0.33f, 0.0f, 0.67f, 1.0f));
        clearValues.get();
        VkClearDepthStencilValue clearDepthStencilValue = VkClearDepthStencilValue.malloc();
        clearDepthStencilValue.clear();
        clearDepthStencilValue.depth(1);
        clearDepthStencilValue.stencil(0);
        clearValues.depthStencil(clearDepthStencilValue);
        clearValues.rewind();
        return clearValues;
    }
    private VkRect2D prepareRenderArea(){
        VkRect2D renderArea = VkRect2D.malloc();
        renderArea.clear();
        renderArea.offset(VkOffset2D.malloc().set(0, 0));
        renderArea.extent(swapChain.getSwapChainExtent());
        return renderArea;
    }
    private void prepareData(){
        List<ShaderMeshInputStruct> inputData = new ArrayList<>();
            inputData.add(
                    new ShaderMeshInputStruct(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0, 0, 0), new Vector2f(1.0f, 0.0f)));
            inputData.add(new ShaderMeshInputStruct(new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(0.5f, 0, 0),
                    new Vector2f(0.0f, 0.0f)));
            inputData.add(new ShaderMeshInputStruct(new Vector3f(0.5f, 0.5f, 0.0f), new Vector3f(0, 0.5f, 0),
                    new Vector2f(0.0f, 1.0f)));
            inputData.add(new ShaderMeshInputStruct(new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0,0,0), new Vector2f(1.0f, 1.0f)));
           vertexBuffer = new VertexBuffer(device, inputData);
            List<Integer> indices = new ArrayList<>();
            indices.add(0);
            indices.add(1);
            indices.add(2);
            indices.add(2);
            indices.add(3);
            indices.add(0);
            indexBuffer = new IndexBuffer(indices, device);
            descriptorPool = DescriptorPool.createInstance(device, 3, pipelineConfigStruct.descriptorSetLayout);

            pushConstant = new PushConstant(pipelineConfigStruct.pipelineLayout);
            uniformBuffer = new UniformBuffer(device, 3, Float.SIZE, 0);
            descriptorPool.registerUniformBuffer(uniformBuffer, 0);
                    
            List<Long> sizes = new ArrayList<>();
            sizes.add((long) Float.SIZE);
            sizes.add((long) Float.SIZE);
            sizes.add((long) Float.SIZE);
            texture = new Texture();
            texture.createTextureImage(VulkanRenderContext.class.getClassLoader()
                    .getResource("textures/baseWhiteColor.png").getPath().substring(1), device);
            descriptorPool.registerTexture(texture, 1, 3);
            
    }
}
