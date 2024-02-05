#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES img;
//uniform sampler2D img;
in vec2 texturePosition;
out vec4 fragColor;

void main() {
    fragColor = texture(img, texturePosition);
}