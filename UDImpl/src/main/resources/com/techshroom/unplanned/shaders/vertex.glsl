#version 330 core

layout(location = 0) in vec3 vertexPos;
layout(location = 1) in vec2 vertexTex;

uniform mat4 matrix;

out vec2 texCoord;

void main(void) {
	// Override gl_Position with our new calculated position
	gl_Position = matrix * vec4(vertexPos, 1);
//	gl_Position = vec4(vertexPos, 1);
	texCoord = vertexTex;
}
