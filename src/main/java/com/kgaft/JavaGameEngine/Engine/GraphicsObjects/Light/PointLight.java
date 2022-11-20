package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class PointLight extends WorldObject implements ShaderStruct {
    private float farDistance = 3.0f;
    private float nearDistance = 0.7f;
    private float ambientIntensity = 0.2f;
    private float specularIntensity = 0.5f;
    private float shininess = 32;
    private Vector4f color;

    public PointLight(Vector4f color) {
        this.color = color;
    }

    public float getFarDistance() {
        return farDistance;
    }

    public void setFarDistance(float farDistance) {
        this.farDistance = farDistance;
    }

    public float getNearDistance() {
        return nearDistance;
    }

    public void setNearDistance(float nearDistance) {
        this.nearDistance = nearDistance;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = specularIntensity;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("lightPosition", getPosition());
        fields.put("lightColor", color);
        fields.put("a", farDistance);
        fields.put("b", nearDistance);
        fields.put("ambientIntensity", ambientIntensity);
        fields.put("specularIntensity", specularIntensity);
        fields.put("shininess", shininess);
        return fields;
    }
}
