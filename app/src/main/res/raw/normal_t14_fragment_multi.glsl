#version 300 es
precision mediump float;

uniform sampler2D texture;
in vec2 otexturePosition;
layout(location = 0) out vec4 fragColor1;
layout(location = 1) out vec4 fragColor2;
layout(location = 2) out vec4 fragColor3;

void main(){
    vec4 color = texture(texture, otexturePosition);
    fragColor1 = vec4(1.0, color.g, color.b, color.a);
    fragColor2 = vec4(color.r, 1.0, color.b, color.a);
    fragColor3 = vec4(color.r, color.g, 1.0, color.a);
}