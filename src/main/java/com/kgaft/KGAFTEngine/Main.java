package com.kgaft.KGAFTEngine;

import com.kgaft.KGAFTEngine.Window.Window;
import com.kgaft.VulkanLib.Device.Buffer.PushConstant;
import com.kgaft.VulkanLib.Device.Buffer.UniformBuffer;
import com.kgaft.VulkanLib.Device.Descriptors.DescriptorBufferInfo;
import com.kgaft.VulkanLib.Device.Descriptors.DescriptorImageInfo;
import com.kgaft.VulkanLib.Device.Descriptors.DescriptorPool;
import com.kgaft.VulkanLib.Device.Descriptors.DescriptorSet;
import com.kgaft.VulkanLib.Device.DeviceBuilder;
import com.kgaft.VulkanLib.Device.LogicalDevice.LogicalDevice;
import com.kgaft.VulkanLib.Device.PhysicalDevice.DeviceSuitability;
import com.kgaft.VulkanLib.Device.PhysicalDevice.DeviceSuitabilityResults;
import com.kgaft.VulkanLib.Device.SwapChain;
import com.kgaft.VulkanLib.Device.Synchronization.SyncManager;
import com.kgaft.VulkanLib.Instance.Instance;
import com.kgaft.VulkanLib.Instance.InstanceBuilder;
import com.kgaft.VulkanLib.Instance.InstanceLogger.DefaultVulkanFileLoggerCallback;
import com.kgaft.VulkanLib.Instance.InstanceLogger.DefaultVulkanLoggerCallback;
import com.kgaft.VulkanLib.Pipelines.GraphicsPipeline.Configuration.GraphicsPipelineBuilder;
import com.kgaft.VulkanLib.Pipelines.PipelineConfiguration.PipelineBuilder.PushConstantInfo;
import com.kgaft.VulkanLib.Pipelines.PipelineConfiguration.PipelineBuilder.UniformBufferInfo;
import com.kgaft.VulkanLib.Pipelines.PipelineConfiguration.PipelineBuilder.VertexInput;
import com.kgaft.VulkanLib.RenderPipeline.GraphicsRenderPipeline;
import com.kgaft.VulkanLib.Shader.Shader;
import com.kgaft.VulkanLib.Shader.ShaderCreateInfo;
import com.kgaft.VulkanLib.Shader.ShaderFileType;
import com.kgaft.VulkanLib.Shader.ShaderLoader;
import com.kgaft.VulkanLib.Device.PhysicalDevice.PhysicalDevice;
import com.kgaft.VulkanLib.Utils.LwjglObject;
import com.kgaft.VulkanLib.Utils.VkErrorException;


import org.joml.Vector3f;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.vulkan.VK10.*;


