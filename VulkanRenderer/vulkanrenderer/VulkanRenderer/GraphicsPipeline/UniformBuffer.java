package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;


import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class UniformBuffer {
    private List<Long> buffers;
    private List<Long> buffersMemories;
    private int binding;
    private VulkanDevice device;
    private int size;
    private DescriptorSet descriptorSet;
    public UniformBuffer(VulkanDevice device, int bufferCount, int size, int binding) {
        buffers = new ArrayList<>(bufferCount);
        buffersMemories = new ArrayList<>(bufferCount);
        this.binding = binding;
        this.size = size;
        try(MemoryStack stack = stackPush()) {
            LongBuffer pBuffer = stack.mallocLong(1);
            LongBuffer pBufferMemory = stack.mallocLong(1);

            for(int i = 0;i < bufferCount;i++) {
                device.createBuffer(size,
                        VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                        pBuffer,
                        pBufferMemory);

               buffers.add(pBuffer.get());
               buffersMemories.add(pBufferMemory.get());
               pBuffer.rewind();
               pBufferMemory.rewind();
            }

        }
        this.device = device;
    }
    public void writeToBuffer(int index, int size, float toWrite){
        try(MemoryStack stack = stackPush()) {

            PointerBuffer data = stack.mallocPointer(1);
            vkMapMemory(device.getVkDevice(), buffersMemories.get(index), 0, size, 0, data);
            {
                data.getByteBuffer(size).putFloat(toWrite);
            }
            vkUnmapMemory(device.getVkDevice(), buffersMemories.get(index));
        }
    }
    
    protected void setDescriptorSet(DescriptorSet descriptorSet) {
        this.descriptorSet = descriptorSet;
    }
    public List<Long> getBuffers() {
        return buffers;
    }
    public int getSize(){
        return size;
    }
    public int getBinding() {
        return binding;
    }
    public void attach(VkCommandBuffer commandBuffer, int index, long pipelineLayout){
        descriptorSet.bind(commandBuffer, index, pipelineLayout);
    }
    
}
