#version 330 core

in vec2 UVs;

out vec4 FragColor;

uniform sampler2D baseColorTexture;

void main() {
    FragColor = texture(baseColorTexture, UVs);
}
