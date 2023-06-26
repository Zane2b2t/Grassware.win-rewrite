uniform sampler2D texture;
uniform vec3 rgb;
uniform vec3 rgb1;
uniform float step;
uniform float offset;
uniform float mix;

void main() {
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0f) {
        float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + offset;

        distance = distance / step;

        distance = ((sin(distance) + 1.0) / 2.0);

        float distanceInv = 1 - distance;
        float r = rgb.r * distance + rgb1.r * distanceInv;
        float g = rgb.g * distance + rgb1.g * distanceInv;
        float b = rgb.b * distance + rgb1.b * distanceInv;

        // Blur effect
        vec4 sum = vec4(0.0);
        int blurRadius = 5;
        for (int i = -blurRadius; i < blurRadius; i++) {
            for (int j = -blurRadius; j < blurRadius; j++) {
                vec2 offset = vec2(float(i), float(j));
                sum += texture2D(texture, gl_TexCoord[0].xy + offset / step);
            }
        }
        gl_FragColor = (sum / (float(blurRadius) * 2.0 + 1.0)) * mix + vec4(r, g, b, mix);
    }
}
