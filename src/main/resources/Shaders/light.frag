#version 330 core

#define LIGHT_BLOCKS_AMOUNT 30

in vec3 WorldPos0;
in vec3 Normals;

struct BaseLight
{
	vec3 Color;
	float AmbientIntensity;
	float DiffuseIntensity;
	float specularPower;
	float specularIntensity;
};

struct DirectionalLight
{
	vec3 Color;
	float AmbientIntensity;
	float DiffuseIntensity;
	vec3 Direction;
	float specularPower;
	float specularIntensity;
};

struct PointLight
{
	vec3 Color;
	float AmbientIntensity;
	float DiffuseIntensity;
	vec3 Position;
	float Constant;
	float Linear;
	float Exp;
	float specularPower;
	float specularIntensity;
};

struct SpotLight
{
	vec3 Color;
	float AmbientIntensity;
	float DiffuseIntensity;
	vec3 Position;
	float Constant;
	float Linear;
	float Exp;
	vec3 Direction;
	float Cutoff;
	float specularPower;
	float specularIntensity;
};

uniform int enabledDirectionalLights;
uniform int enabledPointLights;
uniform int enabledSpotLights;

uniform vec3 cameraPosition;

uniform DirectionalLight directionalLights[LIGHT_BLOCKS_AMOUNT];
uniform PointLight pointLights[LIGHT_BLOCKS_AMOUNT];
uniform SpotLight spotLights[LIGHT_BLOCKS_AMOUNT];

vec4 CalcDirectionalLight(DirectionalLight directionalLight, vec3 Normal);
vec4 CalcPointLight(PointLight l, vec3 Normal);
vec4 CalcSpotLight(SpotLight l, vec3 Normal);

vec4 loadLights(){
	vec3 Normal = normalize(Normals);
	vec4 light;
	bool lightEmpty = true;
	for(int i = 0; i<enabledDirectionalLights; i++){
		if(lightEmpty){
			light = CalcDirectionalLight(directionalLights[i], Normal);
			lightEmpty = false;
		}
		else{
			light+=CalcDirectionalLight(directionalLights[i], Normal);
		}
	}
	for(int i = 0; i<enabledPointLights; i++){
		if(lightEmpty){
			light = CalcPointLight(pointLights[i], Normal);
			lightEmpty = false;
		}
		else{
			light+=CalcPointLight(pointLights[i], Normal);
		}
	}
	for(int i = 0; i<enabledSpotLights; i++){
		if(lightEmpty){
			light = CalcSpotLight(spotLights[i], Normal);
			lightEmpty = false;
		}
		else{
			light+=CalcSpotLight(spotLights[i], Normal);
		}
	}
	return light;
}

vec4 CalcLightInternal(BaseLight Light, vec3 LightDirection, vec3 Normal)
{
	vec4 AmbientColor = vec4(Light.Color * Light.AmbientIntensity, 1.0f);
	float DiffuseFactor = dot(Normal, -LightDirection);

	vec4 DiffuseColor = vec4(0, 0, 0, 0);
	vec4 SpecularColor = vec4(0, 0, 0, 0);

	if (DiffuseFactor > 0) {
		DiffuseColor = vec4(Light.Color * Light.DiffuseIntensity * DiffuseFactor, 1.0f);

		vec3 VertexToEye = normalize(cameraPosition - WorldPos0);
		vec3 LightReflect = normalize(reflect(LightDirection, Normal));
		float SpecularFactor = dot(VertexToEye, LightReflect);
		if (SpecularFactor > 0) {
			SpecularFactor = pow(SpecularFactor, Light.specularPower);
			SpecularColor = vec4(Light.Color * Light.specularIntensity * SpecularFactor, 1.0f);
		}
	}

	return (AmbientColor + DiffuseColor + SpecularColor);
}

vec4 CalcDirectionalLight(DirectionalLight directionalLight, vec3 Normal)
{
	BaseLight baseLight;
	baseLight.specularIntensity = directionalLight.specularIntensity;
	baseLight.specularPower = directionalLight.specularPower;
	baseLight.AmbientIntensity = directionalLight.AmbientIntensity;
	baseLight.DiffuseIntensity = directionalLight.DiffuseIntensity;
	baseLight.Color = directionalLight.Color;
	return CalcLightInternal(baseLight, directionalLight.Direction, Normal);
}

vec4 CalcPointLight(PointLight l, vec3 Normal)
{
	vec3 LightDirection = WorldPos0 - l.Position;
	float Distance = length(LightDirection);
	LightDirection = normalize(LightDirection);
	BaseLight baseLight;
	baseLight.specularIntensity = l.specularIntensity;
	baseLight.specularPower = l.specularPower;
	baseLight.AmbientIntensity = l.AmbientIntensity;
	baseLight.DiffuseIntensity = l.DiffuseIntensity;
	baseLight.Color = l.Color;
	vec4 Color = CalcLightInternal(baseLight, LightDirection, Normal);
	float AttenuationFactor =  l.Constant +
	l.Linear * Distance +
	l.Exp * Distance * Distance;

	return Color / AttenuationFactor;
}

vec4 CalcSpotLight(SpotLight l, vec3 Normal)
{

	PointLight pointLight;
	pointLight.Color = l.Color;
	pointLight.DiffuseIntensity = l.DiffuseIntensity;
	pointLight.AmbientIntensity = l.AmbientIntensity;
	pointLight.specularPower = l.specularPower;
	pointLight.specularIntensity = l.specularIntensity;
	pointLight.Constant = l.Constant;
	pointLight.Exp = l.Exp;
	pointLight.Linear = l.Linear;
	pointLight.Position = l.Position;
	vec3 LightToPixel = normalize(WorldPos0 - l.Position);
	float SpotFactor = dot(LightToPixel, l.Direction);

	if (SpotFactor > l.Cutoff) {
		vec4 Color = CalcPointLight(pointLight, Normal);
		return Color * (1.0 - (1.0 - SpotFactor) * 1.0/(1.0 - l.Cutoff));
	}
	else {
		return vec4(0,0,0,0);
	}
}