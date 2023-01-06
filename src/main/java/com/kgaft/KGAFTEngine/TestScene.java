package com.kgaft.KGAFTEngine;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.kgaft.KGAFTEngine.Engine.GameObjects.PlayerNonPhysicsMode;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Lighting.DirectPbrLight;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.PhysicsMoveAbleObject;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Scene;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Mesh;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Model;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.Texture;
import com.kgaft.KGAFTEngine.Engine.Utils.CollisionLoader;
import com.kgaft.KGAFTEngine.Engine.Utils.ModelLoader;
import com.kgaft.KGAFTEngine.Window.KeyBoardCallBack;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import java.util.List;

public class TestScene extends Scene implements KeyBoardCallBack {
    private Mesh mesh;
    private Mesh secondMesh;
    private PlayerNonPhysicsMode cameraCallBack = new PlayerNonPhysicsMode();
    private DirectPbrLight directPbrLight;
    private com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.RigidBody rigidBody;

    public TestScene() {
        super();
    }
    private void loadModels(){
        ModelLoader loader = new ModelLoader();
        Model model = loader.loadModel(TestScene.class.getClassLoader().getResource("Models/pokedex/pokedex.gltf").getPath().substring(1));
        try {
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_BaseColor_tga.png").getPath(), Texture.ALBEDO_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_T_ao.png").getPath(), Texture.AMBIENT_OCCLUSION_MAP));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Metallic.png").getPath(), Texture.METALLIC_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Roughness.png").getPath(), Texture.ROUGHNESS_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Normal_tga.png").getPath(), Texture.NORMAL_MAP_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/pokedex/Pokedex_LowPoly_Pokedex_Emissive.png").getPath(), Texture.EMISSIVE_MAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mesh = model.getMeshes().get(0);
        model = loader.loadModel(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball.obj").getPath().substring(1));
        try{
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/pokeballColor.png").getPath(), Texture.ALBEDO_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_Metallic.png").getPath(), Texture.METALLIC_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_Roughness.png").getPath(), Texture.ROUGHNESS_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_Normal.png").getPath(), Texture.NORMAL_MAP_TEXTURE));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_AO.png").getPath(), Texture.AMBIENT_OCCLUSION_MAP));
            model.addTexture(Texture.loadTexture(TestScene.class.getClassLoader().getResource("Models/PokeBall/Pokeball_Pokeball_Emissive.png").getPath(), Texture.EMISSIVE_MAP));
        }catch (Exception e){
            e.printStackTrace();
        }
        secondMesh = model.getMeshes().get(0);
        secondMesh.setPosition(new Vector3f(0, 10, 130));

        CollisionLoader collisionLoader = new CollisionLoader();
        List<com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.RigidBody> rigidBodyList = collisionLoader.loadColission(TestScene.class.getClassLoader().getResource("Models/pokedex/pokedex.gltf").getPath().substring(1), 10, new javax.vecmath.Vector3f(0, 0, 1), new javax.vecmath.Vector3f(0, 100, 100), new Quat4f(0, 0, 0, 1));
        rigidBody = rigidBodyList.get(0);

        addRenderTarget(mesh);
        addRenderTarget(secondMesh);
    }
    private void setupLight(){
        directPbrLight = new DirectPbrLight(new Vector3f(1f, 1f, 1f), new Vector3f(0, 0,1));
        getLightManager().addDirectLight(directPbrLight);
    }
    @Override
    public void setup() {
        super.setup();
        loadModels();
        setupLight();
        cameraCallBack.addDependentObject(getCameraManager().getCurrentCamera());
        getWindow().addKeyBoardCallBack(cameraCallBack);
        getWindow().addMouseMoveCallBack(cameraCallBack);
        getWindow().addKeyBoardCallBack(this);
        CollisionShape groundShape = new StaticPlaneShape(new javax.vecmath.Vector3f(0, 1, 0), 0.25f);
        MotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(0.0f, 0.0f, -20.0f), 1.0f)));
        RigidBodyConstructionInfo groundBodyConstructionInfo = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new javax.vecmath.Vector3f(0, 0, 0));
        groundBodyConstructionInfo.restitution = 0.25f;
        RigidBody groundRigidBody = new RigidBody(groundBodyConstructionInfo);
        PhysicsMoveAbleObject physicsMoveAbleObject = new PhysicsMoveAbleObject(null, new com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.RigidBody(groundMotionState, groundRigidBody, groundBodyConstructionInfo));
        physicsMoveAbleObject.setExclude(true);
        getPhysicsManager().addPhysicsObject(new PhysicsMoveAbleObject(mesh, rigidBody));
        getPhysicsManager().addPhysicsObject(physicsMoveAbleObject);
    }

    @Override
    public void update(boolean withLight) {
        super.update(withLight);

    }


    @Override
    public int[] getKeyCodes() {
        int[] array = new int[2];
        array[0] = GLFW.GLFW_KEY_1;
        array[1] = GLFW.GLFW_KEY_2;
        return array;
    }
    int c = 0;
    @Override
    public void keyPressed(int keyCodeId) {
        c+=10;
        rigidBody.getRigidBody().applyForce(new javax.vecmath.Vector3f(100, 100, 100), new javax.vecmath.Vector3f(0, 10, 0));
    }
}
