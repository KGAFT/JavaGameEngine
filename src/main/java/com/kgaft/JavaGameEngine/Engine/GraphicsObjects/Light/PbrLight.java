package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class PbrLight implements ShaderStruct {
    private Vector3f color;

    private Vector3f position;
    private float intensity = 10;

    public PbrLight(Vector3f color, Vector3f position) {
        this.color = color;
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
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
        fields.put("position", position);
        fields.put("color", color);
        fields.put("intensity", new Float(intensity));
        return fields;
    }
}
