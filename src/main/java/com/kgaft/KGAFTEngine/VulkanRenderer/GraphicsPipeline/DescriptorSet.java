package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;

import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSet {

    private int imageCount;

    private long descriptorPool;

    private ArrayList<Long> descriptorSets = new ArrayList<>();


    public DescriptorSet(int imageCount) {
        this.imageCount = imageCount;
    }

    public void createDescriptorPool(VulkanDevice device) {

        try(MemoryStack stack = stackPush()) {

            VkDescriptorPoolSize.Buffer poolSize = VkDescriptorPoolSize.callocStack(1, stack);
            poolSize.type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            poolSize.descriptorCount(imageCount);

            VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.callocStack(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO);
            poolInfo.pPoolSizes(poolSize);
            poolInfo.maxSets(imageCount);

            LongBuffer pDescriptorPool = stack.mallocLong(1);

            if(vkCreateDescriptorPool(device.getVkDevice(), poolInfo, null, pDescriptorPool) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create descriptor pool");
            }

            descriptorPool = pDescriptorPool.get(0);
        }
    }

    public void createDescriptorSets(VulkanDevice device, List<Long> uniformBuffers, List<Long> size, long descriptorSetLayout) {

        try(MemoryStack stack = stackPush()) {

            LongBuffer layouts = stack.mallocLong(imageCount);
            for(int i = 0;i < layouts.capacity();i++) {
                layouts.put(i, descriptorSetLayout);
            }

            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO);
            allocInfo.descriptorPool(descriptorPool);
            allocInfo.pSetLayouts(layouts);

            LongBuffer pDescriptorSets = stack.mallocLong(imageCount);

            if(vkAllocateDescriptorSets(device.getVkDevice(), allocInfo, pDescriptorSets) != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate descriptor sets");
            }

            descriptorSets = new ArrayList<>(pDescriptorSets.capacity());

            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.callocStack(1, stack);
            bufferInfo.offset(0);


            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.callocStack(1, stack);
            descriptorWrite.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
            descriptorWrite.dstBinding(0);
            descriptorWrite.dstArrayElement(0);
            descriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            descriptorWrite.descriptorCount(1);
            descriptorWrite.pBufferInfo(bufferInfo);

            for(int i = 0;i < pDescriptorSets.capacity();i++) {

                long descriptorSet = pDescriptorSets.get(i);

                bufferInfo.buffer(uniformBuffers.get(i));
                bufferInfo.range(size.get(i));

                descriptorWrite.dstSet(descriptorSet);

                vkUpdateDescriptorSets(device.getVkDevice(), descriptorWrite, null);

                descriptorSets.add(descriptorSet);
            }
        }
    }
    public void bind(VkCommandBuffer commandBuffer, int index, long pipelineLayout){
        try(MemoryStack stack = stackPush()){
            vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                    pipelineLayout, 0, stack.longs(descriptorSets.get(index)), null);
        }

    }


}
