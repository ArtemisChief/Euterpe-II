#version 330

#define WHITE       0
#define BLACK       1
#define WHITE_PRESS 2
#define BLACK_PRESS 3

layout (location = 0) in vec2 position;

uniform int trackID;
uniform int colorID;
uniform float scaleY;
uniform float offsetY;

uniform mat4 proj;

out vec3 fragColor;

float getOffsetX(const int trackID) {

    float width = 2.2f;
    float gap = 0.13f;

    float offsetX = (-52 / 2 + trackID / 12 * 7) * (width + gap) + (width - gap) / 2;

    int tone = trackID % 12;

    switch (tone){
        case 0:
        break;
        case 1:
        offsetX += width / 2;
        break;
        case 2:
        offsetX += width + gap;
        break;
        case 3:
        offsetX += (width + gap) * 2;
        break;
        case 4:
        offsetX += width / 2 + 2 * (width + gap);
        break;
        case 5:
        offsetX += (width + gap) * 3;
        break;
        case 6:
        offsetX += width / 2 + 3 * (width + gap);
        break;
        case 7:
        offsetX += (width + gap) * 4;
        break;
        case 8:
        offsetX += (width + gap) * 5;
        break;
        case 9:
        offsetX += width / 2 + 5 * (width + gap);
        break;
        case 10:
        offsetX += (width + gap) * 6;
        break;
        case 11:
        offsetX += width / 2 + 6 * (width + gap);
        break;
    }

    return offsetX;
}

void main() {

    float posZ;

    switch (colorID){
        case WHITE:
        fragColor = vec3(0.94f, 0.94f, 0.94f);
        posZ = -0.2f;
        break;
        case BLACK:
        fragColor = vec3(0.22f, 0.22f, 0.22f);
        posZ = 0.0f;
        break;
        case WHITE_PRESS:
        fragColor = vec3(0.90f, 0.47f, 0.04f);
        posZ = -0.2f;
        break;
        case BLACK_PRESS:
        fragColor = vec3(0.52f, 0.29f, 0.06f);
        posZ = 0.0f;
        break;
        default :
        fragColor = vec3(1.0f, 0.0f, 0.0f);
        posZ = -0.4f;
        break;
    }

    gl_Position = proj * vec4(position.x + getOffsetX(trackID), position.y * scaleY + offsetY - 33.0f, posZ, 1);

}



