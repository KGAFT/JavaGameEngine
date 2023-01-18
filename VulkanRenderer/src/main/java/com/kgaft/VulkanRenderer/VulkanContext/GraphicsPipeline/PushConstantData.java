package com.kgaft.VulkanRenderer.VulkanContext.GraphicsPipeline;

import org.joml.Matrix4f;

import java.nio.ByteBuffer;

public class PushConstantData {
    public static int getSize(){
        return 4*4*Float.BYTES*2;
    }

    private Matrix4f modelMatrix;
    private Matrix4f worldMatrix;

    public PushConstantData(Matrix4f modelMatrix, Matrix4f worldMatrix) {
        this.modelMatrix = modelMatrix;
        this.worldMatrix = worldMatrix;
    }
    public void storeDataIntoByteBuffer(ByteBuffer byteBuffer){
        float[] worldMatrixData = new float[4*4];
        float[] modelMatrixData = new float[4*4];
        modelMatrix.set(1, 1, 2);
        modelMatrix.get(modelMatrixData);
        worldMatrix.get(worldMatrixData);
        for (float modelMatrixDatum : modelMatrixData) {
            byteBuffer.putFloat(modelMatrixDatum);
        }
        for (float worldMatrixDatum : worldMatrixData) {
            byteBuffer.putFloat(worldMatrixDatum);
        }
    }
}
