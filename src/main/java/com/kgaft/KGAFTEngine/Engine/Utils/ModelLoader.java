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

        AIVector3D.Buffer verticesBuf = mesh.mVertices();
        AIVector3D.Buffer normalsBuf = mesh.mNormals();
        AIVector3D.Buffer uvsBuffer = mesh.mTextureCoords(0);
        AIFace.Buffer buffer = mesh.mFaces();

        int indicesCapacity = 0;
        while (buffer.remaining()>0){
            indicesCapacity+=buffer.get().mIndices().remaining();
        }
        buffer.rewind();
        float[] positions = new float[verticesBuf.remaining()*3];
        float[] normals = new float[normalsBuf.remaining()*3];
        float[] uvs = new float[uvsBuffer.remaining()*2];
        int[] indices = new int[indicesCapacity];
        int counter = 0;
        while(verticesBuf.remaining()>0){
            AIVector3D vector3D = verticesBuf.get();
            positions[counter] = vector3D.x();
            positions[counter+1] = vector3D.y();
            positions[counter+2] = vector3D.z();
            counter+=3;
        }
        counter = 0;
        while(normalsBuf.remaining()>0){
            AIVector3D normal = normalsBuf.get();
            normals[counter] = normal.x();
            normals[counter+1] = normal.y();
            normals[counter+2] = normal.z();
            counter+=3;
        }
        counter = 0;
        while(uvsBuffer.remaining()>0){
            AIVector3D vector3D = uvsBuffer.get();
            uvs[counter] = vector3D.x();
            uvs[counter+1] = vector3D.y();
            counter+=2;
        }


        counter = 0;
        while (buffer.remaining()>0){
            IntBuffer index = buffer.get().mIndices();
            while(index.remaining()>0){
                indices[counter]=index.get();
                counter++;
            }
        }

        VertexArrayObject vao = assembleDataToVao(positions, normals, uvs, indices);
        Mesh currentMesh = new Mesh(vao);
        currentMesh.setName(mesh.mName().dataString());
        return currentMesh;
    }

    private VertexArrayObject assembleDataToVao(float[] positions, float[] normals, float[] uvs,  int[] indices){
        VertexArrayObject vao = VertexArrayObject.createVao();


        vao.attachEbo(ElementBufferObject.createEbo(indices));
        vao.attachVbo(0, VertexBufferObject.createVbo(positions, 3));
        vao.attachVbo(1, VertexBufferObject.createVbo(uvs, 2));
        vao.attachVbo(2, VertexBufferObject.createVbo(normals, 3));

        return vao;
    }
}