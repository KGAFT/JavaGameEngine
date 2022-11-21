package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class DirectLight extends WorldObject implements ShaderStruct {
    private Vector4f color;
    private float ambientIntensity = 0.5f;
    private float diffuseIntensity = 0.75f;

    float specularPower = 32;
    float specularIntensity = 1;
    private Vector3f direction = new Vector3f(0.0f, 1.0f, 0.0f);

    public DirectLight(Vector4f color) {
        this.color = color;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }

    public float getDiffuseIntensity() {
        return diffuseIntensity;
    }

    public void setDiffuseIntensity(float diffuseIntensity) {
        this.diffuseIntensity = diffuseIntensity;
    }


    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    @Override
    public void move(Vector3f move) {

    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("color", color);
        fields.put("ambientIntensity", new Float(ambientIntensity));
        fields.put("diffuseIntensity", new Float(diffuseIntensity));
        fields.put("direction", direction);
        fields.put("specularPower", specularPower);
        fields.put("specularIntensity", specularIntensity);
        return fields;
    }
}
