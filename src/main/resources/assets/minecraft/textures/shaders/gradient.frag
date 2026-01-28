#version 120

uniform sampler2D texture;
uniform vec3 rgb;
uniform vec3 rgb1;
uniform vec3 rgb2;
uniform vec3 rgb3;
uniform float step;
uniform float offset;
uniform float mix;

void main() {
    float mask = texture2D(texture, gl_TexCoord[0].xy).a;
    if (mask == 0.0) discard;

    float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + offset;
    float distance2 = sqrt((gl_FragCoord.x - 1920.0) * (gl_FragCoord.x - 1920.0) + gl_FragCoord.y * gl_FragCoord.y) + offset;
    float distance3 = sqrt((gl_FragCoord.x - 1080.0) * (gl_FragCoord.x - 1080.0) + (gl_FragCoord.y - 1080.0) * (gl_FragCoord.y - 1080.0)) + offset;

    distance = sin(distance / step + sin(gl_FragCoord.y / step)) * 0.5 + 0.5;
    distance2 = sin(distance2 / step + sin(gl_FragCoord.x / step)) * 0.5 + 0.5;
    distance3 = sin(distance3 / step + sin(gl_FragCoord.y / step + gl_FragCoord.x / step)) * 0.5 + 0.5;

    float ripple = sin(distance * 10.0 + step * 10000.0) * 0.05;
    float swirl = cos((gl_FragCoord.x - 960.0) * 0.05 + (gl_FragCoord.y - 540.0) * 0.05 + step) * 0.05;

    distance += ripple + swirl;
    distance2 += ripple - swirl;
    distance3 += ripple + swirl;

    float inv = 1.0 - distance;

    float r = rgb.r  * distance + rgb1.r * inv + rgb2.r * distance2 + rgb3.r * distance3;
    float g = rgb.g  * distance + rgb1.g * inv + rgb2.g * distance2 + rgb3.g * distance3;
    float b = rgb.b  * distance + rgb1.b * inv + rgb2.b * distance2 + rgb3.b * distance3;

    float finalAlpha = mix * gl_Color.a * mask;

    gl_FragColor = vec4(r, g, b, finalAlpha);
}
