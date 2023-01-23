package com.kgaft.VulkanRenderer.VulkanContext.GraphicsPipeline;

import com.kgaft.VulkanRenderer.VulkanContext.VulkanDevice;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class IndexBuffer {
    private long indexBuffer;
    private long indexBufferMemory;

    private int indicesCount;

    public IndexBuffer(List<Integer> indices, VulkanDevice device){

        try(MemoryStack stack = stackPush()) {

            int bufferSize = Integer.SIZE*indices.size();
            indicesCount = indices.size();

            LongBuffer stagingBuffer = stack.mallocLong(1);
            LongBuffer stagingBufferMemory = stack.mallocLong(1);

            device.createBuffer(bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    stagingBuffer,
                    stagingBufferMemory);
            long stagingBufferMemoryVal = stagingBufferMemory.get();
            long stagingBufferVal = stagingBuffer.get();
            device.storeIntDataInBuffer(indices, stagingBufferMemoryVal, bufferSize);


            LongBuffer buffer = stack.mallocLong(1);
            LongBuffer bufferMemory = stack.mallocLong(1);

            device.createBuffer(bufferSize, VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, buffer, bufferMemory);

            this.indexBuffer = buffer.get();
            this.indexBufferMemory = bufferMemory.get();
            device.copyBuffer(stagingBufferVal, indexBuffer, bufferSize);

            vkDestroyBuffer(device.getVkDevice(), stagingBufferVal, null);
            vkFreeMemory(device.getVkDevice(), stagingBufferMemoryVal, null);
        }
    }

    public void bind(VkCommandBuffer commandBuffer){
        vkCmdBindIndexBuffer(commandBuffer, indexBuffer, 0, VK_INDEX_TYPE_UINT32);
    }
    public void draw(VkCommandBuffer commandBuffer){
        bind(commandBuffer);
        vkCmdDrawIndexed(commandBuffer, indicesCount, 1, 0, 0, 0);
    }

}
