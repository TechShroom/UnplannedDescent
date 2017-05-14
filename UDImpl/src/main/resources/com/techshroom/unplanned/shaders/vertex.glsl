#version 330 core

layout(location = 0) in vec3 vertexPos;
layout(location = 1) in vec2 vertexTex;
layout(location = 2) in vec3 vertexNor;

uniform mat4 mvpMat;
uniform mat4 modelMat;
uniform mat3 normalMat;

out vec2 texCoord;
out vec3 worldPos;
out vec3 normal;

void main(void) {
	vec4 pos = vec4(vertexPos, 1);
	gl_Position = mvpMat * pos;

	// pass-through vars
	worldPos = vec3(modelMat * pos);
	texCoord = vertexTex;
	normal = vec3(normalMat * vertexNor);
}
