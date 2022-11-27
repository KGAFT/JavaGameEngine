package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class SpotPbrLight implements ShaderStruct {

    private Vector3f color;

    private Vector3f direction;

    private Vector3f position;

    private float intensity = 10;

    private float cutOff;

    public SpotPbrLight(Vector3f color, Vector3f direction, Vector3f position, float cutOff) {
        this.color = color;
        this.direction = direction;
        this.position = position;
        this.cutOff = cutOff;
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

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("direction", direction);
        fields.put("color", color);
        fields.put("position", position);
        fields.put("intensity", new Float(intensity));
        fields.put("cutOff", new Float(cutOff));
        return fields;
    }
}
