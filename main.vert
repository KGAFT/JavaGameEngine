#version 460
layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uvs;

layout(location = 0) out vec2 ReadyUvs;

vec2 fixVectorPositioning(vec2 base){
    base.y*=-1;
    return base;
}

vec3 fixVectorPositioning(vec3 base){
    base.y*=-1;
    return base;
}

vec4 fixVectorPositioning(vec4 base){
    base.y*=-1;
    return base;
}

void main() {
    ReadyUvs = uvs;
    gl_Position = vec4(position, 1.0);
}