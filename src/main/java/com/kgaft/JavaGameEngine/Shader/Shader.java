package com.kgaft.JavaGameEngine.Shader;

import com.kgaft.JavaGameEngine.Utils.IOUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionException;

public class Shader {
    private static int shaderId;


    public static void initializeShader(HashMap<String, Integer> shadersToCompile){
        List<Integer> shadersToLink = new ArrayList<>();
        shadersToCompile.forEach((fileName, type)->{
            shadersToLink.add(compileShader(getShaderSourceCode(fileName), type));
        });
        try{
            shaderId = compileShaderProgram(shadersToLink);
        }catch (RuntimeException e){
            shadersToLink.forEach(GL33::glDeleteShader);
        }
    }
    public static void attach(){
        if(shaderId!=0){
            GL33.glUseProgram(shaderId);
        }
        else{
            throw new RuntimeException("Error: shader does not loaded");
        }
    }


    private static int compileShaderProgram(List<Integer> shaders){
        int shaderProgramId = GL33.glCreateProgram();
        shaders.forEach(shader->{
            GL33.glAttachShader(shaderProgramId, shader);
        });
        GL33.glLinkProgram(shaderProgramId);
        int status = GL33.glGetProgrami(shaderProgramId, GL20.GL_LINK_STATUS);
        if(status!=1){
            String errorText = GL33.glGetProgramInfoLog(shaderProgramId);
            GL33.glDeleteProgram(shaderProgramId);
            throw new RuntimeException(errorText);
        }
        else{
            shaders.forEach(GL33::glDeleteShader);
            return shaderProgramId;
        }
    }

    private static int compileShader(String shaderSourceCode, int shaderType){
        int shaderId = GL33.glCreateShader(shaderType);
        GL33.glShaderSource(shaderId, shaderSourceCode);
        GL33.glCompileShader(shaderId);
        int compileStatus = GL33.glGetShaderi(shaderId, GL33.GL_COMPILE_STATUS);
        if(compileStatus==0){
            String errorText = GL33.glGetShaderInfoLog(shaderId);
            GL33.glDeleteShader(shaderId);
            throw new RuntimeException(errorText);
        }
        return shaderId;
    }
    private static String getShaderSourceCode(String fileInResourcesName){
        InputStream inputStream = Shader.class.getClassLoader().getResourceAsStream(fileInResourcesName);
        if(inputStream!=null){
            return IOUtil.inputStreamToString(inputStream);
        }
        return null;
    }
}
