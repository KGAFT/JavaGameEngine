package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;


import com.kgaft.KGAFTEngine.VulkanRenderer.ShaderUtil;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanSwapChain;
import com.kgaft.KGAFTEngine.VulkanRenderer.ShaderUtil.ShaderType;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class GraphicsPipeline {
    private VulkanDevice vulkanDevice;
    private VulkanSwapChain swapChain;
    private long pipeline;

    
    private List<VkCommandBuffer> commandBuffers = new ArrayList<>();
    private long pipelineLayout;

   

    public GraphicsPipeline(VulkanDevice vulkanDevice, VulkanSwapChain swapChain) {
        this.vulkanDevice = vulkanDevice;
        this.swapChain = swapChain;
    }

    public void load(PipelineConfigStruct pipelineConfigStruct) throws URISyntaxException {
        try (MemoryStack stack = stackPush()) {
            this.pipelineLayout = pipelineConfigStruct.pipelineLayout;
            ByteBuffer bb = ShaderUtil.compileShaderResource("SPIR-V/default.vert", ShaderType.VERTEX_SHADER);

            long vertexShader = createShader(vulkanDevice.getVkDevice(), bb);
            bb.clear();
            bb = ShaderUtil.compileShaderResource("SPIR-V/default.frag", ShaderType.FRAGMENT_SHADER);
            long fragmentShader = createShader(vulkanDevice.getVkDevice(), bb);
            
            VkPipelineShaderStageCreateInfo.Buffer shaderStageCreateInfo = VkPipelineShaderStageCreateInfo
                    .callocStack(2, stack);
            VkPipelineShaderStageCreateInfo vertexShaderCreate = VkPipelineShaderStageCreateInfo.callocStack(stack);
            vertexShaderCreate.clear();
            vertexShaderCreate.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertexShaderCreate.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertexShaderCreate.pName(Objects.requireNonNull(stack.UTF8Safe("main")));
            vertexShaderCreate.flags(0);
            vertexShaderCreate.module(vertexShader);
            vertexShaderCreate.pSpecializationInfo(null);

            VkPipelineShaderStageCreateInfo fragmentShaderCreate = VkPipelineShaderStageCreateInfo.callocStack(stack);
            fragmentShaderCreate.clear();
            fragmentShaderCreate.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            fragmentShaderCreate.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            fragmentShaderCreate.pName(Objects.requireNonNull(stack.UTF8Safe("main")));
            fragmentShaderCreate.flags(0);
            fragmentShaderCreate.module(fragmentShader);
            fragmentShaderCreate.pSpecializationInfo(null);

            shaderStageCreateInfo.put(vertexShaderCreate);
            shaderStageCreateInfo.put(fragmentShaderCreate);
            shaderStageCreateInfo.rewind();

            VkPipelineVertexInputStateCreateInfo vertexInput = VkPipelineVertexInputStateCreateInfo.callocStack(stack);
            vertexInput.clear();
            vertexInput.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);
            HashMap<VkVertexInputBindingDescription.Buffer, VkVertexInputAttributeDescription.Buffer> input = VertexBuffer
                    .getDescription();
            vertexInput.pVertexAttributeDescriptions(
                    (VkVertexInputAttributeDescription.Buffer) input.values().toArray()[0]);
            vertexInput
                    .pVertexBindingDescriptions((VkVertexInputBindingDescription.Buffer) input.keySet().toArray()[0]);

            VkPipelineViewportStateCreateInfo viewPortCreateInfo = VkPipelineViewportStateCreateInfo.callocStack(stack);
            viewPortCreateInfo.clear();
            viewPortCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
            viewPortCreateInfo.pViewports(pipelineConfigStruct.viewport);
            viewPortCreateInfo.pScissors(pipelineConfigStruct.scissor);

            VkGraphicsPipelineCreateInfo.Buffer pipelineCreateInfo = VkGraphicsPipelineCreateInfo.callocStack(1, stack);
            pipelineCreateInfo.clear();
            pipelineCreateInfo.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO);
            pipelineCreateInfo.pStages(shaderStageCreateInfo);
            pipelineCreateInfo.pVertexInputState(vertexInput);
            pipelineCreateInfo.pInputAssemblyState(pipelineConfigStruct.inputAssemblyInfo);
            pipelineCreateInfo.pViewportState(viewPortCreateInfo);
            pipelineCreateInfo.pRasterizationState(pipelineConfigStruct.rasterizationInfo);
            pipelineCreateInfo.pMultisampleState(pipelineConfigStruct.multisampleInfo);
            pipelineCreateInfo.pColorBlendState(pipelineConfigStruct.colorBlendInfo);
            pipelineCreateInfo.pDepthStencilState(pipelineConfigStruct.depthStencilInfo);
            pipelineCreateInfo.pDynamicState(null);
            pipelineCreateInfo.layout(pipelineConfigStruct.pipelineLayout);
            pipelineCreateInfo.renderPass(pipelineConfigStruct.renderPass);
            pipelineCreateInfo.subpass(pipelineConfigStruct.subpass);

            pipelineCreateInfo.basePipelineIndex(-1);
            pipelineCreateInfo.basePipelineHandle(VK_NULL_HANDLE);

            long[] result = new long[1];
            if (VK13.vkCreateGraphicsPipelines(vulkanDevice.getVkDevice(), VK_NULL_HANDLE, pipelineCreateInfo, null,
                    result) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create pipeline");
            }
            pipeline = result[0];

        }

    }

    public void createCommandBuffers() {
        VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.malloc();
        allocateInfo.clear();
        allocateInfo.sType$Default();
        allocateInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
        allocateInfo.commandPool(vulkanDevice.getCommandPool());
        allocateInfo.commandBufferCount(swapChain.getImageCount());
        PointerBuffer pointerBuffer = stackPush().mallocPointer(swapChain.getImageCount());
        if (vkAllocateCommandBuffers(vulkanDevice.getVkDevice(), allocateInfo, pointerBuffer) != VK_SUCCESS) {
            throw new RuntimeException("Failed to allocate buffer");
        }
        while (pointerBuffer.hasRemaining()) {
            commandBuffers.add(new VkCommandBuffer(pointerBuffer.get(), vulkanDevice.getVkDevice()));
        }
        allocateInfo.free();

    }
    public void renderCommandBuffers(){
       
        
        
    }
    public long getPipeline(){
        return pipeline;
    }
    public List<VkCommandBuffer> getCommandBuffers(){
        return commandBuffers;
    }
    public void bind(VkCommandBuffer commandBuffer) {
        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
    }

    public void update() {
        int imageIndex = swapChain.nextImage();
        swapChain.submitCommandBuffer(commandBuffers.get(imageIndex), imageIndex);

    }

    private long createShader(VkDevice device, ByteBuffer binary) {
        VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.malloc();
        moduleCreateInfo.clear();
        moduleCreateInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
        moduleCreateInfo.pCode(binary);
        long[] result = new long[1];
        if (VK13.vkCreateShaderModule(device, moduleCreateInfo, null, result) == VK_SUCCESS) {
            moduleCreateInfo.free();
            return result[0];
        }
        moduleCreateInfo.free();
        return -1;
    }
}
