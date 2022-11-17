#version 330 core

#define LIGHT_BLOCKS_AMOUNT 60

struct LightInfo{
    vec3 position;
    vec3 color;
    float specularShininess;
    float ambientStrength;
    float specularStrength;
};

uniform vec3 cameraPosition;


uniform LightInfo lights[LIGHT_BLOCKS_AMOUNT];

uniform int activatedLightBlocks;

in vec3 Normals;
in vec3 fragmentPosition;

vec3 processLight(LightInfo lightInfo, vec3 viewPos, vec3 objectColor, vec3 Normal, vec3 FragPos);

vec3 processAllLights(vec3 objectColor){
    for(int counter = 0; counter<activatedLightBlocks; counter++){
        objectColor = processLight(lights[counter], cameraPosition, objectColor, Normals, fragmentPosition);
    }
    return objectColor;
}


vec3 processLight(LightInfo lightInfo, vec3 viewPos, vec3 objectColor, vec3 Normal, vec3 FragPos){
    float ambientStrength = lightInfo.ambientStrength;
    vec3 ambient = ambientStrength * lightInfo.color;

    // diffuse
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightInfo.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightInfo.color;

    // specular
    float specularStrength = lightInfo.specularStrength;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), lightInfo.specularShininess);
    vec3 specular = specularStrength * spec * lightInfo.color;

    vec3 result = (ambient + diffuse + specular) * objectColor;
    return result;
}