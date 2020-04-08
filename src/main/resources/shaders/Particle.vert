#version 330 core

layout (location = 0) in vec2 position;

uniform int trackID;
uniform vec2 offset;
uniform float scale;
uniform float degrees;

uniform mat4 proj;

float getOffsetX(const int trackID) {
    float width = 2.2f;
    float gap = 0.13f;

    float offsetX = (-52 / 2 + trackID / 12 * 7) * (width + gap) + (width - gap) / 2 + gap;

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

    vec2 scalePos = scale * position;
    float angle = radians(degrees);
    vec2 rotationPos = vec2(cos(angle) * scalePos.x - sin(angle) * scalePos.y, sin(angle) * scalePos.x + cos(angle) * scalePos.y);

    gl_Position = proj * vec4(rotationPos + offset + vec2(getOffsetX(trackID), -24.9f), 1, 1);

}
