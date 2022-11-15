package com.kgaft.OpenlGLGameEngine.Engine.GraphicalObjects;

import org.joml.Vector3f;

import java.util.List;

public class Model {
    private List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }
    public void draw(){
        meshes.forEach(Mesh::updateAndLoadToGameWorld);
    }
    public void rotate(Vector3f rotation){
        meshes.forEach(mesh -> mesh.rotate(rotation));
    }
    public void setPosition(Vector3f position){
        meshes.forEach(mesh -> mesh.setPosition(position));
    }
    public void scale(Vector3f scale){
        meshes.forEach(mesh -> mesh.scale(scale));
    }
}
