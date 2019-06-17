#version 330 core

// Interpolated values from the vertex shaders
in vec2 texCoord;
in vec3 worldPos;
in vec3 normal;

// Output data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2D texSample;
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform float lightEnabled;

vec3 calculateAmbient() {
	float ambStrength = 0.5f;
	return ambStrength * lightColor;
}

vec3 calculateDiffuse() {
	vec3 unitNor = normalize(normal);
	vec3 lightDir = normalize(lightPos - worldPos);
	float diff = max(dot(unitNor, lightDir), 0);
	vec3 diffuse = diff * lightColor;
	return diffuse;
}

vec4 getColor() {
	// Output color = color of the texture at the specified UV
	// TODO deal with alpha for lighting purposes :)
	vec4 objColor = texture(texSample, texCoord);
	if (lightEnabled < 0.5) {
		return objColor;
	}
	float alpha = objColor.w;
	vec3 opColor = vec3(objColor);

	vec3 ambient = calculateAmbient();
	vec3 diffuse = calculateDiffuse();

	// reduce lighting color by alpha to simulate pass-through
	vec3 finalLight = (ambient + diffuse) * alpha;

	vec3 coreColor = finalLight * opColor;

	return vec4(coreColor, alpha);
}

void main() {
	color = getColor();
}
