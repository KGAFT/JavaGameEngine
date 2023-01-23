#version 450


layout(location = 0) in vec3 position;
layout(location = 1) in vec3 Normals;
layout(location = 2) in vec2 uv;

layout(location = 0) out vec3 normals;
layout(location = 1) out vec2 UV;

layout(push_constant) uniform PushConstant{
    mat4 modelMatrix;
    mat4 cameraMatrix;
} push;
layout(set = 0, binding=0) uniform ubo{
    float data;
} gubo;

vec3 repairCoord(vec3 inputCoord){
    inputCoord.y = inputCoord.y*-1;
    return inputCoord;
}

void main() {

    gl_Position = vec4(repairCoord(position), 1.0);
    if(gubo.data==1){
        gl_Position.x*=0.5f;
    }
    normals = Normals;
    UV = uv;
}