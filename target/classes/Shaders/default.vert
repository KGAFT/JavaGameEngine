#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;


uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
out vec2 UVs;



void main(){
    gl_Position = cameraMatrix*modelMatrix*vec4(position, 1.0);
    UVs = textureCoordinates;
}