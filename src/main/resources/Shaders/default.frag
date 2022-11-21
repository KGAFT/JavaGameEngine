#version 330 core

in vec2 uvsCoords;

out vec4 FragColor;

uniform sampler2D baseColorTexture;


vec4 loadLights();

void main() {
    FragColor = texture(baseColorTexture, uvsCoords)*loadLights();
}
