package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;

import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSet {

    private long descriptorPool;
    private int descriptorCount;
    private LongBuffer layouts;
    private LongBuffer descriptors;
    private VulkanDevice device;
    private List<Long> buffers = new ArrayList<>();
    private List<Long> sizes = new ArrayList<>();

    public DescriptorSet(VulkanDevice device, int descriptorCount, long descriptorSetLayout){
        VkDescriptorPoolSize.Buffer poolSize = VkDescriptorPoolSize.malloc(1);
        poolSize.clear();
        poolSize.type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        poolSize.descriptorCount(descriptorCount);

        VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.malloc();
        poolInfo.clear();
        poolInfo.sType (VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO);
        poolInfo.pPoolSizes(poolSize);
        poolInfo.maxSets(descriptorCount);
        long[] poolResult = new long[1];
        vkCreateDescriptorPool(device.getVkDevice(), poolInfo, null, poolResult);
        this.descriptorPool = poolResult[0];

        layouts = stackPush().mallocLong(descriptorCount);
        layouts.clear();
        for (int i = 0; i < descriptorCount; i++) {
            layouts.put(descriptorSetLayout);
        }
        layouts.rewind();

        descriptors = stackPush().mallocLong(descriptorCount);
        descriptors.clear();
        this.device = device;
        poolInfo.free();
        poolSize.free();
    }
    public void registerBuffer(long buffer, long size){
        this.buffers.add(buffer);
        this.sizes.add(size);
    }
    public void load(){
        VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.malloc();
        allocInfo.clear();
        allocInfo.sType$Default();
        allocInfo.descriptorPool(descriptorPool);
        allocInfo.pSetLayouts(layouts);
        vkAllocateDescriptorSets(device.getVkDevice(), allocInfo, descriptors);
        for (int i = 0; i < descriptorCount; i++) {
            VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.malloc(1);
            bufferInfo.clear();
            bufferInfo.buffer(buffers.get(i));
            bufferInfo.offset( 0);
            bufferInfo.range(sizes.get(i));

            VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.malloc(1);
            descriptorWrite.clear();
            descriptorWrite.sType$Default();
            descriptorWrite.dstSet(descriptors.get(i));
            descriptorWrite.dstBinding(0);
            descriptorWrite.dstArrayElement( 0);
            descriptorWrite.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
            descriptorWrite.descriptorCount(1);
            descriptorWrite.pBufferInfo(bufferInfo);

            vkUpdateDescriptorSets(device.getVkDevice(), descriptorWrite,  null);

            descriptorWrite.free();
            bufferInfo.free();
        }
    }
    public void bindDescriptorSet(int frameCount, VkCommandBuffer buffer, long pipelineLayout){
        long[] bind = new long[]{descriptors.get(frameCount)};
        vkCmdBindDescriptorSets(buffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipelineLayout, 0, bind, null);
        descriptors.rewind();
    }

}
