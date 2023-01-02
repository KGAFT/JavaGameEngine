package com.kgaft.KGAFTEngine;

import com.kgaft.KGAFTEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Lighting.DirectPbrLight;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Mesh;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Texture;
import com.kgaft.KGAFTEngine.Engine.Utils.ModelLoader;
import org.joml.Vector3f;

public class TestScene extends Scene {
    private Mesh mesh;
    private PlayerNonPhysicsMode cameraCallBack = new PlayerNonPhysicsMode();
    @Override
    public void setup() {
        ModelLoader loader = new ModelLoader();
        mesh = loader.loadModel(TestScene.class.getClassLoader().getResource("Models/pokedex/pokedex.gltf").getPath().substring(1));
        try {
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_BaseColor_tga.png").getPath(), Texture.ALBEDO_TEXTURE), true);
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_T_ao.png").getPath(), Texture.AMBIENT_OCCLUSION_MAP), true);
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Metallic.png").getPath(), Texture.METALLIC_TEXTURE), true);
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Roughness.png").getPath(), Texture.ROUGHNESS_TEXTURE), true);
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Normal_tga.png").getPath(), Texture.NORMAL_MAP_TEXTURE), true);
            mesh.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Emissive.png").getPath(), Texture.EMISSIVE_MAP), true);
        } catch (Exception e) {

        }
        getLightManager().addDirectLight(new DirectPbrLight(new Vector3f(0.5f, 0.2f, 0.4f), new Vector3f(1, 1,1)));
        cameraCallBack.addDependentObject(getCameraManager().getCurrentCamera());
        getWindow().addKeyBoardCallBack(cameraCallBack);
        getWindow().addMouseMoveCallBack(cameraCallBack);
        addRederTarget(mesh);
    }

    @Override
    public void update() {

    }


}
