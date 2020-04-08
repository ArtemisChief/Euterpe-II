#version 330 core

uniform int colorID;
uniform float life;

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

vec4 getColor(int colorID, float life){
    vec4 color;

    float s = 0.8f;
    float v = 1.0f;

    int h = (colorID * 5) % 360;

    color =  vec4(hsvToRgb(vec3(h, s, v)), sqrt(life));

    return color;
}

void main() {

    gl_FragColor = getColor(colorID, life);

}
