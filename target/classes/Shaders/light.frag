#version 330 core

#define LIGHT_BLOCKS_AMOUNT 256


uniform vec3 cameraPosition;
uniform vec3 lights[LIGHT_BLOCKS_AMOUNT];
uniform vec3 lightColors[LIGHT_BLOCKS_AMOUNT];
uniform int activatedLightBlocks;

in vec3 Normals;
in vec3 fragmentPosition;

vec3 processLight(vec3 lightPos, vec3 viewPos, vec3 lightColor, vec3 objectColor, vec3 Normal, vec3 FragPos);

vec3 processAllLights(vec3 objectColor){
    for(int counter = 0; counter<activatedLightBlocks; counter++){
        objectColor = processLight(lights[counter], cameraPosition, lightColors[counter], objectColor, Normals, fragmentPosition);
    }
    return objectColor;
}


vec3 processLight(vec3 lightPos, vec3 viewPos, vec3 lightColor, vec3 objectColor, vec3 Normal, vec3 FragPos){
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    // diffuse
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    // specular
    float specularStrength = 0.5;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (ambient + diffuse + specular) * objectColor;
    return result;
}