package com.kgaft.KGAFTEngine.VulkanRenderer;

public class QueueFamilyIndices {
    private int graphicsFamily = -1;
    private int presentFamily = -1;

    public QueueFamilyIndices() {
    }

    public QueueFamilyIndices(int graphicsFamily, int presentFamily) {
        this.graphicsFamily = graphicsFamily;
        this.presentFamily = presentFamily;
    }

    public int getGraphicsFamily() {
        return graphicsFamily;
    }

    public void setGraphicsFamily(int graphicsFamily) {
        this.graphicsFamily = graphicsFamily;
    }

    public int getPresentFamily() {
        return presentFamily;
    }

    public void setPresentFamily(int presentFamily) {
        this.presentFamily = presentFamily;
    }
}
