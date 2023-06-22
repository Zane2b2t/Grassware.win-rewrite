#version 120

uniform sampler2D inTexture, textureToCheck;
uniform vec2 texelSize, direction;
uniform float radius;
uniform float weights[256];
uniform vec3 rgb;
uniform vec3 rgb1;
uniform float step;
uniform float o;
uniform float intensity;

#define offset texelSize * direction

void main() {
    if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) {
        discard;
    }
    float blr = texture2D(inTexture, gl_TexCoord[0].st).a * weights[0];

    for (float f = 1.0; f <= radius; f++) {
        blr += texture2D(inTexture, gl_TexCoord[0].st + f * offset).a * (weights[int(abs(f))]) * intensity;
        blr += texture2D(inTexture, gl_TexCoord[0].st - f * offset).a * (weights[int(abs(f))]) * intensity;
    }

    float alpha =texture2D(textureToCheck, gl_TexCoord[0].xy).a;
    float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + o;

    distance = distance / step;

    distance = ((sin(distance) + 1.0) / 2.0);

    float distanceInv = 1 - distance;
    float r = rgb.r * distance + rgb1.r * distanceInv;
    float g = rgb.g * distance + rgb1.g * distanceInv;
    float b = rgb.b * distance + rgb1.b * distanceInv;


    gl_FragColor = vec4(r, g, b, blr);
}
