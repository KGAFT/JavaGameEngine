#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;


uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
out vec2 UvsCoords;


void main(){
    UvsCoords = textureCoordinates;
    gl_Position = vec4(position, 1.0);
}