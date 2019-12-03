#version 330 core

#define WHITE   200
#define BLACK   201

uniform int colorID;

vec4 hsvToRgb(vec3 hsv){
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

    return vec4(r, g, b, 1.0f);
}

vec4 getColor(int colorID){
    vec4 color;

    switch (colorID){
        case WHITE:
        color = vec4(0.98f, 0.98f, 0.98f, 1.0f);
        break;

        case BLACK:
        color = vec4(0.07f, 0.07f, 0.07f, 1.0f);
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

    gl_FragColor = getColor(colorID);

}
