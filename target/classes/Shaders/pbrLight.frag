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

struct SpotLight{
    vec3 position;
    vec3 direction;
    vec3 color;
    float intensity;
    float cutOff;
};


uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;






uniform PointLight pointLights[LIGHT_BLOCKS_AMOUNT];
uniform DirectLight directLights[LIGHT_BLOCKS_AMOUNT];
uniform SpotLight spotLights[LIGHT_BLOCKS_AMOUNT];

uniform int enabledSpotLights;
uniform int enabledDirectionalLights;
uniform int enabledPointLights;
uniform vec3 cameraPosition;

const float PI = 3.14159265359;



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

// ----------------------------------------------------------------------------
float DistributionGGX(vec3 normals, vec3 halfWayVector, float roughness)
{
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(normals, halfWayVector), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySmith(vec3 processedNormals, vec3 worldViewVector, vec3 lightPosition, float roughness)
{
    float NdotV = max(dot(processedNormals, worldViewVector), 0.0);
    float NdotL = max(dot(processedNormals, lightPosition), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
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

    float NDF = DistributionGGX(normals, halfWay, roughness);
    float G   = GeometrySmith(normals, worldViewVector, processedLightPos, roughness);
    vec3 F    = fresnelSchlick(clamp(dot(halfWay, worldViewVector), 0.0, 1.0), startFresnelSchlick);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(normals, worldViewVector), 0.0) * max(dot(normals, processedLightPos), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular = numerator / denominator;

    // kS is equal to Fresnel
    vec3 kS = F;

    vec3 kD = vec3(1.0) - kS;

    kD *= 1.0 - metallic;

    // scale light by NdotL
    float NdotL = max(dot(normals, processedLightPos), 0.0);


    return (kD * albedo / PI + specular) * radiance * NdotL;
}

vec3 processDirectionaLight(DirectLight light, vec3 normals, vec3 worldViewVector, vec3 startFresnelSchlick, float roughness, float metallic, vec3 albedo){
    vec3 processedLightPos = normalize(-light.direction);
    vec3 halfWay = normalize(worldViewVector + processedLightPos);

    vec3 radiance = light.color * light.intensity;

    float NDF = DistributionGGX(normals, halfWay, roughness);
    float G   = GeometrySmith(normals, worldViewVector, processedLightPos, roughness);
    vec3 F    = fresnelSchlick(clamp(dot(halfWay, worldViewVector), 0.0, 1.0), startFresnelSchlick);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(normals, worldViewVector), 0.0) * max(dot(normals, processedLightPos), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular = numerator / denominator;

    // kS is equal to Fresnel
    vec3 kS = F;

    vec3 kD = vec3(1.0) - kS;

    kD *= 1.0 - metallic;

    // scale light by NdotL
    float NdotL = max(dot(normals, processedLightPos), 0.0);


    return (kD * albedo / PI + specular) * radiance * NdotL;
}

vec3 processSpotLight(SpotLight spotLight, vec3 normals, vec3 worldViewVector, vec3 startFresnelSchlick, float roughness, float metallic, vec3 albedo){
    vec3 lightToPixel = normalize(fragmentPosition - spotLight.position);
    float spotFactor = dot(lightToPixel, spotLight.direction);

    if(spotFactor>spotLight.cutOff){
        vec3 processedLightPos = normalize(spotLight.position - fragmentPosition);
        vec3 halfWay = normalize(worldViewVector + processedLightPos);
        float distance = length(spotLight.position - fragmentPosition);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = spotLight.color * spotLight.intensity * attenuation;

        float NDF = DistributionGGX(normals, halfWay, roughness);
        float G   = GeometrySmith(normals, worldViewVector, processedLightPos, roughness);
        vec3 F    = fresnelSchlick(clamp(dot(halfWay, worldViewVector), 0.0, 1.0), startFresnelSchlick);

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(normals, worldViewVector), 0.0) * max(dot(normals, processedLightPos), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
        vec3 specular = numerator / denominator;

        // kS is equal to Fresnel
        vec3 kS = F;

        vec3 kD = vec3(1.0) - kS;

        kD *= 1.0 - metallic;

        // scale light by NdotL
        float NdotL = max(dot(normals, processedLightPos), 0.0);

        vec3 result = (kD * albedo / PI + specular) * radiance * NdotL;

        float spotLightIntensity = abs((1.0 - (1.0 - spotFactor)/(1.0 - spotLight.cutOff)));

        return result*spotLightIntensity;
    }
    return vec3(0);
}

void main()
{
    vec3 albedo = pow(texture(albedoMap, UvsCoords).rgb, vec3(2.2));
    float metallic = texture(metallicMap, UvsCoords).r;
    float roughness = texture(roughnessMap, UvsCoords).r;
    float ao = texture(aoMap, UvsCoords).r;
    vec3 processedNormals = normalize(getNormalFromMap(UvsCoords, Normals, fragmentPosition));
    vec3 worldViewVector = normalize(cameraPosition - fragmentPosition);

    // calculate reflectance at normal incidence; if dia-electric (like plastic) use F0
    // of 0.04 and if it's a metal, use the albedo color as F0 (metallic workflow)
    vec3 startFresnelSchlick = vec3(0.04);
    startFresnelSchlick = mix(startFresnelSchlick, albedo, metallic);

    // reflectance equation
    vec3 Lo = vec3(0.0);

    for(int i = 0; i<enabledDirectionalLights; i++){
        Lo+=processDirectionaLight(directLights[i], processedNormals, worldViewVector, startFresnelSchlick, roughness, metallic, albedo);
    }

    for(int i = 0; i<enabledPointLights; i++){
        Lo+=processPointLight(pointLights[i], processedNormals, worldViewVector, startFresnelSchlick, roughness, metallic, albedo);
    }

    for(int i = 0; i<enabledSpotLights; i++){
        Lo+=processSpotLight(spotLights[i], processedNormals, worldViewVector, startFresnelSchlick, roughness, metallic, albedo);
    }


    vec3 ambient = vec3(0.03) * albedo * ao;

    vec3 color = ambient + Lo;

    // HDR tonemapping
    color = color / (color + vec3(1.0));
    // gamma correct
    color = pow(color, vec3(1.0/2.2));

    FragColor = vec4(color, 1.0);
}