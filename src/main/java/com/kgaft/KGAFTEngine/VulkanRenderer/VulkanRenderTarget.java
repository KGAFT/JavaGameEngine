package com.kgaft.KGAFTEngine.VulkanRenderer;

import java.util.List;

import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.IndexBuffer;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.Texture;
import com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline.VertexBuffer;

public interface VulkanRenderTarget {
    VertexBuffer getVbo();
    IndexBuffer getIbo();
    List<Texture> getTexturesToAttach();
}
