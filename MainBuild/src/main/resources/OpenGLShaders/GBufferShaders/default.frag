#version 330 core

in vec3 Normals;
in vec2 UvsCoords;
in vec3 fragmentPosition;


uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform sampler2D emissiveMap;

layout (location = 0) out vec3 position;
layout (location = 1) out vec3 normals;
layout (location = 2) out vec4 albedo;
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