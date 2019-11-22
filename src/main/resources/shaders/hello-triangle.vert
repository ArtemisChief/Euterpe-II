#version 330

layout (location = 0) in vec2 position;
layout (location = 1) in vec3 color;

uniform GlobalMatrices
{
    mat4 view;
    mat4 proj;
};

uniform mat4 model;

out vec3 fragColor;

void main() {

    gl_Position = proj * (view * (model * vec4(position, 0, 1)));

    fragColor = color;

}
