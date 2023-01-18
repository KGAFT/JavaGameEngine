package com.kgaft.KGAFTEngine.Engine.Utils;

import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Mesh;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Model;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.KGAFTEngine.Engine.VertexObjects.VertexBufferObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public Model loadModel(String modelPath){
        AIScene scene = Assimp.aiImportFile(modelPath, Assimp.aiProcess_FlipUVs | Assimp.aiProcess_Triangulate);
        System.out.println(Assimp.aiGetErrorString());
        return processScene(scene);
    }
    private Model processScene(AIScene scene){
        PointerBuffer meshes = scene.mMeshes();
        List<Mesh> result = new ArrayList<>();
        for(int c = 0; c<scene.mNumMeshes(); c++){
            AIMesh mesh = AIMesh.create(meshes.get(c));
            result.add(processMesh(mesh));
        }
        Model model = new Model(result);
        return model;
    }
    private Mesh processMesh(AIMesh mesh){
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        AIVector3D.Buffer verticesBuf = mesh.mVertices();
        while(verticesBuf.remaining()>0){
            AIVector3D vector3D = verticesBuf.get();
            positions.add(vector3D.x());
            positions.add(vector3D.y());
            positions.add(vector3D.z());
        }
        AIVector3D.Buffer normalsBuf = mesh.mNormals();
        while(normalsBuf.remaining()>0){
            AIVector3D normal = normalsBuf.get();
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());
        }
        AIVector3D.Buffer uvsBuffer = mesh.mTextureCoords(0);
        while(uvsBuffer.remaining()>0){
            AIVector3D vector3D = uvsBuffer.get();
            uvs.add(vector3D.x());
            uvs.add(vector3D.y());
        }
        AIFace.Buffer buffer = mesh.mFaces();
        while (buffer.remaining()>0){
            IntBuffer index = buffer.get().mIndices();
            while(index.remaining()>0){
                indices.add(index.get());
            }
        }
        VertexArrayObject vao = assembleDataToVao(positions, normals, uvs, indices);
        Mesh currentMesh = new Mesh(vao);
        currentMesh.setName(mesh.mName().dataString());
        return currentMesh;
    }

    private VertexArrayObject assembleDataToVao(List<Float> positions, List<Float> normals, List<Float> uvs, List<Integer> indices){
        VertexArrayObject vao = VertexArrayObject.createVao();
        float[] posRaw = new float[positions.size()];
        float[] normalsRaw = new float[normals.size()];
        float[] uvsRaw = new float[uvs.size()];
        int[] indRaw = new int[indices.size()];
        for (int i = 0; i < positions.size(); i++) {
            posRaw[i] = positions.get(i);
        }
        for (int i = 0; i < normals.size(); i++) {
            normalsRaw[i] = normals.get(i);
        }
        for (int i = 0; i < uvs.size(); i++) {
            uvsRaw[i] = uvs.get(i);
        }
        for (int i = 0; i < indices.size(); i++) {
            indRaw[i] = indices.get(i);
        }

        vao.attachEbo(ElementBufferObject.createEbo(indRaw));
        vao.attachVbo(0, VertexBufferObject.createVbo(posRaw, 3));
        vao.attachVbo(1, VertexBufferObject.createVbo(uvsRaw, 2));
        vao.attachVbo(2, VertexBufferObject.createVbo(normalsRaw, 3));
        return vao;
    }
}