public class Main {
    public static void main(String[] args) throws IllegalClassFormatException, VkErrorException, IOException, InterruptedException {

        Window.prepareWindow(1280, 720, "Vulan lib development", true);
        Window window = Window.getWindow();
        InstanceBuilder instanceBuilder = new InstanceBuilder();
        instanceBuilder.presetForDebug();
        instanceBuilder.setApplicationName("VulkanLib testing app");
        instanceBuilder.setEngineName("VulkanLib testing engine");
        instanceBuilder.setApplicationVersion(1,0,0);
        instanceBuilder.setEngineVersion(1,0,0);
        instanceBuilder.presetForPresent();

        instanceBuilder.addStartingVulkanLoggerCallback(new DefaultVulkanLoggerCallback());
        instanceBuilder.addStartingVulkanLoggerCallback(new DefaultVulkanFileLoggerCallback());
        Instance instance = new Instance(instanceBuilder);
        DeviceBuilder builder = new DeviceBuilder();
        builder.requestGraphicSupport();
        builder.requestPresentSupport(window.getSurface(instance.getInstance()));
        builder.requestComputeSupport();
        builder.requestRayTracingSupport();
        HashMap<PhysicalDevice, DeviceSuitabilityResults> supportedDevices = new HashMap<>();
        PhysicalDevice.getPhysicalDevices(instance).forEach(element->{
            try {
                DeviceSuitabilityResults results = DeviceSuitability.isDeviceSuitable(builder, element);
                if(results!=null){
                    supportedDevices.put(element, results);
                }
            } catch (VkErrorException e) {
                throw new RuntimeException(e);
            }
        });
        supportedDevices.forEach((element, val)->{
            System.out.println(element.getProperties().get().deviceNameString());
        });

        LogicalDevice device = new LogicalDevice(instance, PhysicalDevice.getPhysicalDevices(instance).get(0), builder, supportedDevices.get(PhysicalDevice.getPhysicalDevices(instance).get(0)));
        SwapChain swapChain = new SwapChain(device, window.getSurface(instance.getInstance()), window.getWidth(), window.getHeight(), true);
        window.addResizeCallBack((swapChain::recreate));
        List<ShaderCreateInfo> createInfos = new ArrayList<>();
        createInfos.add(new ShaderCreateInfo("main.vert", ShaderFileType.SRC_FILE, VK_SHADER_STAGE_VERTEX_BIT, new ArrayList<>()));
        createInfos.add(new ShaderCreateInfo("main.frag", ShaderFileType.SRC_FILE, VK_SHADER_STAGE_FRAGMENT_BIT, new ArrayList<>()));
        Shader shader = ShaderLoader.createShaderParallel(device, createInfos, 2);
        GraphicsPipelineBuilder gBuilder = new GraphicsPipelineBuilder();
        gBuilder.addVertexInput(new VertexInput(0, 3, Float.SIZE, VK_FORMAT_R32G32B32_SFLOAT));
        gBuilder.addVertexInput(new VertexInput(1, 2, Float.SIZE, VK_FORMAT_R32G32_SFLOAT));

        gBuilder.addPushConstantInfo(new PushConstantInfo(VK_SHADER_STAGE_FRAGMENT_BIT, Float.BYTES*3+Integer.BYTES*1));
        gBuilder.addUniformBuffer(new UniformBufferInfo(0, Float.BYTES*3, 1, VK_SHADER_STAGE_FRAGMENT_BIT));
        LwjglObject<VkExtent2D> renderArea = new LwjglObject<>(VkExtent2D.class);
        renderArea.get().width(window.getWidth());
        renderArea.get().height(window.getHeight());
        GraphicsRenderPipeline renderPipeline = new GraphicsRenderPipeline(device, swapChain, gBuilder, shader, renderArea, swapChain.getSwapchainImages().size());



        SyncManager syncManager = new SyncManager(device, swapChain, device.getPresentQueue(), swapChain.getSwapchainImages().size());
        syncManager.addResizeCallback(renderPipeline::resize);
        window.addResizeCallBack(syncManager);
        AtomicInteger cmdCount = new AtomicInteger();

        DescriptorPool pool = new DescriptorPool(device, false);
        DescriptorSet set = pool.allocateDescriptorSet(swapChain.getSwapchainImages().size(), renderPipeline.getGraphicsPipeline().getConfigurer().getDescriptorSetLayout());

        UniformBuffer ub = new UniformBuffer(device, Float.BYTES*3);
        DescriptorBufferInfo unInfo = new DescriptorBufferInfo(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1);
        unInfo.getBase().get().get(0).buffer(ub.getBuffer());
        unInfo.getBase().get().get(0).offset(0);
        unInfo.getBase().get().get(0).range(Float.BYTES*3);
        set.addBufferInfo(unInfo);
        set.updateDescriptors();


        PushConstant pcs = new PushConstant(Float.BYTES*3+Integer.BYTES*1, renderPipeline.getGraphicsPipeline().getConfigurer().getPipelineLayout());
        Vector3f fColor = new Vector3f(1, 0, 0);
        Vector3f sColor = new Vector3f(0, 0, 1);

        while(window.isWindowActive()){
            VkCommandBuffer cmd = syncManager.beginRender(cmdCount);

            renderPipeline.begin(cmd, cmdCount.get());
            set.bindDescriptor(VK_PIPELINE_BIND_POINT_GRAPHICS, cmdCount.get(), cmd, renderPipeline.getGraphicsPipeline().getConfigurer().getPipelineLayout());
            vkCmdDraw(cmd, 3, 1, 0, 0);

            renderPipeline.endRender(cmd, cmdCount.get());
            syncManager.endRender();
            window.postEvents();
        }


        syncManager.setStop(true);
        renderPipeline.destroy();
        GraphicsRenderPipeline.releaseRenderImagePool();
        syncManager.destroy();
        shader.destroy();
        swapChain.destroy();
        device.destroy();
        KHRSurface.vkDestroySurfaceKHR(instance.getInstance(), window.getSurface(instance.getInstance()), null);
        instance.destroy();


    }

}