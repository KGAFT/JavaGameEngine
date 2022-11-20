#version 330 core

in vec2 uvsCoords;

out vec4 FragColor;

in vec3 Normales;
in vec3 primitivePosition;

uniform sampler2D baseColorTexture;


out vec3 WorldPos0;
out vec3 Normals;

vec4 loadLights();

void main() {
    WorldPos0 = primitivePosition;
    Normals = Normales;
    FragColor = texture(baseColorTexture, uvsCoords)*loadLights();
}
