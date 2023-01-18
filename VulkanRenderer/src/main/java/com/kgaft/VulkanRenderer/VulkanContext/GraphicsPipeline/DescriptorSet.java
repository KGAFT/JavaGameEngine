package com.kgaft.VulkanRenderer.VulkanContext.GraphicsPipeline;

import com.kgaft.VulkanRenderer.VulkanContext.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class DescriptorSet {
    private List<Long> descriptorSets;

    

    protected DescriptorSet(List<Long> descriptorSets) {
        this.descriptorSets = descriptorSets;
    }



    public void bind(VkCommandBuffer commandBuffer, int index, long pipelineLayout) {
        try (MemoryStack stack = stackPush()) {
            vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                    pipelineLayout, 0, stack.longs(descriptorSets.get(index)), null);
        }

    }

}
