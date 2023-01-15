package com.kgaft.KGAFTEngine.VulkanRenderer;

import com.kgaft.KGAFTEngine.Window.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.joml.Math.clamp;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanSwapChain {

    private static final int MAX_FRAMES_IN_FLIGHT = 2;
    private long swapChainKHR;
    private VulkanDevice device;
    private long renderPass;
    private int swapChainImageFormat;
    private VkExtent2D swapChainExtent;
    private List<Long> swapChainImages = new ArrayList<>();
    private List<Long> swapChainImageViews = new ArrayList<>();
    private List<Long> depthImages = new ArrayList<>();
    private List<Long> depthImagesMemories = new ArrayList<>();
    private List<Long> depthImageViews = new ArrayList<>();
    private List<Long> frameBuffers = new ArrayList<>();

    private List<Long> imageAvailableSemaphores = new ArrayList<>();
    private List<Long> renderFinishedSemaphores = new ArrayList<>();
    private List<Long> inFlightFences = new ArrayList<>();
    private List<Long> imagesInFlight = new ArrayList<>();
    private Window window;
    private int currentFrame = 0;

    public VulkanSwapChain(VulkanDevice device, Window window) {
        this.device = device;
        this.window = window;
    }

    public void load(boolean vSync) {
        createSwapChain(vSync);
        createImageViews();
        createRenderPass();
        createDepthResources();
        createFrameBuffers();
        createSyncsObjects();
    }

    private void createSyncsObjects() {
        VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.malloc();
        semaphoreCreateInfo.clear();
        semaphoreCreateInfo.sType$Default();

        VkFenceCreateInfo fenceCreateInfo = VkFenceCreateInfo.malloc();
        fenceCreateInfo.clear();
        fenceCreateInfo.sType$Default();
        fenceCreateInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

        for(int i = 0; i<MAX_FRAMES_IN_FLIGHT; i++){
            long[] imageAvailableSemaphore = new long[1];
            long[] renderFinishedSemaphore = new long[1];
            long[] fence = new long[1];
            if(vkCreateSemaphore(device.getVkDevice(),semaphoreCreateInfo, null, imageAvailableSemaphore)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create semaphore");
            }
            if(vkCreateSemaphore(device.getVkDevice(), semaphoreCreateInfo, null, renderFinishedSemaphore)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create semaphore");
            }
            if(vkCreateFence(device.getVkDevice(), fenceCreateInfo, null, fence)!=VK_SUCCESS){
                throw new RuntimeException("Failed to create fence");
            }
            imageAvailableSemaphores.add(imageAvailableSemaphore[0]);
            renderFinishedSemaphores.add(renderFinishedSemaphore[0]);
            inFlightFences.add(fence[0]);
        }
    }


    private void createSwapChain(boolean vSync) {
        SwapChainSupportDetails details = device.getSwapChainSupportDetails();
        int imageCount = details.getCapabilitiesKHR().minImageCount() + 1;
        if (details.getCapabilitiesKHR().maxImageCount() > 0 && imageCount > details.getCapabilitiesKHR().maxImageCount()) {
            imageCount = details.getCapabilitiesKHR().maxImageCount();
        }
        VkExtent2D extent2D = findSwapExtent(details.getCapabilitiesKHR());
        VkSurfaceFormatKHR surfaceFormat = findSurfaceFormat(details.getSurfaceFormats());
        VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.callocStack(stackPush());
        createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
        createInfo.surface(device.getVulkanInstance().getSurface());
        createInfo.minImageCount(imageCount);
        createInfo.imageFormat(surfaceFormat.format());
        createInfo.imageColorSpace(surfaceFormat.colorSpace());
        createInfo.imageExtent(extent2D);
        createInfo.imageArrayLayers(1);
        createInfo.imageUsage(VK13.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

        QueueFamilyIndices indices = device.getDeviceQueueIndices();
        IntBuffer intBuffer = stackGet().mallocInt(2);
        intBuffer.put(indices.getGraphicsFamily());
        intBuffer.put(indices.getPresentFamily());
        intBuffer.rewind();
        if (indices.getGraphicsFamily() != indices.getPresentFamily()) {
            createInfo.imageSharingMode(VK13.VK_SHARING_MODE_CONCURRENT);
            createInfo.pQueueFamilyIndices(intBuffer);
        } else {
            createInfo.imageSharingMode(VK13.VK_SHARING_MODE_EXCLUSIVE);
        }

        createInfo.preTransform(details.getCapabilitiesKHR().currentTransform());
        createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
        createInfo.presentMode(findPresentMode(details.getPresentModes(), vSync));
        createInfo.clipped(true);
        createInfo.oldSwapchain(VK_NULL_HANDLE);
        long[] result = new long[1];
        if (KHRSwapchain.vkCreateSwapchainKHR(device.getVkDevice(), createInfo, null, result) != VK13.VK_SUCCESS) {
            throw new RuntimeException("Failed to create swapChain");
        }
        this.swapChainKHR = result[0];
        int[] count = new int[1];
        vkGetSwapchainImagesKHR(device.getVkDevice(), swapChainKHR, count, null);
        long[] images = new long[count[0]];
        vkGetSwapchainImagesKHR(device.getVkDevice(), swapChainKHR, count, images);
        for (long image : images) {
            swapChainImages.add(image);
        }
        swapChainImageFormat = surfaceFormat.format();
        swapChainExtent = extent2D;
        intBuffer.clear();

    }


    private void createImageViews() {
        for (long swapChainImage : swapChainImages) {
            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.malloc();
            createInfo.clear();
            createInfo.sType(VK13.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            createInfo.image(swapChainImage);
            createInfo.viewType(VK13.VK_IMAGE_VIEW_TYPE_2D);
            createInfo.format(swapChainImageFormat);
            createInfo.subresourceRange().aspectMask(VK13.VK_IMAGE_ASPECT_COLOR_BIT);
            createInfo.subresourceRange().baseMipLevel(0);
            createInfo.subresourceRange().levelCount(1);
            createInfo.subresourceRange().baseArrayLayer(0);
            createInfo.subresourceRange().layerCount(1);
            createInfo.pNext(VK_NULL_HANDLE);
            long[] result = new long[1];
            if (VK13.vkCreateImageView(device.getVkDevice(), createInfo, null, result) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create image view");
            }
            swapChainImageViews.add(result[0]);

        }
    }

    private void createRenderPass() {
        try (MemoryStack stack = stackPush()) {
            VkAttachmentDescription depthAttachment = VkAttachmentDescription.malloc();
            depthAttachment.clear();
            depthAttachment.format(findDepthFormat());
            depthAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            depthAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            depthAttachment.storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            depthAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            depthAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            depthAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            depthAttachment.finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

            VkAttachmentReference depthAttachmentRef = VkAttachmentReference.callocStack(stack);
            depthAttachmentRef.attachment(1);
            depthAttachmentRef.layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

            VkAttachmentDescription colorAttachment = VkAttachmentDescription.malloc();
            colorAttachment.clear();
            colorAttachment.format(swapChainImageFormat);
            colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            colorAttachment.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference.Buffer colorAttachmentRef = VkAttachmentReference.callocStack(1, stack);
            colorAttachmentRef.attachment(0);
            colorAttachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subpass = VkSubpassDescription.callocStack(1, stack);
            subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpass.colorAttachmentCount(1);
            subpass.pColorAttachments(colorAttachmentRef);
            subpass.pDepthStencilAttachment(depthAttachmentRef);

            VkSubpassDependency.Buffer dependency = VkSubpassDependency.callocStack(1, stack);
            dependency.srcSubpass(VK_SUBPASS_EXTERNAL);
            dependency.dstSubpass(0);
            dependency.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT | VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT);
            dependency.srcAccessMask(0);
            dependency.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT | VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT);
            dependency.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT);


            VkAttachmentDescription.Buffer attachmentDescription = VkAttachmentDescription.calloc(2, stack);
            attachmentDescription.put(colorAttachment);
            attachmentDescription.put(depthAttachment);
            attachmentDescription.rewind();
            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.callocStack(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            renderPassInfo.pAttachments(attachmentDescription);
            renderPassInfo.pSubpasses(subpass);
            renderPassInfo.pDependencies(dependency);

            LongBuffer pRenderPass = stack.mallocLong(1);

            if (vkCreateRenderPass(device.getVkDevice(), renderPassInfo, null, pRenderPass) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create render pass");
            }

            renderPass = pRenderPass.get(0);
            colorAttachment.free();
            depthAttachment.free();
        }
    }

    private void createDepthResources() {
        int depthFormat = findDepthFormat();
        for (int i = 0; i < swapChainImages.size(); i++) {
            VkImageCreateInfo imageInfo = VkImageCreateInfo.malloc();
            imageInfo.clear();
            imageInfo.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            imageInfo.imageType(VK_IMAGE_TYPE_2D);
            imageInfo.extent().width(swapChainExtent.width());
            imageInfo.extent().height(swapChainExtent.height());
            imageInfo.extent().depth(1);
            imageInfo.mipLevels(1);
            imageInfo.arrayLayers(1);
            imageInfo.format(depthFormat);
            imageInfo.tiling(VK_IMAGE_TILING_OPTIMAL);
            imageInfo.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            imageInfo.usage(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT);
            imageInfo.samples(VK_SAMPLE_COUNT_1_BIT);
            imageInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            imageInfo.flags(0);
            long[] image = new long[1];
            long[] imageMemory = new long[1];
            device.createImageWithInfo(imageInfo, image, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, imageMemory);
            depthImages.add(image[0]);
            depthImagesMemories.add(imageMemory[0]);

            VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.malloc();
            viewInfo.clear();
            viewInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            viewInfo.image(image[0]);
            viewInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
            viewInfo.format(depthFormat);
            viewInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
            viewInfo.subresourceRange().baseMipLevel(0);
            viewInfo.subresourceRange().levelCount(1);
            viewInfo.subresourceRange().baseArrayLayer(0);
            viewInfo.subresourceRange().layerCount(1);
            long[] imageViewHandle = new long[1];
            if (VK13.vkCreateImageView(device.getVkDevice(), viewInfo, null, imageViewHandle) != VK_SUCCESS) {
                throw new RuntimeException("Error while creating depth image view");
            }
            depthImageViews.add(imageViewHandle[0]);
            viewInfo.free();
            imageInfo.free();

        }
    }

    private void createFrameBuffers() {
        for (int i = 0; i < swapChainImages.size(); i++) {
            VkFramebufferCreateInfo framebufferCreateInfo = VkFramebufferCreateInfo.malloc();
            LongBuffer longBuffer = stackGet().mallocLong(2);
            longBuffer.clear();
            longBuffer.put(swapChainImageViews.get(i));
            longBuffer.put(depthImageViews.get(i));
            longBuffer.rewind();
            framebufferCreateInfo.clear();
            framebufferCreateInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
            framebufferCreateInfo.renderPass(renderPass);
            framebufferCreateInfo.attachmentCount(2);
            framebufferCreateInfo.pAttachments(longBuffer);
            framebufferCreateInfo.width(swapChainExtent.width());
            framebufferCreateInfo.height(swapChainExtent.height());
            framebufferCreateInfo.layers(1);
            long[] frameBufferId = new long[1];
            if (VK13.vkCreateFramebuffer(device.getVkDevice(), framebufferCreateInfo, null, frameBufferId) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create framebuffer");
            }
            frameBuffers.add(frameBufferId[0]);
            framebufferCreateInfo.free();
        }
    }



    private VkSurfaceFormatKHR findSurfaceFormat(List<VkSurfaceFormatKHR> formats) {
        return formats.stream()
                .filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_UNORM)
                .filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
                .findAny()
                .orElse(formats.get(0));
    }

    private int findPresentMode(List<Integer> presentModes, boolean vSync) {
        if (!vSync) {
            for (Integer presentMode : presentModes) {
                if (presentMode == VK_PRESENT_MODE_MAILBOX_KHR) {
                    return presentMode;
                }
            }
        }
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkExtent2D findSwapExtent(VkSurfaceCapabilitiesKHR capabilitiesKHR) {
        if (capabilitiesKHR.currentExtent().width() != 0xFFFFFFFF) {
            return capabilitiesKHR.currentExtent();
        }


        VkExtent2D actualExtent = VkExtent2D.mallocStack().set(window.getWidth(), window.getHeight());

        VkExtent2D minExtent = capabilitiesKHR.minImageExtent();
        VkExtent2D maxExtent = capabilitiesKHR.maxImageExtent();

        actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }

    private int findDepthFormat() {
        List<Integer> formats = new ArrayList<>();
        formats.add(VK_FORMAT_D32_SFLOAT);
        formats.add(VK_FORMAT_D32_SFLOAT_S8_UINT);
        formats.add(VK_FORMAT_D24_UNORM_S8_UINT);

        return device.findSupportedFormat(formats, VK_IMAGE_TILING_OPTIMAL, VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT);
    }

    public long getRenderPass() {
        return renderPass;
    }

    public int nextImage() {
        VK13.vkWaitForFences(device.getVkDevice(), inFlightFences.get(currentFrame), true, Long.MAX_VALUE);
        int[] nextImage = new int[1];
        vkAcquireNextImageKHR(device.getVkDevice(), swapChainKHR, Long.MAX_VALUE, imageAvailableSemaphores.get(currentFrame), VK_NULL_HANDLE, nextImage);
        return nextImage[0];
    }
    public void waitFences(){
        try{
            VK13.vkWaitForFences(device.getVkDevice(), inFlightFences.get(currentFrame), true, Long.MAX_VALUE);
            VK13.vkWaitForFences(device.getVkDevice(), imagesInFlight.get(currentFrame), true, Long.MAX_VALUE);
        }catch(Exception e){

        }
        
    }

    public void submitCommandBuffer(VkCommandBuffer commandBuffer, int imageIndex) {
        try(MemoryStack stack = stackPush()){
            if(imagesInFlight.size()>imageIndex){
                vkWaitForFences(device.getVkDevice(),  imagesInFlight.get(imageIndex), true, Long.MAX_VALUE);
            }
            imagesInFlight.add(imageIndex, inFlightFences.get(currentFrame));
            VkSubmitInfo submitInfo = VkSubmitInfo.malloc();
            submitInfo.clear();
            submitInfo.sType$Default();
            LongBuffer waitSemaphores = stack.mallocLong(1);
            waitSemaphores.put(imageAvailableSemaphores.get(currentFrame));
            waitSemaphores.rewind();
            IntBuffer waitStages = stack.mallocInt(1);
            waitStages.put(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            waitStages.rewind();

            submitInfo.pWaitSemaphores(waitSemaphores);
            submitInfo.pWaitDstStageMask(waitStages);


            PointerBuffer pb = stack.mallocPointer(1);
            pb.put(commandBuffer);
            pb.rewind();

            submitInfo.pCommandBuffers(pb);

            LongBuffer signalSemaphores = stack.mallocLong(1);
            signalSemaphores.put(renderFinishedSemaphores.get(currentFrame));
            signalSemaphores.rewind();

            submitInfo.pSignalSemaphores(signalSemaphores);
            vkResetFences(device.getVkDevice(), inFlightFences.get(currentFrame));
            if(vkQueueSubmit(device.getGraphicsQueue(), submitInfo, inFlightFences.get(currentFrame))!=VK_SUCCESS){
                throw new RuntimeException("Failed to submit queues");
            }
            
            MemoryStack secondStack = stackPush();

            VkPresentInfoKHR presentInfoKHR = VkPresentInfoKHR.malloc();
            presentInfoKHR.clear();
            presentInfoKHR.sType$Default();
            signalSemaphores.clear();
            signalSemaphores.rewind();
            signalSemaphores.put(renderFinishedSemaphores.get(currentFrame));
            signalSemaphores.rewind();

            presentInfoKHR.pWaitSemaphores(signalSemaphores);

            presentInfoKHR.swapchainCount(1);
            LongBuffer swapChains = secondStack.mallocLong(1);
            swapChains.put(swapChainKHR);
            swapChains.rewind();
            presentInfoKHR.pSwapchains(swapChains);
            IntBuffer imageIndices = secondStack.mallocInt(1);
            imageIndices.put(imageIndex);
            imageIndices.rewind();

            presentInfoKHR.pImageIndices(imageIndices);

            vkQueuePresentKHR(device.getPresentQueue(), presentInfoKHR);

            currentFrame = (currentFrame+1)%MAX_FRAMES_IN_FLIGHT;
            submitInfo.free();
            presentInfoKHR.free();
        }


    }
    public int getImageCount(){
        return swapChainImages.size();
    }

    public long getFrameBuffer(int index){
        return frameBuffers.get(index);
    }

    public VkExtent2D getSwapChainExtent() {
        return swapChainExtent;
    }

    public void destroy(){
        swapChainImageViews.forEach(image->{
            vkDestroyImageView(device.getVkDevice(), image, null);
        });swapChainImages.clear();
        vkDestroySwapchainKHR(device.getVkDevice(), swapChainKHR, null);
        for (int i = 0; i < depthImageViews.size(); i++) {
            vkDestroyImageView(device.getVkDevice(), depthImageViews.get(i), null);
            vkDestroyImage(device.getVkDevice(), depthImages.get(i), null);
            vkFreeMemory(device.getVkDevice(), depthImagesMemories.get(i), null);
        }
        frameBuffers.forEach(frameBuffer->{
            vkDestroyFramebuffer(device.getVkDevice(), frameBuffer, null);
        });
        vkDestroyRenderPass(device.getVkDevice(), renderPass, null);
        for (int i = 0; i < MAX_FRAMES_IN_FLIGHT; i++) {
            vkDestroySemaphore(device.getVkDevice(), renderFinishedSemaphores.get(i), null);
            vkDestroySemaphore(device.getVkDevice(), imageAvailableSemaphores.get(i), null);
            vkDestroyFence(device.getVkDevice(), inFlightFences.get(i), null);
        }
    }
}
