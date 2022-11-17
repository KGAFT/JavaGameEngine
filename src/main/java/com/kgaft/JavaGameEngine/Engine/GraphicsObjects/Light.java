package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import org.joml.Vector3f;

public class Light {
    private Vector3f positionLight;
    private Vector3f lightColor;

    private float specularStrength = 0.5f;

    private float ambientStrength = 0.1f;

    private float shininess = 32f;
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

    public float getSpecularStrength() {
        return specularStrength;
    }

    public void setSpecularStrength(float specularStrength) {
        this.specularStrength = specularStrength;
    }

    public float getAmbientStrength() {
        return ambientStrength;
    }

    public void setAmbientStrength(float ambientStrength) {
        this.ambientStrength = ambientStrength;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}
