package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import java.util.List;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;


public interface IFrameBuffer {
    int getId();
    void render(List<RenderTarget> renderTargets);
    void loadDataToShader();
    int getShaderType();
}
