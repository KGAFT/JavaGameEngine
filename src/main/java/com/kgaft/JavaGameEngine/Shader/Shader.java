package com.kgaft.JavaGameEngine.Shader;

import com.kgaft.JavaGameEngine.Utils.IOUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shader {

    private static int shaderId = -1;

    private static final String PBR_LIGHT_SHADER_NAME = "pbrLight.frag";
    private static final String PHONG_LIGHT_SHADER_NAME = "light.frag";
    private static final String PHONG_ENTRY_POINT = "default.frag";

    public static void uniformMatrix4f(float[] data, String matrixName) {
        GL33.glUniformMatrix4fv(GL33.glGetUniformLocation(shaderId, matrixName), false, data);
    }

    public static void uniformFloatValueInArrayOfStructs(int index, float value, String arrayName, String valueName) {
        String name = arrayName + "[" + index + "]." + valueName;
        GL33.glUniform1f(GL33.glGetUniformLocation(shaderId, name), value);
    }
    public static void uniformArrayOfStructs(List<ShaderStruct> values, String arrayName){
        for (int i = 0; i < values.size(); i++) {
            String variableName = arrayName+"["+i+"]";
            uniformStruct(values.get(i), variableName);
        }
    }

    public static void uniformStruct(ShaderStruct shaderStruct, String variableName) {
        shaderStruct.getFields().forEach((name, value) -> {
            String fieldName = variableName + "." + name;
            if (Float.class.equals(value.getClass())) {
                uniformFloat((Float) value, fieldName);
            } else if (Integer.class.equals(value.getClass())) {
                uniformInt((Integer) value, fieldName);
            } else if (Vector3f.class.equals(value.getClass())) {
                uniformVector3f((Vector3f) value, fieldName);
            } else if (Vector4f.class.equals(value.getClass())) {
                uniformVector4f((Vector4f) value, fieldName);
            } else if (Matrix4f.class.equals(value.getClass())) {
                float[] data = new float[4 * 4];
                uniformMatrix4f(data, fieldName);
            }
        });
    }

    public static void uniformVector3fInArrayOfStructs(int index, Vector3f value, String arrayName, String valueName) {
        String name = arrayName + "[" + index + "]." + valueName;
        GL33.glUniform3f(GL33.glGetUniformLocation(shaderId, name), value.x, value.y, value.z);
    }

    public static void uniformMatrix4fInArrayOfStructs(int index, Matrix4f value, String arrayName, String valueName) {
        String name = arrayName + "[" + index + "]." + valueName;
        float[] data = new float[4 * 4];
        value.get(data);
        GL33.glUniformMatrix4fv(GL33.glGetUniformLocation(shaderId, name), false, data);
    }

    public static void uniformVector3f(float[] data, String vectorName) {
        GL33.glUniform3fv(GL33.glGetUniformLocation(shaderId, vectorName), data);
    }

    public static void uniformVector4f(Vector4f data, String vectorName) {
        GL33.glUniform4f(GL33.glGetUniformLocation(shaderId, vectorName), data.x, data.y, data.z, data.w);
    }

    public static void uniformVector3f(Vector3f data, String vectorName) {
        GL33.glUniform3f(GL33.glGetUniformLocation(shaderId, vectorName), data.x, data.y, data.z);
    }

    public static void uniformFloat(float value, String varName) {
        GL33.glUniform1f(GL33.glGetUniformLocation(shaderId, varName), value);
    }

    public static void uniformInt(int value, String varName) {
        GL33.glUniform1i(GL33.glGetUniformLocation(shaderId, varName), value);
    }
    public static void initForPhongLight(){
        List<String> exclude = new ArrayList<>();
        exclude.add(PBR_LIGHT_SHADER_NAME);
        initializeShader(exclude);
    }
    public static void initForPbrLight(){
        List<String> exclude = new ArrayList<>();
        exclude.add(PHONG_LIGHT_SHADER_NAME);
        exclude.add(PHONG_ENTRY_POINT);
        initializeShader(exclude);
    }
    public static void initializeShader(List<String> exclude) {
        List<Integer> shadersToLink = new ArrayList<>();
        File file = new File(Shader.class.getClassLoader().getResource("Shaders").getPath());
        for (File listFile : file.listFiles()) {
            if(!exclude.contains(listFile)){
                int type = 0;
                switch (IOUtil.getFileExtension(listFile.getName())) {
                    case "frag":
                        type = GL33.GL_FRAGMENT_SHADER;
                        break;
                    case "vert":
                        type = GL33.GL_VERTEX_SHADER;
                        break;
                }
                shadersToLink.add(compileShader(getShaderSourceCode("Shaders/" + listFile.getName()), type));
            }
           }
        try {
            shaderId = compileShaderProgram(shadersToLink);
        } catch (RuntimeException e) {
            shadersToLink.forEach(GL33::glDeleteShader);
        }
    }

    public static void attach() {
        if (shaderId != -1) {
            GL33.glUseProgram(shaderId);
        } else {
            throw new RuntimeException("Error: shader does not loaded");
        }
    }


    private static int compileShaderProgram(List<Integer> shaders) {
        int shaderProgramId = GL33.glCreateProgram();
        shaders.forEach(shader -> {
            GL33.glAttachShader(shaderProgramId, shader);
        });
        GL33.glLinkProgram(shaderProgramId);
        int status = GL33.glGetProgrami(shaderProgramId, GL20.GL_LINK_STATUS);
        if (status != 1) {
            String errorText = GL33.glGetProgramInfoLog(shaderProgramId);
            GL33.glDeleteProgram(shaderProgramId);
            throw new RuntimeException(errorText);
        } else {
            shaders.forEach(GL33::glDeleteShader);
            return shaderProgramId;
        }
    }

    private static int compileShader(String shaderSourceCode, int shaderType) {
        int shaderId = GL33.glCreateShader(shaderType);
        GL33.glShaderSource(shaderId, shaderSourceCode);
        GL33.glCompileShader(shaderId);
        int compileStatus = GL33.glGetShaderi(shaderId, GL33.GL_COMPILE_STATUS);
        if (compileStatus == 0) {
            String errorText = GL33.glGetShaderInfoLog(shaderId);
            GL33.glDeleteShader(shaderId);
            throw new RuntimeException(errorText);
        }
        return shaderId;
    }

    private static String getShaderSourceCode(String fileInResourcesName) {
        InputStream inputStream = Shader.class.getClassLoader().getResourceAsStream(fileInResourcesName);
        if (inputStream != null) {
            return IOUtil.inputStreamToString(inputStream);
        }
        return null;
    }

    public static int getShaderId() {
        return shaderId;
    }
}
