#version 450 core


layout(binding = 1) uniform sampler2D texSampler;

layout(location = 0) in vec3 normals;
layout(location = 1) in vec2 UV;
layout (location = 0) out vec4 outColor;

void main() {
    outColor = texture(texSampler, UV);
}