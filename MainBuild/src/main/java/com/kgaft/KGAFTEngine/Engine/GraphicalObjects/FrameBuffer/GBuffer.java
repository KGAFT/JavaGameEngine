package com.kgaft.KGAFTEngine.Engine.GraphicalObjects.FrameBuffer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL33;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Texture;
import com.kgaft.KGAFTEngine.Engine.Shader.Shader;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexBufferObject;
import com.kgaft.KGAFTEngine.Window.Window;
import com.kgaft.KGAFTEngine.Window.WindowResizeCallBack;



public class GBuffer implements IFrameBuffer, WindowResizeCallBack{
    private int id;
    private int positionsTexture;
    private int normalTexture;
    private int albedoTexture;
    private int metallicTexture;
    private int roughnessTexture;
    private int aoTexture;
    private int emissiveTexture;
    private int rboDepth;
    private int width;
    private int height;
    private VertexArrayObject vao;
    private ArrayList<Integer> textureBlocks = new ArrayList<>();
    public GBuffer(Window window) {
        this.width = window.getWidth();
        this.height = window.getHeight();
        create();
    }

    @Override
    public int getId() {
       
        return id;
    }

    @Override
    public void render(List<RenderTarget> renderTargets) {
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, id);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
        renderTargets.forEach(renderTarget->{
            float[] worldMat = new float[4*4];
            renderTarget.getWorldMatrix().get(worldMat);
            Shader.uniformMatrix4f(worldMat, "modelMatrix");
            renderTarget.getTexturesToAttach().forEach(Texture::attach);
            renderTarget.getVertexArrayObject().draw();
        });
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void loadDataToShader() {
        loadTextures();
        vao.draw();

        GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, id);
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0); 
        GL33.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL33.GL_DEPTH_BUFFER_BIT, GL33.GL_NEAREST);
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }
    private void loadTextures(){
        Shader.uniformInt(0, "positionsMap");
        Shader.uniformInt(1, "normalMap");
        Shader.uniformInt(2, "albedoMap");
        Shader.uniformInt(3, "metallicMap");
        Shader.uniformInt(4, "roughnessMap");
        Shader.uniformInt(5, "aoMap");
        Shader.uniformInt(6, "emissiveMap");
        Shader.uniformVector2f(new float[]{width, height}, "screenSize");
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, positionsTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE1);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, normalTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE2);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, albedoTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE3);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, metallicTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE4);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, roughnessTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE5);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, aoTexture);
        GL33.glActiveTexture(GL33.GL_TEXTURE6);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, emissiveTexture);
    }

    @Override
    public int getShaderType() {
        // TODO Auto-generated method stub
        return Shader.GBUFFER_SHADER;
    }

    @Override
    public void resized(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        destroy(false);
        create();
    }

    public void destroy(boolean fullDestroy){
        GL33.glDeleteFramebuffers(id);
        GL33.glDeleteTextures(positionsTexture);
        GL33.glDeleteTextures(normalTexture);
        GL33.glDeleteTextures(albedoTexture);
        GL33.glDeleteTextures(metallicTexture);
        GL33.glDeleteTextures(roughnessTexture);
        GL33.glDeleteTextures(aoTexture);
        GL33.glDeleteTextures(emissiveTexture);
        GL33.glDeleteRenderbuffers(rboDepth);
        if(fullDestroy){
            vao.destroy();
            vao = null;

        }
        
       
    }

    private void create(){
        if(vao==null){
            vao = VertexArrayObject.createVao();
            vao.attachEbo(ElementBufferObject.createEbo(prepareQuadIndices()));
            vao.attachVbo(0, VertexBufferObject.createVbo(prepareQuadVertices(), 3));
            vao.attachVbo(1, VertexBufferObject.createVbo(prepareQuadTexCoords(), 2));
        }
        

        id = GL33.glGenFramebuffers();
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, id);
        positionsTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT0, null);
        normalTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT1, null);
        albedoTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT2, null);
        metallicTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT3, null);
        roughnessTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT4, null);
        aoTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT5, null);
        emissiveTexture = FrameBufferTexture.createTexture(id, width, height, GL33.GL_COLOR_ATTACHMENT6, null);
        GL33.glDrawBuffers(new int[]{GL33.GL_COLOR_ATTACHMENT0, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_COLOR_ATTACHMENT2,  GL33.GL_COLOR_ATTACHMENT3, GL33.GL_COLOR_ATTACHMENT4, GL33.GL_COLOR_ATTACHMENT5, GL33.GL_COLOR_ATTACHMENT6});
        rboDepth=GL33.glGenRenderbuffers();
        GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, rboDepth);
        GL33.glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL33.GL_DEPTH_COMPONENT, width, height);
        GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, rboDepth);
    
        if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Failed to create frame buffer");
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
    }
    private float[] prepareQuadVertices(){
        float[] result = new float[]{
            -1.0f,  1.0f, 0.0f, 
            -1.0f, -1.0f, 0.0f, 
            1.0f,  1.0f, 0.0f, 
            1.0f, -1.0f, 0.0f, 
        };
        return result;
    }
    private float[] prepareQuadTexCoords(){
        float[] result = new float[]{
            
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
        };
        return result;
    }
    private int[] prepareQuadIndices(){
        int[] result = new int[]{
            0, 1, 2,
            3, 2, 1
        };
        return result;
    }
}
