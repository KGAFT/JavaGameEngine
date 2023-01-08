package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;


import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

public class VertexBuffer {
    public static HashMap<VkVertexInputBindingDescription.Buffer, VkVertexInputAttributeDescription.Buffer> getDescription(){
        HashMap<VkVertexInputBindingDescription.Buffer, VkVertexInputAttributeDescription.Buffer> result = new HashMap<>();
        VkVertexInputBindingDescription.Buffer bindDesc = VkVertexInputBindingDescription.malloc(1);
        bindDesc.clear();
        bindDesc.binding(0);
        bindDesc.stride(Float.BYTES*8);
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
        attribDesc.offset(3*Float.BYTES);

        attribDesc.get();
        attribDesc.binding(0);
        attribDesc.location(2);
        attribDesc.format(VK_FORMAT_R32G32_SFLOAT);
        attribDesc.offset(6*Float.BYTES);
        attribDesc.rewind();

        result.put(bindDesc, attribDesc);
        return result;
    }

    private long vertexBuffer = 0;
    private int vertexCount = 0;
    public VertexBuffer(VkDevice device, List<ShaderMeshInputStruct> inputData) {
        try(MemoryStack stack = MemoryStack.stackPush()){
            List<Float> insertData = new ArrayList<>();

            vertexCount = inputData.size();

            inputData.forEach(data->{
                insertData.add(data.position.x);
                insertData.add(data.position.y);
                insertData.add(data.position.z);

                insertData.add(data.normal.x);
                insertData.add(data.normal.y);
                insertData.add(data.normal.z);

                insertData.add(data.uv.x);
                insertData.add(data.uv.y);
            });
            int bufferSize = insertData.size()*Float.SIZE;

            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.callocStack(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(bufferSize);
            bufferInfo.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
            bufferInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pVertexBuffer = stack.mallocLong(1);

            if(vkCreateBuffer(device, bufferInfo, null, pVertexBuffer) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create vertex buffer");
            }
            vertexBuffer = pVertexBuffer.get(0);

            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
            vkGetBufferMemoryRequirements(device, vertexBuffer, memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(findMemoryType(device.getPhysicalDevice(), memRequirements.memoryTypeBits(),
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));

            LongBuffer pVertexBufferMemory = stack.mallocLong(1);

            if(vkAllocateMemory(device, allocInfo, null, pVertexBufferMemory) != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate vertex buffer memory");
            }
            long vertexBufferMemory = pVertexBufferMemory.get(0);

            vkBindBufferMemory(device, vertexBuffer, vertexBufferMemory, 0);


            PointerBuffer data = stack.mallocPointer(1);
            vkBindBufferMemory(device, vertexBuffer, vertexBufferMemory, 0);

            vkMapMemory(device, vertexBufferMemory, 0, bufferInfo.size(), 0, data);

            ByteBuffer bb = data.getByteBuffer(0, bufferSize);

            insertData.forEach(iData->{
                bb.putFloat(iData);
            });

            vkUnmapMemory(device, vertexBufferMemory);
        }


    }

    public void loadDataToCommandBuffer(VkCommandBuffer commandBuffer){
        long[] buffers = new long[]{vertexBuffer};
        long[] offsets = new long[]{0};
        vkCmdBindVertexBuffers(commandBuffer, 0, buffers, offsets);
    }

    public void draw(VkCommandBuffer commandBuffer){
        vkCmdDraw(commandBuffer, vertexCount, 1, 0, 0);
    }

    private int findMemoryType(VkPhysicalDevice device, int typeFilter, int properties) {

        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.mallocStack();
        vkGetPhysicalDeviceMemoryProperties(device, memProperties);

        for(int i = 0;i < memProperties.memoryTypeCount();i++) {
            if((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                return i;
            }
        }

        throw new RuntimeException("Failed to find suitable memory type");
    }

}
