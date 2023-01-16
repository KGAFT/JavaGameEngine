package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PipelineConfiguration;

import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanSwapChain;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.PushConstantData;
import com.kgaft.KGAFTEngine.Window.Window;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

public class PipelineConfigStruct {
    public static PipelineConfigStruct defaultConfig(Window window, VulkanDevice device, VulkanSwapChain swapChain) {
        PipelineConfigStruct configInfo = new PipelineConfigStruct();
        configInfo.inputAssemblyInfo = VkPipelineInputAssemblyStateCreateInfo.malloc();
        configInfo.inputAssemblyInfo.clear();
        configInfo.inputAssemblyInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO);
        configInfo.inputAssemblyInfo.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
        configInfo.inputAssemblyInfo.primitiveRestartEnable(false);

        configInfo.viewport = VkViewport.malloc(1);
        configInfo.viewport.clear();
        configInfo.viewport.x(0);
        configInfo.viewport.y(0);
        configInfo.viewport.width(window.getWidth());
        configInfo.viewport.height(window.getHeight());
        configInfo.viewport.minDepth(0);
        configInfo.viewport.maxDepth(1);

        configInfo.scissor = VkRect2D.malloc(1);
        configInfo.scissor.clear();
        VkOffset2D offset2D = VkOffset2D.malloc();
        offset2D.clear();
        offset2D.x(0);
        offset2D.y(0);
        configInfo.scissor.offset(offset2D);
        VkExtent2D extent2D = VkExtent2D.malloc();
        extent2D.clear();
        extent2D.width(window.getWidth());
        extent2D.height(window.getHeight());
        configInfo.scissor.extent(extent2D);

        configInfo.viewportInfo = VkPipelineViewportStateCreateInfo.malloc(1);
        configInfo.viewportInfo.clear();
        configInfo.viewportInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
        configInfo.viewportInfo.viewportCount(1);
        configInfo.viewportInfo.pViewports(configInfo.viewport);
        configInfo.viewportInfo.scissorCount(1);
        configInfo.viewportInfo.pScissors(configInfo.scissor);

        configInfo.rasterizationInfo = VkPipelineRasterizationStateCreateInfo.malloc();
        configInfo.rasterizationInfo.clear();

