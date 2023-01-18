package com.kgaft.VulkanRenderer.VulkanContext.GraphicsPipeline;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.ByteBuffer;

import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

public class PushConstant {
    private long pipelineLayout;
    private PushConstantData data = new PushConstantData(new Matrix4f().identity(), new Matrix4f().identity());

    public PushConstant(long pipelineLayout) {
        this.pipelineLayout = pipelineLayout;
    }

    public PushConstantData getData() {
        return data;
    }

    public void setData(PushConstantData data) {
        this.data = data;
    }

    public void loadData(VkCommandBuffer commandBuffer) {
        try(MemoryStack stack = MemoryStack.stackPush()){
            ByteBuffer buffer = stack.malloc(PushConstantData.getSize());
            data.storeDataIntoByteBuffer(buffer);
            buffer.rewind();
            VK13.vkCmdPushConstants(commandBuffer, pipelineLayout, VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT,
                    0, buffer);
        }

    }
}
