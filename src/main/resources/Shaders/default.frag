#version 330 core

in vec2 uvsCoords;

out vec4 FragColor;

in vec3 Normals;
in vec3 primitivePosition;

uniform sampler2D baseColorTexture;
uniform sampler2D specularTexture;
vec4 processAllLights(vec3 primitivePosition, sampler2D baseColorMap, sampler2D specularLight, vec2 texCoord);

void main() {
    FragColor = processAllLights(primitivePosition, baseColorTexture, specularTexture, uvsCoords);
}
