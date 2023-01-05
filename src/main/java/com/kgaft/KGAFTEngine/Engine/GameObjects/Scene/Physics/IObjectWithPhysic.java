package com.kgaft.KGAFTEngine.Engine.GameObjects.Scene.Physics;


import org.joml.Matrix4f;

import java.util.List;

public interface IObjectWithPhysic {
    RigidBody getRigidBody();
    void setWorldMatrix(Matrix4f worldMatrix);
    boolean isExclude();
}
