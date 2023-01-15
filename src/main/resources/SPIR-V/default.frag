#version 450 core

#define LIGHT_BLOCKS_AMOUNT 100

struct PointLight{
    vec3 position;
    vec3 color;
    float intensity;
};

struct DirectLight{
    vec3 direction;
    vec3 color;
    float intensity;
};



layout(set = 0, binding = 0) uniform lightsStruct {
    PointLight pointLights[LIGHT_BLOCKS_AMOUNT];
    DirectLight directLights[LIGHT_BLOCKS_AMOUNT];
    int enabledSpotLights;
    int enabledDirectionalLights;
    int enabledPointLights;
    vec3 cameraPosition;
    float emissiveIntensity;
    float emissiveShininess;
    float gammaCorrect;
    float ambientIntensity;
} lights;
layout(binding = 1) uniform sampler2D albedoMap;
layout(binding = 2) uniform sampler2D normalMap;
layout(binding = 3) uniform sampler2D metallicMap;
layout(binding = 4) uniform sampler2D roughnessMap;
layout(binding = 5) uniform sampler2D aoMap;
layout(binding = 6) uniform sampler2D emissiveMap;



layout(location = 0) in vec3 normals;
layout(location = 1) in vec2 UV;
layout (location = 0) out vec4 outColor;

void main() {
    outColor = texture(albedoMap, UV);
}