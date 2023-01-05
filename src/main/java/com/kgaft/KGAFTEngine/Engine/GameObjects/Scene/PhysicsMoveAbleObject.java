package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene;

import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.IObjectWithPhysic;
import com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics.RigidBody;
import com.kgaft.KGAFTEngine.Engine.GraphicalObjects.RenderTarget;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PhysicsMoveAbleObject implements IObjectWithPhysic {
    private RenderTarget renderTarget;
    private RigidBody rigidBody;

    private boolean exclude;

    public PhysicsMoveAbleObject(RenderTarget renderTarget, RigidBody rigidBody) {
        this.renderTarget = renderTarget;
        this.rigidBody = rigidBody;
    }

    @Override
    public RigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public void setWorldMatrix(Matrix4f worldMatrix) {
        try {
            renderTarget.setWorldMatrix(worldMatrix);
        } catch (NullPointerException e) {

        }

    }

    public void rotate(Vector3f rotation){
        rigidBody.setRotation(rotation);
    }
    public void setPosition(Vector3f position){
        rigidBody.setPosition(position);
    }

    @Override
    public boolean isExclude() {
        return exclude;
    }

    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }
}
