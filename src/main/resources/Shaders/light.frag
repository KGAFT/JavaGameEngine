#version 330 core

#define LIGHT_BLOCKS_AMOUNT 60

struct PointLightInfo{
	vec3 lightPosition;
	vec4 lightColor;
	float a;
	float b;
	float ambientIntensity;
	float spelcularIntensity;
	float shininess;
};
struct DirectLightInfo{
	vec4 color;
	vec3 direction;
	float ambientIntensity;
	float specularIntensity;
	float shininess;
};

struct SpotLightInfo{
	vec4 color;
	vec3 lightPosition;
	float outerCone;
	float innerCone;
	float ambientIntensity;
	float specularIntensity;
	float shininess;
};

uniform vec3 cameraPosition;
uniform LightInfo lights[LIGHT_BLOCKS_AMOUNT];

uniform int activatedLightBlocks;

in vec3 Normals;
in vec3 fragmentPosition;



vec3 processAllLights(vec3 objectColor, sampler2D baseColorMap, sampler2D specularLight, vec2 texCoord){
    for(int counter = 0; counter<activatedLightBlocks; counter++){
        objectColor = processLight(lights[counter], cameraPosition, objectColor, Normals, fragmentPosition);
    }
    return objectColor;
}


vec4 pointLight(PointLightInfo info, vec3 primitivePosition, vec3 Normal, sampler2D baseColorMap, sampler2D specularMap, vec2 texCoord)
{
	// used in two variables so I calculate it here to not have to do it twice
	vec3 lightVec = info.lightPosition - crntPos;

	// intensity of light with respect to distance
	float dist = length(lightVec);
	float a = info.a;
	float b = info.b;
	float inten = 1.0f / (a * dist * dist + b * dist + 1.0f);

	// ambient lighting
	float ambient = info.ambientIntensity;

	// diffuse lighting
	vec3 normal = normalize(Normal);
	vec3 lightDirection = normalize(lightVec);
	float diffuse = max(dot(normal, lightDirection), 0.0f);

	// specular lighting
	float specularLight = info.spelcularIntensity;
	vec3 viewDirection = normalize(cameraPosition - primitivePosition);
	vec3 reflectionDirection = reflect(-lightDirection, normal);
	float specAmount = pow(max(dot(viewDirection, reflectionDirection), 0.0f), info.shininess);
	float specular = specAmount * specularLight;

	return (texture(baseColorMap, texCoord) * (diffuse * inten + ambient) + texture(specularMap, texCoord).r * specular * inten) * info.lightColor;
}

vec4 direcLight(DirectLightInfo info, vec3 primitivePosition, vec3 Normal, sampler2D baseColorMap, sampler2D specularMap, vec2 texCoord)
{
	// ambient lighting
	float ambient = info.ambientIntensity;

	// diffuse lighting
	vec3 normal = normalize(Normal);
	vec3 lightDirection = normalize(info.direction);
	float diffuse = max(dot(normal, lightDirection), 0.0f);

	// specular lighting
	float specularLight = info.specularIntensity;
	vec3 viewDirection = normalize(cameraPosition - primitivePosition);
	vec3 reflectionDirection = reflect(-lightDirection, normal);
	float specAmount = pow(max(dot(viewDirection, reflectionDirection), 0.0f), info.shininess);
	float specular = specAmount * specularLight;

	return (texture(baseColorMap, texCoord) * (diffuse + ambient) + texture(specularMap, texCoord).r * specular) * info.color;
}

vec4 spotLight(SpotLightInfo info, vec3 primitivePosition, vec3 Normal, sampler2D baseColorMap, sampler2D specularMap, vec2 texCoord)
{
	// controls how big the area that is lit up is
	float outerCone = info.outerCone;
	float innerCone = info.innerCone;

	// ambient lighting
	float ambient = info.ambientIntensity;

	// diffuse lighting
	vec3 normal = normalize(Normal);
	vec3 lightDirection = normalize(info.lightPosition - primitivePosition);
	float diffuse = max(dot(normal, lightDirection), 0.0f);

	// specular lighting
	float specularLight = info.specularIntensity;
	vec3 viewDirection = normalize(cameraPosition - primitivePosition);
	vec3 reflectionDirection = reflect(-lightDirection, normal);
	float specAmount = pow(max(dot(viewDirection, reflectionDirection), 0.0f), info.shininess);
	float specular = specAmount * specularLight;

	// calculates the intensity of the crntPos based on its angle to the center of the light cone
	float angle = dot(vec3(0.0f, -1.0f, 0.0f), -lightDirection);
	float inten = clamp((angle - outerCone) / (innerCone - outerCone), 0.0f, 1.0f);

	return (texture(baseColorMap, texCoord) * (diffuse * inten + ambient) + texture(specularMap, texCoord).r * specular * inten) * info.color;
}