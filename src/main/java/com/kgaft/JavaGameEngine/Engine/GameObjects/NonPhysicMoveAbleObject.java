package com.kgaft.JavaGameEngine.Engine.GameObjects;

import org.joml.Vector3f;

public interface NonPhysicMoveAbleObject {
    void move(Vector3f move);
    void rotate(Vector3f rotation);
}
