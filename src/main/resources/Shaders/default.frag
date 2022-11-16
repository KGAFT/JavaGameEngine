#version 330 core

in vec2 uvsCoords;

out vec4 FragColor;

in vec3 Normals;

uniform sampler2D baseColorTexture;

void main() {
    FragColor = texture(baseColorTexture, uvsCoords);
}
