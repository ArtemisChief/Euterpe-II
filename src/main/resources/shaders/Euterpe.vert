#version 330

#define WHITE   200
#define BLACK   201

layout (location = 0) in vec2 position;

uniform int trackID;
uniform int colorID;
uniform float scaleY;
uniform float offsetY;

uniform mat4 proj;

out vec3 fragColor;

vec3 hsvToRgb(vec3 hsv){
    float h = hsv.x, s = hsv.y, v = hsv.z;
    float r = 0, g = 0, b = 0;
    int i = int(h) / 60 % 6;
    float f = (h / 60) - i;
    float p = v * (1 - s);
    float q = v * (1 - f * s);
    float t = v * (1 - (1 - f) * s);

    switch (i) {
        case 0:
        r = v;
        g = t;
        b = p;
        break;
        case 1:
        r = q;
        g = v;
        b = p;
        break;
        case 2:
        r = p;
        g = v;
        b = t;
        break;
        case 3:
        r = p;
        g = q;
        b = v;
        break;
        case 4:
        r = t;
        g = p;
        b = v;
        break;
        case 5:
        r = v;
        g = p;
        b = q;
        break;
        default :
        break;
    }

    return vec3(r, g, b);
}

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

float getPosZ(int trackID){
    float posZ;

    switch (trackID % 12) {
        case 1:
        case 4:
        case 6:
        case 9:
        case 11:
        posZ = 1.0f;
        break;
        default:
        posZ = 0.5f;
        break;
    }

    return posZ;
}

vec3 getColor(int colorID){
    vec3 color;

    switch (colorID){
        case WHITE:
        color = vec3(0.98f, 0.98f, 0.98f);
        break;

        case BLACK:
        color = vec3(0.07f, 0.07f, 0.07f);
        break;

        default :
        float s = 0.5f;
        float v = 1.0f;

        if(colorID > 100){
            colorID -= 100;
            s = 0.1f;
        }

        int h = (colorID * 5 + 330) % 360;

        color =  hsvToRgb(vec3(h, s, v));
        break;
    }

    return color;
}

void main() {

    gl_Position = proj * vec4(position.x + getOffsetX(trackID), position.y * scaleY + offsetY - 33.0f, getPosZ(trackID), 1);

    fragColor = getColor(colorID);

}



