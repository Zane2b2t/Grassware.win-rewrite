#version 120

uniform sampler2D texture;
uniform vec3 rgb;
uniform vec3 rgb1;
uniform vec3 rgb2;
uniform float step;
uniform float offset;
uniform float mix;

void main() {
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0f) {
        float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + offset;
        float distance2 = sqrt((gl_FragCoord.x - 800.0) * (gl_FragCoord.x - 800.0) + gl_FragCoord.y * gl_FragCoord.y) + offset;

        distance = distance / step;
        distance2 = distance2 / step;

        distance = ((sin(distance) + 1.0) / 2.0);
        distance2 = ((sin(distance2) + 1.0) / 2.0);

        float distanceInv = 1 - distance;
        float r *= rgb.r * distance + rgb1.r * distanceInv + rgb2.r * distance2;
        float g *= rgb.g * distance + rgb1.g * distanceInv + rgb2.g * distance2;
        float b *= rgb.b * distance + rgb1.b * distanceInv + rgb2.b * distance2;
        gl_FragColor = vec4(r, g, b, mix);
    }
}
