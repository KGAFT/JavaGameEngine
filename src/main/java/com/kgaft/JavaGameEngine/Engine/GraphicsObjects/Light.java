package com.kgaft.JavaGameEngine.Engine.GraphicsObjects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {
    private Vector3f positionLight;
    private Vector3f lightColor;

    private Matrix4f lightMatrix;

    private float specularStrength = 0.5f;

    private float ambientStrength = 0.1f;

    private float shininess = 32f;
    public Light(Vector3f positionLight, Vector3f lightColor) {
        this.positionLight = positionLight;
        this.lightColor = lightColor;
        this.lightMatrix = new Matrix4f().identity();
    }

    public Vector3f getPositionLight() {
        return positionLight;
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

    public void setPosition(Vector3f position) {
        lightMatrix.translate(position);
    }

    public void rotate(Vector3f rotation) {
        lightMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
    }

    public Matrix4f getLightMatrix() {
        return lightMatrix;
    }
}
