package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class SpotLight extends WorldObject implements ShaderStruct {
    private float outerCone = 0.9f;
    private float innerCone = 0.95f;
    private float ambientIntensity = 0.2f;
    private float specularIntensity = 0.5f;
    private float shininess = 32f;
    private Vector4f color;

    public SpotLight(Vector4f color) {
        this.color = color;
    }

    public float getOuterCone() {
        return outerCone;
    }

    public void setOuterCone(float outerCone) {
        this.outerCone = outerCone;
    }

    public float getInnerCone() {
        return innerCone;
    }

    public void setInnerCone(float innerCone) {
        this.innerCone = innerCone;
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
        fields.put("color", color);
        fields.put("lightPosition", getPosition());
        fields.put("outerCone", new Float(outerCone));
        fields.put("innerCone", new Float(innerCone));
        fields.put("ambientIntensity", new Float(ambientIntensity));
        fields.put("specularIntensity", new Float(specularIntensity));
        fields.put("shininess", new Float(shininess));
        return fields;
    }

}
