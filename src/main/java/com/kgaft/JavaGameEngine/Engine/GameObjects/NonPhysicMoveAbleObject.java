package com.kgaft.JavaGameEngine.Engine.GameObjects;

public interface NonPhysicMoveAbleObject {
    void move(float forwardBackWardAmplifier, float leftRightAmplifier, float upDownAmplifier);
    void rotate(float xSpeed, float ySpeed);
}
