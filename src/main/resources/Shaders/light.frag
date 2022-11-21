#version 330 core

#define LIGHT_BLOCKS_AMOUNT 30

in vec3 WorldPos0;
in vec3 Normals;

struct DirectionalLight
{
	vec4 color;
	float ambientIntensity;
	float diffuseIntensity;
	float specularPower;
	float specularIntensity;
	vec3 direction;
};

struct PointLight{
	vec4 color;
	float ambientIntensity;
	float diffuseIntensity;
	float specularPower;
	float specularIntensity;
	vec3 position;
	float constant;
	float linear;
	float exp;
};
struct SpotLight{
	vec4 color;
	float ambientIntensity;
	float diffuseIntensity;
	float specularPower;
	float specularIntensity;
	vec3 position;
	float constant;
	float linear;
	float exp;
	vec3 direction;
	float cutOff;
};

uniform int enabledDirectionalLights;
uniform int enabledPointLights;
uniform int enabledSpotLights;

uniform vec3 cameraPosition;

uniform DirectionalLight directionalLights[LIGHT_BLOCKS_AMOUNT];
uniform PointLight pointLights[LIGHT_BLOCKS_AMOUNT];
uniform SpotLight spotLights[LIGHT_BLOCKS_AMOUNT];


vec4 loadDirectionalLight(DirectionalLight directionalLight);
vec4 loadPointLight(PointLight pointLight);
vec4 loadSpotLight(SpotLight spotLight);

vec4 loadLights(){
	vec3 Normals = normalize(Normals);
	vec4 color;
	bool isColorEmpty = true;
	for(int i = 0; i<enabledDirectionalLights; i++){
		if(isColorEmpty){
			color = loadDirectionalLight(directionalLights[i]);
			isColorEmpty = false;
		}
		else{
			color+=loadDirectionalLight(directionalLights[i]);
		}
	}
	for(int i = 0; i<enabledPointLights; i++){
		if(isColorEmpty){
			color = loadPointLight(pointLights[i]);
			isColorEmpty = false;
		}
		else{
			color+=loadPointLight(pointLights[i]);
		}
	}
	for(int i = 0; i<enabledSpotLights; i++){
		if(isColorEmpty){
			color = loadSpotLight(spotLights[i]);
			isColorEmpty = false;
		}
		else{
			color+=loadSpotLight(spotLights[i]);
		}
	}

	return color;
}

vec4 loadBaseLight(DirectionalLight directionalLight){
	vec4 ambientColor = directionalLight.color *
	directionalLight.ambientIntensity;
	float diffuseFactor = dot(normalize(Normals), -directionalLight.direction);

	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);
	if (diffuseFactor > 0) {
		vec3 vertexToEye = normalize(cameraPosition-WorldPos0);
		diffuseColor = directionalLight.color *
		directionalLight.diffuseIntensity *
		diffuseFactor;
		diffuseColor.w = 1;
		vec3 lightReflect = normalize(reflect(directionalLight.direction, Normals));
		float specularFactor = dot(vertexToEye, lightReflect);
		if(specularFactor>0){
			specularFactor = pow(specularFactor, directionalLight.specularPower);
			specularColor = directionalLight.color * directionalLight.specularIntensity * specularFactor;
			specularColor.w = 1;
		}

	}
	else {
		diffuseColor = vec4(0, 0, 0, 0);
	}
	return (ambientColor+diffuseColor+specularColor);
}

vec4 loadBaseLight(PointLight light, vec3 direction){
	vec4 ambientColor = light.color *
	light.ambientIntensity;
	float diffuseFactor = dot(normalize(Normals), -direction);

	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);
	if (diffuseFactor > 0) {
		vec3 vertexToEye = normalize(cameraPosition-WorldPos0);
		diffuseColor = light.color *
		light.diffuseIntensity *
		diffuseFactor;
		diffuseColor.w = 1;
		vec3 lightReflect = normalize(reflect(direction, Normals));
		float specularFactor = dot(vertexToEye, lightReflect);
		if(specularFactor>0){
			specularFactor = pow(specularFactor, light.specularPower);
			specularColor = light.color * light.specularIntensity * specularFactor;
			specularColor.w = 1;
		}

	}
	else {
		diffuseColor = vec4(0, 0, 0, 0);
	}
	return (ambientColor+diffuseColor+specularColor);
}

vec4 loadBaseLight(SpotLight light, vec3 direction){
	vec4 ambientColor = light.color *
	light.ambientIntensity;
	float diffuseFactor = dot(normalize(Normals), -direction);

	vec4 diffuseColor = vec4(0, 0, 0, 0);
	vec4 specularColor = vec4(0, 0, 0, 0);
	if (diffuseFactor > 0) {
		vec3 vertexToEye = normalize(cameraPosition-WorldPos0);
		diffuseColor = light.color *
		light.diffuseIntensity *
		diffuseFactor;
		diffuseColor.w = 1;
		vec3 lightReflect = normalize(reflect(direction, Normals));
		float specularFactor = dot(vertexToEye, lightReflect);
		if(specularFactor>0){
			specularFactor = pow(specularFactor, light.specularPower);
			specularColor = light.color * light.specularIntensity * specularFactor;
			specularColor.w = 1;
		}

	}
	else {
		diffuseColor = vec4(0, 0, 0, 0);
	}
	return (ambientColor+diffuseColor+specularColor);
}

vec4 loadDirectionalLight(DirectionalLight directionalLight){
	return loadBaseLight(directionalLight);
}

vec4 loadPointLight(PointLight pointLight){
	vec3 lightDirection = cameraPosition-pointLight.position;
	float lightDistance = length(lightDirection);
	lightDirection = normalize(lightDirection);
	vec4 baseColor = loadBaseLight(pointLight, lightDirection);
	float attenuationFactor = pointLight.constant+pointLight.linear*lightDistance+pointLight.exp*lightDistance*lightDistance;
	return baseColor/attenuationFactor;
}
vec4 loadPointLight(SpotLight pointLight){
	vec3 lightDirection = cameraPosition-pointLight.position;
	float lightDistance = length(lightDirection);
	lightDirection = normalize(lightDirection);
	vec4 baseColor = loadBaseLight(pointLight, lightDirection);
	float attenuationFactor = pointLight.constant+pointLight.linear*lightDistance+pointLight.exp*lightDistance*lightDistance;
	return baseColor/attenuationFactor;
}

vec4 loadSpotLight(SpotLight spotLight){
	vec3 lightToPixel = normalize(WorldPos0 - spotLight.position);
	float spotFactor = dot(lightToPixel, spotLight.direction);
	if(spotFactor>spotLight.cutOff){
		vec4 color = loadPointLight(spotLight);
		return color*(1.0 - (1.0 - spotFactor) * 1.0/(1.0 - spotLight.cutOff));
	}
	else{
		return vec4(0, 0, 0, 0);
	}

}