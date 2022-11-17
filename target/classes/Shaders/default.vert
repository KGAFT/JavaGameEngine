#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;
layout(location = 2) in vec3 normals;

uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
out vec2 uvsCoords;
out vec3 Normals;
out vec3 fragmentPosition;


void main(){
    uvsCoords = textureCoordinates;
    Normals = mat3(transpose(inverse(modelMatrix)))*normals;
    fragmentPosition = vec3(modelMatrix*vec4(position, 1.0));
    gl_Position = cameraMatrix*modelMatrix*vec4(position, 1.0);
}