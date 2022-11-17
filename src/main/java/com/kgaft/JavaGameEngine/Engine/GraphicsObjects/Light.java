package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import org.joml.Vector3f;

public class Light {
    private Vector3f positionLight;
    private Vector3f lightColor;

    public Light(Vector3f positionLight, Vector3f lightColor) {
        this.positionLight = positionLight;
        this.lightColor = lightColor;
    }

    public Vector3f getPositionLight() {
        return positionLight;
    }

    public void setPositionLight(Vector3f positionLight) {
        this.positionLight = positionLight;
    }

    public Vector3f getLightColor() {
        return lightColor;
    }

    public void setLightColor(Vector3f lightColor) {
        this.lightColor = lightColor;
    }
}