        configInfo.rasterizationInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO);
        configInfo.rasterizationInfo.depthClampEnable(false);
        configInfo.rasterizationInfo.rasterizerDiscardEnable(false);
        configInfo.rasterizationInfo.polygonMode(VK_POLYGON_MODE_FILL);
        configInfo.rasterizationInfo.lineWidth(1.0f);
        configInfo.rasterizationInfo.cullMode(VK_CULL_MODE_NONE);
        configInfo.rasterizationInfo.frontFace(VK_FRONT_FACE_CLOCKWISE);
        configInfo.rasterizationInfo.depthBiasEnable(false);
        configInfo.rasterizationInfo.depthBiasConstantFactor(0.0f); // Optional
        configInfo.rasterizationInfo.depthBiasClamp(0.0f); // Optional
        configInfo.rasterizationInfo.depthBiasSlopeFactor(0.0f); // Optional

        configInfo.multisampleInfo = VkPipelineMultisampleStateCreateInfo.malloc();
        configInfo.multisampleInfo.clear();

        configInfo.multisampleInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO);
        configInfo.multisampleInfo.sampleShadingEnable(false);
        configInfo.multisampleInfo.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
        configInfo.multisampleInfo.minSampleShading(1.0f); // Optional
        configInfo.multisampleInfo.pSampleMask(null); // Optional
        configInfo.multisampleInfo.alphaToCoverageEnable(false); // Optional
        configInfo.multisampleInfo.alphaToOneEnable(false); // Optional

        configInfo.colorBlendAttachment = VkPipelineColorBlendAttachmentState.malloc(1);
        configInfo.colorBlendAttachment.clear();

        configInfo.colorBlendAttachment.colorWriteMask(
                VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT |
                        VK_COLOR_COMPONENT_A_BIT);
        configInfo.colorBlendAttachment.blendEnable(false);
        configInfo.colorBlendAttachment.srcColorBlendFactor(VK_BLEND_FACTOR_ONE); // Optional
        configInfo.colorBlendAttachment.dstColorBlendFactor(VK_BLEND_FACTOR_ZERO); // Optional
        configInfo.colorBlendAttachment.colorBlendOp(VK_BLEND_OP_ADD); // Optional
        configInfo.colorBlendAttachment.srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE); // Optional
        configInfo.colorBlendAttachment.dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO); // Optional
        configInfo.colorBlendAttachment.alphaBlendOp(VK_BLEND_OP_ADD); // Optional

        configInfo.colorBlendInfo = VkPipelineColorBlendStateCreateInfo.malloc();
        configInfo.colorBlendInfo.clear();

        configInfo.colorBlendInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO);
        configInfo.colorBlendInfo.logicOpEnable(false);
        configInfo.colorBlendInfo.logicOp(VK_LOGIC_OP_COPY); // Optional
        configInfo.colorBlendInfo.pAttachments(configInfo.colorBlendAttachment);
        configInfo.colorBlendInfo.blendConstants(0, 0); // Optional
        configInfo.colorBlendInfo.blendConstants(1, 0); // Optional
        configInfo.colorBlendInfo.blendConstants(2, 0); // Optional
        configInfo.colorBlendInfo.blendConstants(3, 0); // Optional

        configInfo.depthStencilInfo = VkPipelineDepthStencilStateCreateInfo.malloc();
        configInfo.depthStencilInfo.clear();

        configInfo.depthStencilInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO);
        configInfo.depthStencilInfo.depthTestEnable(true);
        configInfo.depthStencilInfo.depthWriteEnable(true);
        configInfo.depthStencilInfo.depthCompareOp(VK_COMPARE_OP_LESS);
        configInfo.depthStencilInfo.depthBoundsTestEnable(false);
        configInfo.depthStencilInfo.minDepthBounds(0.0f); // Optional
        configInfo.depthStencilInfo.maxDepthBounds(1.0f); // Optional
        configInfo.depthStencilInfo.stencilTestEnable(false);
        VkStencilOpState frState = VkStencilOpState.malloc();
        frState.clear();
        configInfo.depthStencilInfo.front(frState); // Optional
        VkStencilOpState bkState = VkStencilOpState.malloc();
        bkState.clear();
        configInfo.depthStencilInfo.back(bkState); // Optional

        configInfo.renderPass = swapChain.getRenderPass();
        configInfo.subpass = 0;

        VkPushConstantRange.Buffer pushConstantRange = VkPushConstantRange.malloc(1);
        pushConstantRange.clear();
        pushConstantRange.stageFlags(VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT);
        pushConstantRange.size(PushConstantData.getSize());
        pushConstantRange.offset(0);

        VkDescriptorSetLayoutBinding.Buffer uboLayoutBinding = VkDescriptorSetLayoutBinding.malloc(2);
        uboLayoutBinding.clear();
        uboLayoutBinding.binding(0);
        uboLayoutBinding.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
        uboLayoutBinding.descriptorCount(1);
        uboLayoutBinding.stageFlags(VK_SHADER_STAGE_VERTEX_BIT);
        uboLayoutBinding.pImmutableSamplers(null);
        uboLayoutBinding.get();
        uboLayoutBinding.descriptorCount(1);
        uboLayoutBinding.binding(1);
        uboLayoutBinding.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
        uboLayoutBinding.pImmutableSamplers(null);
        uboLayoutBinding.stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);
        uboLayoutBinding.rewind();

        VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.malloc();
        layoutInfo.clear();
        layoutInfo.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO);
        layoutInfo.pBindings(uboLayoutBinding);
        LongBuffer result = stackPush().mallocLong(1);
        vkCreateDescriptorSetLayout(device.getVkDevice(), layoutInfo, null, result);
        configInfo.descriptorSetLayout = result.get();
        result.rewind();
        VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.malloc();
        pipelineLayoutInfo.clear();
        pipelineLayoutInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);
        pipelineLayoutInfo.pSetLayouts(result);
        pipelineLayoutInfo.pPushConstantRanges(pushConstantRange);
        long[] layoutResult = new long[1];
        if (VK13.vkCreatePipelineLayout(device.getVkDevice(), pipelineLayoutInfo, null, layoutResult) != VK_SUCCESS) {
            throw new RuntimeException("Failed to create layout");
        }
        configInfo.pipelineLayout = layoutResult[0];

        return configInfo;
    }

    public VkViewport.Buffer viewport;
    public VkRect2D.Buffer scissor;

    public long descriptorSetLayout;
    public VkPipelineViewportStateCreateInfo.Buffer viewportInfo;
    public VkPipelineInputAssemblyStateCreateInfo inputAssemblyInfo;
    public VkPipelineRasterizationStateCreateInfo rasterizationInfo;
    public VkPipelineMultisampleStateCreateInfo multisampleInfo;
    public VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachment;
    public VkPipelineColorBlendStateCreateInfo colorBlendInfo;
    public VkPipelineDepthStencilStateCreateInfo depthStencilInfo;

    public long pipelineLayout;
    public long renderPass;
    public int subpass = 0;
}
