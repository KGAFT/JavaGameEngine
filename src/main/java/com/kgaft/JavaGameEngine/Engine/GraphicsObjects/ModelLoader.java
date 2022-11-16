package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;


import com.kgaft.JavaGameEngine.Engine.VertexObjects.ElementBufferObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexArrayObject;
import com.kgaft.JavaGameEngine.Engine.VertexObjects.VertexBufferObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelLoader {

    private HashMap<String, Texture> textureList = new HashMap<>();

    public Model loadModel(String modelPath){
        AIScene scene = Assimp.aiImportFile(modelPath, Assimp.aiProcess_FlipUVs | Assimp.aiProcess_Triangulate);
        System.out.println(Assimp.aiGetErrorString());
        return processScene(scene, modelPath);
    }
    private Model processScene(AIScene scene, String modelPath){
        PointerBuffer meshes = scene.mMeshes();
        File file = new File(modelPath);
        List<Mesh> modelMeshes = new ArrayList<>();
        for(int c = 0; c<scene.mNumMeshes(); c++){
            AIMesh mesh = AIMesh.create(meshes.get(c));
            modelMeshes.add(processMesh(mesh, scene, file.getParent()));
        }
        return new Model(modelMeshes);
    }
    private Mesh processMesh(AIMesh mesh, AIScene scene, String directory){
        List<Texture> textures = loadTextures(AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex())), Assimp.aiTextureType_DIFFUSE, directory);

        textures.addAll(loadTextures(AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex())), Assimp.aiTextureType_SPECULAR, directory));
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
        return assembleDataToMesh(positions, normals, uvs, indices, textures);
    }
    private List<Texture> loadTextures(AIMaterial material, int textureType, String textureDirectory){
        List<Texture> results = new ArrayList<>();
        int count = Assimp.aiGetMaterialTextureCount(material, textureType);
        AIString path = AIString.calloc();
        for(int c = 0; c<count; c++){
            path.clear();
            Assimp.aiGetMaterialTexture(material, textureType, 0, path, (IntBuffer) null, null, null, null, null, null);
            Texture texture = null; //textureList.get(path.dataString());
            if(texture==null){
                switch (textureType){
                    case Assimp.aiTextureType_DIFFUSE:
                        /*
                        texture = Texture.loadTexture(textureDirectory+"/"+path.dataString());

                         */
                        //texture.setSamplerName("baseColorTexture");
                        break;

                }
            }
            if(texture!=null){
                results.add(texture);
                if(!textureList.containsKey(path.dataString())){
                    textureList.put(path.dataString(), texture);
                }
            }
        }
        return results;
    }
    private Mesh assembleDataToMesh(List<Float> positions, List<Float> normals, List<Float> uvs, List<Integer> indices, List<Texture> textures){
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
        Mesh mesh = new Mesh();
        mesh.setMeshTextures(textures);
        mesh.setVertexArrayObject(vao);
        return mesh;
    }
}
