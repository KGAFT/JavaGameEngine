package com.kgaft.KGAFTEngine.VulkanRenderer.GraphicsPipeline;

import com.kgaft.KGAFTEngine.VulkanRenderer.VulkanDevice;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VK13;
import org.lwjgl.vulkan.VkMappedMemoryRange;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_WHOLE_SIZE;

public class UniformBuffer {
    private VulkanDevice device;
    private long instanceSize;
    private int instanceCount;
    private int usageFlags;
    private int memoryPropertyFlags;

    private long alignmentSize;

    private long bufferSize;

    private long buffer;
    private long bufferMemory;
    private PointerBuffer map;

    public UniformBuffer(VulkanDevice device, long instanceSize, int instanceCount, int usageFlags, int memoryPropertyFlags, long minOffsetAlignment) {
        this.device = device;
        this.instanceSize = instanceSize;
        this.instanceCount = instanceCount;
        this.usageFlags = usageFlags;
        this.memoryPropertyFlags = memoryPropertyFlags;
        if(minOffsetAlignment == -1){
            this.alignmentSize = getAlignment(instanceSize, 1);
        }
        else{
            this.alignmentSize = getAlignment(instanceSize, minOffsetAlignment);
        }

        this.bufferSize = alignmentSize*instanceCount;
        LongBuffer res = stackPush().mallocLong(1);
        LongBuffer resMemory = stackPush().mallocLong(1);
        device.createBuffer(bufferSize, usageFlags, memoryPropertyFlags, res, resMemory);
        this.buffer = res.get();
        this.bufferMemory = resMemory.get();
        map(VK_WHOLE_SIZE, 0);
    }

    private long getAlignment(long instanceSize, long minOffsetAlignment) {
        if (minOffsetAlignment > 0) {
            return (instanceSize + minOffsetAlignment - 1) & ~(minOffsetAlignment - 1);
        }
        return instanceSize;
    }
    private void map(long size, long offset){
        map = stackPush().mallocPointer(1);
        VK13.vkMapMemory(device.getVkDevice(), bufferMemory, offset, size, 0, map);
    }
    private void unmap(){
        if(map!=null){
            VK13.vkUnmapMemory(device.getVkDevice(), bufferMemory);
            map = null;

        }
    }
    public void write(ByteBuffer data, long size, long offset){
        if(size== VK_WHOLE_SIZE){
            map.getByteBuffer((int) size).put(data);
        }
        else{
            byte[] toPut = new byte[data.remaining()];
            data.get(toPut, 0, data.remaining());

            map.getByteBuffer((int) size).put(toPut, (int) offset, 0);

        }
    }
    public ByteBuffer getBuffer(int size){
        return map.getByteBuffer(size);
    }
    public void flush(long size, long offset){
        VkMappedMemoryRange mappedRange = VkMappedMemoryRange.malloc();
        mappedRange.clear();
        mappedRange.sType$Default();
        mappedRange.memory(bufferMemory);
        mappedRange.offset(offset);
        mappedRange.size(size);
        VK13.vkFlushMappedMemoryRanges(device.getVkDevice(), mappedRange);

    }
    public void invalidate(long size, long offset) {
        VkMappedMemoryRange mappedRange = VkMappedMemoryRange.malloc();
        mappedRange.clear();
        mappedRange.sType$Default();
        mappedRange.memory(bufferMemory);
        mappedRange.offset(offset);
        mappedRange.size(size);
        VK13.vkInvalidateMappedMemoryRanges(device.getVkDevice(), mappedRange);
    }

    public long getBuffer() {
        return buffer;
    }

    public long getBufferSize() {
        return bufferSize;
    }
}
