#version 330 core

#define LIGHT_BLOCKS_AMOUNT 53

struct PointLightInfo{
	vec3 lightPosition;
	vec4 lightColor;
	float a;
	float b;
	float ambientIntensity;
	float specularIntensity;
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
struct LightReturn{
	vec4 baseColor;
	float specularColor;
	vec4 lightColor;
};
uniform vec3 cameraPosition;
uniform PointLightInfo pointLights[LIGHT_BLOCKS_AMOUNT];
uniform DirectLightInfo directLights[LIGHT_BLOCKS_AMOUNT];
uniform SpotLightInfo spotLights[LIGHT_BLOCKS_AMOUNT];

uniform int activatedPointLights;
uniform int activatedSpotLights;
uniform int activatedDirectLgihts;

in vec3 Normals;
in vec3 fragmentPosition;

LightReturn pointLight(PointLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo);
LightReturn directLight(DirectLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo);
LightReturn spotLight(SpotLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo);

vec4 processAllLights(vec3 primitivePosition, sampler2D baseColorMap, sampler2D specularLight, vec2 texCoord){
	vec4 objectColor = texture(baseColorMap, texCoord);
	float specularInfo = texture(specularLight, texCoord).r;
	vec4 lightColor = vec4(1.0, 1.0, 1.0, 1.0);
	bool lightColorEmpty = true;
	for(int counter = 0; counter<activatedPointLights; counter++){
		LightReturn pointLightReturn = pointLight(pointLights[counter], primitivePosition, Normals, objectColor, specularInfo);
		objectColor = pointLightReturn.baseColor;
		specularInfo = pointLightReturn.specularColor;
		if(lightColorEmpty){
			lightColor=pointLightReturn.lightColor;
		}
		else{
			lightColor*=pointLightReturn.lightColor;
		}
	}
	for(int counter = 0; counter<activatedDirectLgihts; counter++){
		LightReturn lightReturn = directLight(directLights[counter], primitivePosition, Normals, objectColor, specularInfo);
		objectColor = lightReturn.baseColor;
		specularInfo = lightReturn.specularColor;
		lightColor*=lightReturn.lightColor;
	}
	for(int counter = 0; counter<activatedSpotLights; counter++){
		LightReturn lightReturn = spotLight(spotLights[counter], primitivePosition, Normals, objectColor, specularInfo);
		objectColor = lightReturn.baseColor;
		specularInfo = lightReturn.specularColor;
		lightColor*=lightReturn.lightColor;
	}
    return (objectColor+specularInfo)*lightColor;
}


LightReturn pointLight(PointLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo)
{
	// used in two variables so I calculate it here to not have to do it twice
	vec3 lightVec = info.lightPosition - primitivePosition;

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
	float specularLight = info.specularIntensity;
	vec3 viewDirection = normalize(cameraPosition - primitivePosition);
	vec3 reflectionDirection = reflect(-lightDirection, normal);
	float specAmount = pow(max(dot(viewDirection, reflectionDirection), 0.0f), info.shininess);
	float specular = specAmount * specularLight;
	LightReturn pointLightReturn;
	pointLightReturn.lightColor = info.lightColor;
	pointLightReturn.baseColor = baseColor * (diffuse * inten + ambient);
	pointLightReturn.specularColor = specularInfo * specular * inten;

	return pointLightReturn;
}

LightReturn directLight(DirectLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo)
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
	LightReturn lightReturn;
	lightReturn.lightColor = info.color;
	lightReturn.baseColor = baseColor*(diffuse+ambient);
	lightReturn.specularColor = specularInfo*specular;
	return lightReturn;
}

LightReturn spotLight(SpotLightInfo info, vec3 primitivePosition, vec3 Normal, vec4 baseColor, float specularInfo)
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
	LightReturn lightReturn;
	lightReturn.lightColor = info.color;
	lightReturn.specularColor = specularInfo*specular*inten;
	lightReturn.baseColor = baseColor*(diffuse * inten + ambient);
	return lightReturn;
}