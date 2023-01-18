package com.kgaft.KGAFTEngine.Engine.GraphicalObjects;


import java.util.List;

public class Model {
    private List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public void addTexture(Texture texture) {
        meshes.forEach(mesh -> {
            mesh.addTexture(texture);
        });
    }

    public void destroy() {
        meshes.forEach(Mesh::destroy);
    }


    public List<Mesh> getMeshes() {
        return meshes;
    }
}
