#version 150

uniform sampler2D sampler1;
uniform sampler2D sampler2;
uniform vec2 texelSize;
uniform vec2 direction;
uniform float radius;
uniform float kernel[64];

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    uv.y = 1.0 - uv.y;

    float alpha = texture(sampler2, uv).a;
    if (direction.x == 0.0 && alpha == 0.0) {
        discard;
    }

    vec4 pixel_color = texture(sampler1, uv) * kernel[0];
    for (float f = 1; f <= radius; f++) {
        vec2 offset = f * texelSize * direction;
        pixel_color += texture(sampler1, uv - offset) * kernel[int(f)];
        pixel_color += texture(sampler1, uv + offset) * kernel[int(f)];
    }

    fragColor = vec4(pixel_color.rgb, direction.x == 0.0 ? alpha : 1.0);
}
