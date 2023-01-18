package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;

public class DescriptorPool {
    public static final int TEXTURE_BLOCK_AMOUNT = 50;
    public static final int UNIFORM_BUFFERS_AMOUNT = 3;

    private static DescriptorPool descriptorPoolInstance;

    public static DescriptorPool createInstance(VulkanDevice device, int imageCount, long descriptorSetLayout) {
        descriptorPoolInstance = new DescriptorPool(device, imageCount, descriptorSetLayout);
        return descriptorPoolInstance;
    }

    public static DescriptorPool getDescriptorPoolInstance() {
        return descriptorPoolInstance;
    }

    private VulkanDevice device;
    private long descriptorPool;
    private long descriptorSetLayout;
    private DescriptorPool(VulkanDevice device, int imageCount, long descriptorSetLayout) {
        createDescriptorPool(device, imageCount);
        this.descriptorSetLayout = descriptorSetLayout;
    }

    private void createDescriptorPool(VulkanDevice device, int imageCount) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.device = device;
            VkDescriptorPoolSize.Buffer poolSize = VkDescriptorPoolSize.callocStack(UNIFORM_BUFFERS_AMOUNT+ TEXTURE_BLOCK_AMOUNT, stack);
            for(int i = 0; i<UNIFORM_BUFFERS_AMOUNT; i++){
                poolSize.type(VK13.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
                poolSize.descriptorCount(imageCount * UNIFORM_BUFFERS_AMOUNT);
                poolSize.get();
            }
            
            for (int i = 0; i < TEXTURE_BLOCK_AMOUNT; i++) {
                Texture.getSizeForDescriptorPool(poolSize, imageCount);
                poolSize.get();
            }
            poolSize.rewind();
            VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.callocStack(stack);
            poolInfo.sType(VK13.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO);
            poolInfo.pPoolSizes(poolSize);
            poolInfo.maxSets(UNIFORM_BUFFERS_AMOUNT+TEXTURE_BLOCK_AMOUNT);

            LongBuffer pDescriptorPool = stack.mallocLong(1);

            if (VK13.vkCreateDescriptorPool(device.getVkDevice(), poolInfo, null, pDescriptorPool) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to create descriptor pool");
            }

            descriptorPool = pDescriptorPool.get(0);
            
        }
    }

    public void registerUniformBuffer(UniformBuffer uniformBuffer,  int binding) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            LongBuffer layouts = stack.mallocLong(uniformBuffer.getBuffers().size());
            for (int i = 0; i < layouts.capacity(); i++) {
                layouts.put(i, descriptorSetLayout);
            }

            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.callocStack(stack);
            allocInfo.sType(VK13.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(layouts);

            LongBuffer pDescriptorSets = stack.mallocLong(uniformBuffer.getBuffers().size());

            if (VK13.vkAllocateDescriptorSets(device.getVkDevice(), allocInfo, pDescriptorSets) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate descriptor sets");
            }

            List<Long> descriptorSets = new ArrayList<>(pDescriptorSets.capacity());

            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.callocStack(1, stack);
            bufferInfo.offset(0);

            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.callocStack(1,
                    stack);
            descriptorWrite.sType(VK13.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
            descriptorWrite.dstBinding(binding);
            descriptorWrite.dstArrayElement(0);
            descriptorWrite.descriptorType(VK13.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            descriptorWrite.descriptorCount(1);
            descriptorWrite.pBufferInfo(bufferInfo);

            for (int i = 0; i < uniformBuffer.getBuffers().size(); i++) {

                long descriptorSet = pDescriptorSets.get(i);
                bufferInfo.buffer(uniformBuffer.getBuffers().get(i));
                bufferInfo.range(VK13.VK_WHOLE_SIZE);
                descriptorWrite.dstSet(descriptorSet);
                
                VK13.vkUpdateDescriptorSets(device.getVkDevice(), descriptorWrite, null);

                descriptorSets.add(descriptorSet);
            }
            uniformBuffer.setDescriptorSet(new DescriptorSet(descriptorSets));
           
        }

    }
    public void registerTexture(Texture texture, int binding, int imageCount){
        try (MemoryStack stack = MemoryStack.stackPush()) {

            LongBuffer layouts = stack.mallocLong(imageCount);
            for (int i = 0; i < layouts.capacity(); i++) {
                layouts.put(i, descriptorSetLayout);
            }
            
            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.callocStack(stack);
            allocInfo.clear();
            allocInfo.sType(VK13.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(layouts);

            LongBuffer pDescriptorSets = stack.mallocLong(imageCount);
            int result = VK13.vkAllocateDescriptorSets(device.getVkDevice(), allocInfo, pDescriptorSets);
            if (result != VK13.VK_SUCCESS) {
                System.out.println(result == VK13.VK_ERROR_OUT_OF_HOST_MEMORY);
                System.out.println(result == VK13.VK_ERROR_OUT_OF_DEVICE_MEMORY);
                System.out.println(result == VK13.VK_ERROR_FRAGMENTED_POOL);
                System.out.println(result == VK13.VK_ERROR_OUT_OF_POOL_MEMORY);
                throw new RuntimeException("Failed to allocate descriptor sets");
            }

            List<Long> descriptorSets = new ArrayList<>(pDescriptorSets.capacity());

            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.callocStack(1, stack);
            bufferInfo.offset(0);

            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.callocStack(1, stack);
            texture.loadToWrite(descriptorWrite, binding);
            for (int i = 0; i < imageCount; i++) {

                long descriptorSet = pDescriptorSets.get(i);
                descriptorWrite.dstSet(descriptorSet);
                VK13.vkUpdateDescriptorSets(device.getVkDevice(), descriptorWrite, null);
                descriptorSets.add(descriptorSet);
            }
            
            texture.setDescriptorSet(new DescriptorSet(descriptorSets));
        }
    }

}
