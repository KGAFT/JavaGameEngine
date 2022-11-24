package com.kgaft.JavaGameEngine.Engine.GraphicsObjects.Light;

import com.kgaft.JavaGameEngine.Engine.GameObjects.WorldObject;
import com.kgaft.JavaGameEngine.Shader.Shader;
import com.kgaft.JavaGameEngine.Shader.ShaderStruct;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class PbrLight extends WorldObject implements ShaderStruct {
    private Vector4f color;

    public PbrLight(Vector4f color) {
        this.color = color;
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
        fields.put("position", getPosition());
        fields.put("color", color);
        return fields;
    }
}
