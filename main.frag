#version 460
layout(location = 0) in vec2 uvs;


layout(location = 0) out vec4 FragColor;

layout(push_constant) uniform OutputConfig{
    vec3 color;
    int switchSource;
} config;

layout(std140, binding = 0) uniform UnConfig{
    vec3 color;
} unConfig;

void main() {
    if(config.switchSource!=0){
        FragColor = vec4(config.color, 1.0);
    } else {
        FragColor =  vec4(unConfig.color, 1.0);
    }
}