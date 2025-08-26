#version 300 es
//precision highp float;
precision mediump float;
uniform sampler2D img2;
in vec2 texturePosition2;
out vec4 fragColor;
void main() {
    fragColor = texture(img2, texturePosition2);
}