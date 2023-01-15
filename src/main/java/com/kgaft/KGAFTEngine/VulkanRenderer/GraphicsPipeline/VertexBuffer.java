
package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;


import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;


import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class VertexBuffer {
    public static HashMap<VkVertexInputBindingDescription.Buffer, VkVertexInputAttributeDescription.Buffer> getDescription() {
        HashMap<VkVertexInputBindingDescription.Buffer, VkVertexInputAttributeDescription.Buffer> result = new HashMap<>();
        VkVertexInputBindingDescription.Buffer bindDesc = VkVertexInputBindingDescription.malloc(1);
        bindDesc.clear();
        bindDesc.binding(0);
        bindDesc.stride(Float.BYTES * 8);
        bindDesc.inputRate(VK13.VK_VERTEX_INPUT_RATE_VERTEX);

        VkVertexInputAttributeDescription.Buffer attribDesc = VkVertexInputAttributeDescription.malloc(3);
        attribDesc.clear();
        attribDesc.binding(0);
        attribDesc.location(0);
        attribDesc.format(VK_FORMAT_R32G32B32_SFLOAT);
        attribDesc.offset(0);

        attribDesc.get();
        attribDesc.binding(0);
        attribDesc.location(1);
        attribDesc.format(VK_FORMAT_R32G32B32_SFLOAT);
        attribDesc.offset(3 * Float.BYTES);

        attribDesc.get();
        attribDesc.binding(0);
        attribDesc.location(2);
        attribDesc.format(VK_FORMAT_R32G32_SFLOAT);
        attribDesc.offset(6 * Float.BYTES);
        attribDesc.rewind();

        result.put(bindDesc, attribDesc);
        return result;
    }

    private long vertexBuffer = 0;
    private int vertexCount = 0;

    public VertexBuffer(VulkanDevice device, List<ShaderMeshInputStruct> inputData) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            List<Float> insertData = new ArrayList<>();

            vertexCount = inputData.size();

            inputData.forEach(data -> {
                insertData.add(data.position.x);
                insertData.add(data.position.y);
                insertData.add(data.position.z);

                insertData.add(data.normal.x);
                insertData.add(data.normal.y);
                insertData.add(data.normal.z);

                insertData.add(data.uv.x);
                insertData.add(data.uv.y);
            });
            int bufferSize = insertData.size() * Float.SIZE;

            LongBuffer stagingBuffer = stack.mallocLong(1);
            LongBuffer stagingBufferMemory = stack.mallocLong(1);

            device.createBuffer(bufferSize,
                    VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    stagingBuffer,
                    stagingBufferMemory);
            long stagingBufferMemoryVal = stagingBufferMemory.get();
            long stagingBufferVal = stagingBuffer.get();
            device.storeFloatDataInBuffer(insertData, stagingBufferMemoryVal, bufferSize);

            LongBuffer vertexBufferBuf = stack.mallocLong(1);
            LongBuffer vertexBufferMemoryBuf = stack.mallocLong(1);
            device.createBuffer(
                    bufferSize,
                    VK_BUFFER_USAGE_VERTEX_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                    vertexBufferBuf,
                    vertexBufferMemoryBuf);
            this.vertexBuffer = vertexBufferBuf.get();
            device.copyBuffer(stagingBufferVal, vertexBuffer, bufferSize);
            vkDestroyBuffer(device.getVkDevice(), stagingBufferVal, null);
            vkFreeMemory(device.getVkDevice(), stagingBufferMemoryVal, null);
        }


    }

    public void loadDataToCommandBuffer(VkCommandBuffer commandBuffer) {
        long[] buffers = new long[]{vertexBuffer};
        long[] offsets = new long[]{0};
        vkCmdBindVertexBuffers(commandBuffer, 0, buffers, offsets);
    }

    public void draw(VkCommandBuffer commandBuffer) {
        vkCmdDraw(commandBuffer, vertexCount, 1, 0, 0);
    }

}