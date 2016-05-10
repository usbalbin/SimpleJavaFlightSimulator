#version 130
in vec3 position;
in vec3 normal;
in vec3 color;

out vec3 vertexColor;
out vec3 vertexNormal;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;


void main(){
	vertexColor = color;
	gl_Position = modelViewProjectionMatrix * vec4(position, 1.0);
	vertexNormal = (modelMatrix * vec4(normal, 0.0)).xyz;
}