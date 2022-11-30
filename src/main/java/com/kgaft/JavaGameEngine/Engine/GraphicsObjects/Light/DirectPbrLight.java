package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class DirectPbrLight implements ShaderStruct {
    private Vector3f color;

    private Vector3f direction;

    private float intensity = 2;

    public DirectPbrLight(Vector3f color, Vector3f direction) {
        this.color = color;
        this.direction = direction;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("direction", direction);
        fields.put("color", color);
        fields.put("intensity", new Float(intensity));
        return fields;
    }
}
