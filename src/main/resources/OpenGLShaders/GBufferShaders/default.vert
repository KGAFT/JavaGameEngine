#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;
layout(location = 2) in vec3 normals;

uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
uniform mat4 lightView;
out vec2 UvsCoords;
out vec3 fragmentPosition;
out vec3 Normals;
out vec4 lightSpaceFrag;
void main(){
    UvsCoords = textureCoordinates;
    //Try to recalculate normals with multiply by world matrix, after getting it from normal map
    Normals = (vec4(normals, 0.0f)*modelMatrix).xyz;
    fragmentPosition = vec3(modelMatrix*vec4(position, 1.0));
    lightSpaceFrag = lightView * vec4(position, 1.0);
    gl_Position = cameraMatrix*modelMatrix*vec4(position, 1.0);
}