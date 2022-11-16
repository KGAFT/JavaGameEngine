#version 330 core

layout(location = 3) in vec3 position;
layout(location = 2) in vec2 textureCoordinates;
layout(location = 1) in vec3 normals;

uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
out vec2 uvsCoords;
out vec3 Normals;


void main(){
    uvsCoords = textureCoordinates;
    Normals = normals;
    //gl_Position = cameraMatrix*modelMatrix*vec4(position, 1.0);
      gl_Position = vec4(textureCoordinates, 0, 1);
}