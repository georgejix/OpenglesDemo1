#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

uniform samplerExternalOES img;
in vec2 vTextCoord;
out vec4 fragColor;

void main(){
    fragColor = texture(img, vTextCoord);
}