#version 130

float ambient = 0.65;
in vec3 vertexColor;
in vec3 vertexNormal;
uniform vec3 lightDirection;
out vec4 pixelColor;

void main(){
	if(length(vertexNormal) == 0 || length(lightDirection) == 0)
		pixelColor = vec4(vertexColor.xyz, 1.0);
	else
	    pixelColor = vec4(vertexColor.xyz, 1.0) * (ambient + dot(normalize(vertexNormal) * (1- ambient), -lightDirection));
}