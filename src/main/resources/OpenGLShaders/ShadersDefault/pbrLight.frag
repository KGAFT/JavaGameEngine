#version 330 core

#define LIGHT_BLOCKS_AMOUNT 100

in vec3 Normals;
in vec2 UvsCoords;
in vec3 fragmentPosition;

out vec4 FragColor;


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


uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform sampler2D emissiveMap;



uniform PointLight pointLights[LIGHT_BLOCKS_AMOUNT];
uniform DirectLight directLights[LIGHT_BLOCKS_AMOUNT];

uniform int enabledSpotLights;
uniform int enabledDirectionalLights;
uniform int enabledPointLights;
uniform vec3 cameraPosition;

const float PI = 3.14159265359;

uniform float emissiveIntensity;
uniform float emissiveShininess;
uniform float gammaCorrect;
uniform float ambientIntensity;

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

float distributeGGX(vec3 normals, vec3 halfWayVector, float roughness)
{
    float roughness4 = roughness*roughness*roughness*roughness;
    float halwayDot2 = max(dot(normals, halfWayVector), 0.0)*max(dot(normals, halfWayVector), 0.0);
    float numerator   = roughness4;
    float denominator = (halwayDot2 * (roughness4 - 1.0) + 1.0);
    denominator = PI * denominator * denominator;
    return numerator / denominator;
}

float schlickGeometryGGX(float dotWorldViewVector, float roughness)
{
    float roughnessKoef = ((roughness + 1.0)*(roughness + 1.0)) / 8.0;
    float numerator   = dotWorldViewVector;
    float denominator = dotWorldViewVector * (1.0 - roughnessKoef) + roughnessKoef;
    return numerator / denominator;
}

float smithGeometry(vec3 processedNormals, vec3 worldViewVector, vec3 lightPosition, float roughness)
{
    float worldViewVectorDot = max(dot(processedNormals, worldViewVector), 0.0);
    float lightDot = max(dot(processedNormals, lightPosition), 0.0);
    float ggx2 = schlickGeometryGGX(worldViewVectorDot, roughness);
    float ggx1 = schlickGeometryGGX(lightDot, roughness);
    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 startFresnelSchlick)
{
    return startFresnelSchlick + (1.0 - startFresnelSchlick) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

vec3 processPointLight(PointLight light, vec3 normals, vec3 worldViewVector, vec3 startFresnelSchlick, float roughness, float metallic, vec3 albedo){
    vec3 processedLightPos = normalize(light.position - fragmentPosition);
    vec3 halfWay = normalize(worldViewVector + processedLightPos);
    float distance = length(light.position - fragmentPosition);
    float attenuation = 1.0 / (distance * distance);
    vec3 radiance = light.color * light.intensity * attenuation;
    float halfWayGGX = distributeGGX(normals, halfWay, roughness);
    float geometrySmith   = smithGeometry(normals, worldViewVector, processedLightPos, roughness);
    vec3 fresnelSchlick    = fresnelSchlick(clamp(dot(halfWay, worldViewVector), 0.0, 1.0), startFresnelSchlick);
    vec3 numerator    = halfWayGGX * geometrySmith * fresnelSchlick;
    float denominator = 4.0 * max(dot(normals, worldViewVector), 0.0) * max(dot(normals, processedLightPos), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular = numerator / denominator;
    vec3 finalFresnelSchlick = vec3(1.0) - fresnelSchlick;
    finalFresnelSchlick *= 1.0 - metallic;
    float NdotL = max(dot(normals, processedLightPos), 0.0);
    return (finalFresnelSchlick * albedo / PI + specular) * radiance * NdotL;
}

vec3 processDirectionaLight(DirectLight light, vec3 normals, vec3 worldViewVector, vec3 startFresnelSchlick, float roughness, float metallic, vec3 albedo){
    vec3 processedLightPos = normalize(-light.direction);
    vec3 halfWay = normalize(worldViewVector + processedLightPos);
    vec3 radiance = light.color * light.intensity;
    float halfWayGGX = distributeGGX(normals, halfWay, roughness);
    float geometrySmith   = smithGeometry(normals, worldViewVector, processedLightPos, roughness);
    vec3 fresnelSchlick    = fresnelSchlick(clamp(dot(halfWay, worldViewVector), 0.0, 1.0), startFresnelSchlick);
    vec3 numerator    = halfWayGGX * geometrySmith * fresnelSchlick;
    float denominator = 4.0 * max(dot(normals, worldViewVector), 0.0) * max(dot(normals, processedLightPos), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular = numerator / denominator;
    vec3 finalFresnelSchlick = vec3(1.0) - fresnelSchlick;
    finalFresnelSchlick *= 1.0 - metallic;

    float NdotL = max(dot(normals, processedLightPos), 0.0);


    return (finalFresnelSchlick * albedo / PI + specular) * radiance * NdotL;
}

vec3 postProcessColor(vec3 color){
    color = color / (color + vec3(1.0));

    color = pow(color, vec3(gammaCorrect));
    return color;
}

void main()
{
    vec3 albedo = pow(texture(albedoMap, UvsCoords).rgb, vec3(2.2));
    float metallic = texture(metallicMap, UvsCoords).r;
    float roughness = texture(roughnessMap, UvsCoords).r;
    float ao = texture(aoMap, UvsCoords).r;
    vec4 emissive = texture(emissiveMap, UvsCoords);
    vec3 processedNormals = normalize(getNormalFromMap(UvsCoords, Normals, fragmentPosition));
    vec3 worldViewVector = normalize(cameraPosition - fragmentPosition);


    vec3 startFresnelSchlick = vec3(0.04);
    startFresnelSchlick = mix(startFresnelSchlick, albedo, metallic);

    vec3 Lo = vec3(0.0);

    for(int i = 0; i<enabledDirectionalLights; i++){
        Lo+=processDirectionaLight(directLights[i], processedNormals, worldViewVector, startFresnelSchlick, roughness, metallic, albedo);
    }

    for(int i = 0; i<enabledPointLights; i++){
        Lo+=processPointLight(pointLights[i], processedNormals, worldViewVector, startFresnelSchlick, roughness, metallic, albedo);
    }

    vec3 ambient = vec3(ambientIntensity) * albedo * ao;

    vec3 color = ambient + Lo;

    color+=(emissive*pow(emissive.a, emissiveShininess)*emissiveIntensity).rgb;
    color = postProcessColor(color);
    FragColor = vec4(color, 1.0);
}