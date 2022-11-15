#version 330 core

in vec2 texCoords;

out vec4 FragColor;

in vec3 Normals;

uniform sampler2D baseColorTexture;

void main() {
    FragColor = texture(baseColorTexture, texCoords);
}
