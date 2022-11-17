#version 330 core

in vec2 uvsCoords;

out vec4 FragColor;

in vec3 Normals;

uniform sampler2D baseColorTexture;

vec3 processAllLights(vec3 objectColor);

void main() {
    vec4 objectColor = texture(baseColorTexture, uvsCoords);
    FragColor = vec4(processAllLights(vec3(objectColor.x, objectColor.y, objectColor.z)), objectColor.w);
}
