#version 330 core

in vec3 Normals;
in vec2 UvsCoords;
in vec3 fragmentPosition;
in vec4 FragPosLightSpace;

uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform sampler2D emissiveMap;
uniform sampler2D shadowMap;
uniform vec3 lightShadePos;

layout(location = 0) out vec3 position;
layout(location = 1) out vec3 normals;
layout(location = 2) out vec4 albedo;
layout(location = 3) out vec4 metallic;
layout(location = 4) out vec4 roughness;
layout(location = 5) out vec4 ao;
layout(location = 6) out vec4 emissive;


vec3 getNormalFromMap(vec2 uvsCoords, vec3 normals, vec3 fragmentPosition)
{
    vec3 tangentNormal = texture(normalMap, uvsCoords).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(fragmentPosition);
    vec3 Q2  = dFdy(fragmentPosition);
    vec2 st1 = dFdx(uvsCoords);
    vec2 st2 = dFdy(uvsCoords);
    vec3 N   = normalize(normals);
    vec3 T  = normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);
    return normalize(TBN * tangentNormal);
}

float calcShadow(vec3 normal){

    vec3 projCoords = FragPosLightSpace.xyz / FragPosLightSpace.w;

    projCoords = projCoords * 0.5 + 0.5;

    float currentDepth = projCoords.z;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    vec3 lightDir = normalize(lightShadePos - fragmentPosition);
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);
    for (int x = -1; x <= 1; ++x)
    {
        for (int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    if (projCoords.z > 1.0)
    shadow = 0.0;

    return 1.0-shadow;
}

void main()
{

    position = fragmentPosition;
    normals = normalize(getNormalFromMap(UvsCoords, Normals, fragmentPosition));
    albedo = texture(albedoMap, UvsCoords);
    metallic = texture(metallicMap, UvsCoords);
    roughness = texture(roughnessMap, UvsCoords);
    ao = texture(aoMap, UvsCoords);
    emissive = texture(emissiveMap, UvsCoords);
}