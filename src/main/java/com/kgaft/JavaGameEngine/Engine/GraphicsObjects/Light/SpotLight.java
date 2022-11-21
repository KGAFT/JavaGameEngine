package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class SpotLight extends WorldObject implements ShaderStruct {
    private Vector4f color;
    private float ambientIntensity = 0.1f;
    private float diffuseIntensity = 0.9f;
    private float constant;
    private float linear = 0.1f;
    private float exponential;
    private float specularIntensity = 1.0f;
    private float specularPower = 32f;
    private float cutOff = 0.0f;
    private Vector3f direction = new Vector3f(0.0f, -1.0f, 0.0f);

    public SpotLight(Vector4f color) {
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

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponential() {
        return exponential;
    }

    public void setExponential(float exponential) {
        this.exponential = exponential;
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = specularIntensity;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("color", color);
        fields.put("ambientIntensity", new Float(ambientIntensity));
        fields.put("diffuseIntensity", new Float(diffuseIntensity));
        fields.put("specularPower", specularPower);
        fields.put("specularIntensity", specularIntensity);
        fields.put("position", getPosition());
        fields.put("constant", constant);
        fields.put("linear", linear);
        fields.put("exp", exponential);
        fields.put("direction", direction);
        fields.put("cutOff", cutOff);
        return fields;
    }

}
