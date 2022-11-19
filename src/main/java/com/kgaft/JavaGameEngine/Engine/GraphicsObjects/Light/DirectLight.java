package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class DirectLight extends WorldObject implements ShaderStruct {
    private float ambientIntensity = 0.2f;
    private float specularIntensity = 0.5f;
    private float shininess = 32f;
    private Vector4f color;

    public DirectLight(Vector4f color) {
        this.color = color;
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

    @Override
    public void move(Vector3f move) {

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
        fields.put("color", color);
        fields.put("direction", getPosition());
        fields.put("ambientIntensity", ambientIntensity);
        fields.put("specularIntensity", specularIntensity);
        fields.put("shininess", shininess);
        return fields;
    }
}
