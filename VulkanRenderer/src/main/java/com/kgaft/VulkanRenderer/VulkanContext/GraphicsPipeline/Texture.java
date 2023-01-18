package com.kgaft.VulkanRenderer.VulkanContext.GraphicsPipeline;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import com.kgaft.VulkanRenderer.VulkanContext.VulkanDevice;

import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private static VkImageCreateInfo imageInfo;

    public static void getSizeForDescriptorPool(VkDescriptorPoolSize.Buffer toAdd, int instancesCount) {

        toAdd.type(VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        toAdd.descriptorCount(instancesCount);

    }

    private long textureImage;
    private long textureImageMemory;
    private VulkanDevice device;
    private long textureImageView;
    private long textureSampler;
    private DescriptorSet descriptorSet;

    public void createTextureImage(String texturePath, VulkanDevice device) {
        this.device = device;

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer pChannels = stack.mallocInt(1);

            ByteBuffer pixels = stbi_load(texturePath, pWidth, pHeight, pChannels, STBI_rgb_alpha);

            long imageSize = pWidth.get(0) * pHeight.get(0) * /* always 4 due to STBI_rgb_alpha */pChannels.get(0);

            if (pixels == null) {
                throw new RuntimeException("Failed to load texture image " + texturePath);
            }

            LongBuffer pStagingBuffer = stack.mallocLong(1);
            LongBuffer pStagingBufferMemory = stack.mallocLong(1);
            device.createBuffer(imageSize,
                    VK13.VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    VK13.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK13.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                    pStagingBuffer,
                    pStagingBufferMemory);

            PointerBuffer data = stack.mallocPointer(1);
            VK13.vkMapMemory(device.getVkDevice(), pStagingBufferMemory.get(0), 0, imageSize, 0, data);
            {
                memcpy(data.getByteBuffer(0, (int) imageSize), pixels, imageSize);
            }
            VK13.vkUnmapMemory(device.getVkDevice(), pStagingBufferMemory.get(0));

            stbi_image_free(pixels);

            LongBuffer pTextureImage = stack.mallocLong(1);
            LongBuffer pTextureImageMemory = stack.mallocLong(1);
            createImage(pWidth.get(0), pHeight.get(0),
                    VK13.VK_FORMAT_R8G8B8A8_SRGB, VK13.VK_IMAGE_TILING_OPTIMAL,
                    VK13.VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK13.VK_IMAGE_USAGE_SAMPLED_BIT,
                    VK13.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                    pTextureImage,
                    pTextureImageMemory);

            textureImage = pTextureImage.get(0);
            textureImageMemory = pTextureImageMemory.get(0);

            transitionImageLayout(textureImage,
                    VK13.VK_FORMAT_R8G8B8A8_SRGB,
                    VK13.VK_IMAGE_LAYOUT_UNDEFINED,
                    VK13.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);

            copyBufferToImage(pStagingBuffer.get(0), textureImage, pWidth.get(0), pHeight.get(0));

            transitionImageLayout(textureImage,
                    VK13.VK_FORMAT_R8G8B8A8_SRGB,
                    VK13.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                    VK13.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

            VK13.vkDestroyBuffer(device.getVkDevice(), pStagingBuffer.get(0), null);
            VK13.vkFreeMemory(device.getVkDevice(), pStagingBufferMemory.get(0), null);
        }
        createTextureImageView();
        createTextureSampler();
    }

    private void createImage(int width, int height, int format, int tiling, int usage, int memProperties,
            LongBuffer pTextureImage, LongBuffer pTextureImageMemory) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            imageInfo = VkImageCreateInfo.callocStack(stack);
            imageInfo.sType(VK13.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            imageInfo.imageType(VK13.VK_IMAGE_TYPE_2D);
            imageInfo.extent().width(width);
            imageInfo.extent().height(height);
            imageInfo.extent().depth(1);
            imageInfo.mipLevels(1);
            imageInfo.arrayLayers(1);
            imageInfo.format(format);
            imageInfo.tiling(tiling);
            imageInfo.initialLayout(VK13.VK_IMAGE_LAYOUT_UNDEFINED);
            imageInfo.usage(usage);
            imageInfo.samples(VK13.VK_SAMPLE_COUNT_1_BIT);
            imageInfo.sharingMode(VK13.VK_SHARING_MODE_EXCLUSIVE);

            if (VK13.vkCreateImage(device.getVkDevice(), imageInfo, null, pTextureImage) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to create image");
            }

            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
            VK13.vkGetImageMemoryRequirements(device.getVkDevice(), pTextureImage.get(0), memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
            allocInfo.sType(VK13.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
            allocInfo.allocationSize(memRequirements.size());
            allocInfo.memoryTypeIndex(device.findMemoryType(memRequirements.memoryTypeBits(), memProperties));

            if (VK13.vkAllocateMemory(device.getVkDevice(), allocInfo, null, pTextureImageMemory) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate image memory");
            }

            VK13.vkBindImageMemory(device.getVkDevice(), pTextureImage.get(0), pTextureImageMemory.get(0), 0);
        }
    }

    private void transitionImageLayout(long image, int format, int oldLayout, int newLayout) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.callocStack(1, stack);
            barrier.sType(VK13.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER);
            barrier.oldLayout(oldLayout);
            barrier.newLayout(newLayout);
            barrier.srcQueueFamilyIndex(VK13.VK_QUEUE_FAMILY_IGNORED);
            barrier.dstQueueFamilyIndex(VK13.VK_QUEUE_FAMILY_IGNORED);
            barrier.image(image);
            barrier.subresourceRange().aspectMask(VK13.VK_IMAGE_ASPECT_COLOR_BIT);
            barrier.subresourceRange().baseMipLevel(0);
            barrier.subresourceRange().levelCount(1);
            barrier.subresourceRange().baseArrayLayer(0);
            barrier.subresourceRange().layerCount(1);

            int sourceStage;
            int destinationStage;

            if (oldLayout == VK13.VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK13.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {

                barrier.srcAccessMask(0);
                barrier.dstAccessMask(VK13.VK_ACCESS_TRANSFER_WRITE_BIT);

                sourceStage = VK13.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
                destinationStage = VK13.VK_PIPELINE_STAGE_TRANSFER_BIT;

            } else if (oldLayout == VK13.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
                    && newLayout == VK13.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {

                barrier.srcAccessMask(VK13.VK_ACCESS_TRANSFER_WRITE_BIT);
                barrier.dstAccessMask(VK13.VK_ACCESS_SHADER_READ_BIT);

                sourceStage = VK13.VK_PIPELINE_STAGE_TRANSFER_BIT;
                destinationStage = VK13.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;

            } else {
                throw new IllegalArgumentException("Unsupported layout transition");
            }

            VkCommandBuffer commandBuffer = device.beginSingleTimeCommands();

            VK13.vkCmdPipelineBarrier(commandBuffer,
                    sourceStage, destinationStage,
                    0,
                    null,
                    null,
                    barrier);

            device.endSingleTimeCommands(commandBuffer);
        }
    }

    private void copyBufferToImage(long buffer, long image, int width, int height) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkCommandBuffer commandBuffer = device.beginSingleTimeCommands();

            VkBufferImageCopy.Buffer region = VkBufferImageCopy.callocStack(1, stack);
            region.bufferOffset(0);
            region.bufferRowLength(0); // Tightly packed
            region.bufferImageHeight(0); // Tightly packed
            region.imageSubresource().aspectMask(VK13.VK_IMAGE_ASPECT_COLOR_BIT);
            region.imageSubresource().mipLevel(0);
            region.imageSubresource().baseArrayLayer(0);
            region.imageSubresource().layerCount(1);
            region.imageOffset().set(0, 0, 0);
            region.imageExtent(VkExtent3D.callocStack(stack).set(width, height, 1));

            VK13.vkCmdCopyBufferToImage(commandBuffer, buffer, image, VK13.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                    region);

            device.endSingleTimeCommands(commandBuffer);
        }
    }

    private void memcpy(ByteBuffer dst, ByteBuffer src, long size) {
        src.limit((int) size);
        dst.put(src);
        src.limit(src.capacity()).rewind();
    }

    private void createTextureImageView() {
        textureImageView = device.createImageView(textureImage, VK13.VK_FORMAT_R8G8B8A8_SRGB);
    }

    public void loadToWrite(VkWriteDescriptorSet.Buffer toAdd, int binding) {
        VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.callocStack(1, MemoryStack.stackPush());
        imageInfo.imageLayout(VK13.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
        imageInfo.imageView(textureImageView);
        imageInfo.sampler(textureSampler);

        toAdd.sType(VK13.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET);
        toAdd.dstBinding(1);
        toAdd.dstArrayElement(0);
        toAdd.descriptorType(VK13.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        toAdd.descriptorCount(binding);
        toAdd.pImageInfo(imageInfo);

    }

    protected void setDescriptorSet(DescriptorSet descriptorSet) {
        this.descriptorSet = descriptorSet;
    }

    private void createTextureSampler() {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkSamplerCreateInfo samplerInfo = VkSamplerCreateInfo.callocStack(stack);
            samplerInfo.sType(VK13.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO);
            samplerInfo.magFilter(VK13.VK_FILTER_LINEAR);
            samplerInfo.minFilter(VK13.VK_FILTER_LINEAR);
            samplerInfo.addressModeU(VK13.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.addressModeV(VK13.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.addressModeW(VK13.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.anisotropyEnable(true);
            samplerInfo.maxAnisotropy(16.0f);
            samplerInfo.borderColor(VK13.VK_BORDER_COLOR_INT_OPAQUE_BLACK);
            samplerInfo.unnormalizedCoordinates(false);
            samplerInfo.compareEnable(false);
            samplerInfo.compareOp(VK13.VK_COMPARE_OP_ALWAYS);
            samplerInfo.mipmapMode(VK13.VK_SAMPLER_MIPMAP_MODE_LINEAR);

            LongBuffer pTextureSampler = stack.mallocLong(1);

            if (VK13.vkCreateSampler(device.getVkDevice(), samplerInfo, null, pTextureSampler) != VK13.VK_SUCCESS) {
                throw new RuntimeException("Failed to create texture sampler");
            }

            textureSampler = pTextureSampler.get(0);
        }
    }
    public void attach(VkCommandBuffer commandBuffer, int index, long pipelineLayout){
        descriptorSet.bind(commandBuffer, index, pipelineLayout);
        
    }

}
