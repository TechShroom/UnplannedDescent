#version 330 core

// Interpolated values from the vertex shaders
in vec2 texCoord;

// Output data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2D texSample;

void main() {
    // Output color = color of the texture at the specified UV
    color = texture(texSample, texCoord);
//	color = vec4(texCoord.x, texCoord.y, 0, 1);
}
