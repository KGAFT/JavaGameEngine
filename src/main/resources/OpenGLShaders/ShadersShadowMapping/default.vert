#version 330 core
layout (location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoordinates;
layout(location = 2) in vec3 normals;

uniform mat4 lightView;
uniform mat4 modelMatrix;

void main()
{
    gl_Position = lightView * modelMatrix * vec4(position, 1.0);
}