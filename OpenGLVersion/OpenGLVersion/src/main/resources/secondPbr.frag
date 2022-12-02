#version 330 core

#define PI 3.14159265359
#define LIGHT_BLOCKS_AMOUNT 30

out vec4 FragColor;
in vec3 Normals;
in vec2 UvsCoords;
in vec3 fragmentPosition;

struct Light{
    vec3 position;
    vec4 color;
};


uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;


uniform Light light;
uniform int enabledLightBlocksAmount;
uniform vec3 cameraPosition;

vec3 getNormalFromMap(vec2 TexCoords, vec3 Normal, vec3 WorldPos)
{
    vec3 tangentNormal = texture(normalMap, TexCoords).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(WorldPos);
    vec3 Q2  = dFdy(WorldPos);
    vec2 st1 = dFdx(TexCoords);
    vec2 st2 = dFdy(TexCoords);

    vec3 N   = normalize(Normal);
    vec3 T  = normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

float schlickBackmannGeometryShadowing(float alpha, vec3 normals, vec3 viewOrLight){
    float numerator = max(dot(normals, viewOrLight), 0.0);
    alpha/=2.0;
    float denominator = numerator*(1.0-alpha)+alpha;
    denominator = max(denominator, 0.000000001);
    return numerator/denominator;
}

float smithModel(float alpha, vec3 normals, vec3 worldViewVector, vec3 lightPosition){
    return schlickBackmannGeometryShadowing(alpha, normals, worldViewVector)*schlickBackmannGeometryShadowing(alpha, normals, lightPosition);
}


vec3 fresnelSchlick(vec3 existingSchlick, vec3 worldViewVector, vec3 halfWay){
    return existingSchlick+(vec3(1.0)-existingSchlick)*pow(1-max(dot(worldViewVector, halfWay), 0.0), 5.0);
}

float throwBridgeReitzNormalDistributionGGX(float alpha, vec3 normals, vec3 halfWay){
    float numerator = pow(alpha, 2.0);

    float dotHalfWay = max(dot(normals, halfWay), 0.0);
    float denominator = PI*pow(pow(dotHalfWay, 2.0)*(pow(alpha, 2.0)-1.0)+1.0, 2.0);
    denominator = max(denominator, 0.000000001);
    return numerator/denominator;
}

vec3 PBR(vec3 albedo, vec3 startFresnelSchlick, vec3 worldViewVector, vec3 halfWay, vec3 normals, vec3 lightPosition,vec3 lightColor, float alpha, float metallic){
    vec3 fresnelEffect = fresnelSchlick(startFresnelSchlick, worldViewVector, halfWay);
    vec3 dFresnelEffect = (vec3(1.0)-fresnelEffect)*(1.0-metallic);

    vec3 lambert = albedo/PI;
    vec3 cookTorranceNumerator = throwBridgeReitzNormalDistributionGGX(alpha, normals, halfWay)
    *smithModel(alpha, normals, worldViewVector, lightPosition)
    *fresnelEffect;
    float cookTorranceDenominator = 4.0*max(dot(worldViewVector, normals), 0.0)*max(dot(lightPosition, normals), 0.0);

    vec3 cookTorrance = cookTorranceNumerator/cookTorranceDenominator;

    vec3 BRDF = dFresnelEffect*lambert+cookTorrance;
    vec3 result = BRDF*lightColor*max(dot(lightPosition, normals), 0.0);
    return result;
}

void main() {
    vec3 albedo = texture(albedoMap, UvsCoords).rgb;
    vec3 normals = getNormalFromMap(UvsCoords, Normals, fragmentPosition);
    float metallic = 0.7f;
    float roughness = 0.5f;
    vec3 startFresnelSchlick = albedo;
    startFresnelSchlick = startFresnelSchlick+(1-startFresnelSchlick);
    vec3 worldViewVector = normalize(cameraPosition-fragmentPosition);
    vec3 halfWay = normalize(worldViewVector+normalize(light.position));
    FragColor = vec4(PBR(albedo, startFresnelSchlick, worldViewVector, halfWay, normals, normalize(light.position), light.color.rgb, light.color.a, metallic), light.color.a);
}
