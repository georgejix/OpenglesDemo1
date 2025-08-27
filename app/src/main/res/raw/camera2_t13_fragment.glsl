#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
//precision highp float;
precision mediump float;
uniform samplerExternalOES img;
uniform sampler2D topImg;
in vec2 texturePosition;
//将vertexPosition透传
in vec2 vertexPosition;
out vec4 fragColor;
vec2 leftBottom = vec2(-1.0,0.86);
vec2 rightTop = vec2(1.0,1.0);
void main() {
    if(vertexPosition.x >= leftBottom.x && vertexPosition.x <= rightTop.x &&
        vertexPosition.y >= leftBottom.y && vertexPosition.y <= rightTop.y){
        vec2 tex0 = vec2((vertexPosition.x-leftBottom.x)/(rightTop.x-leftBottom.x),
             1.0-(vertexPosition.y-leftBottom.y)/(rightTop.y-leftBottom.y));
        vec4 color = texture(topImg, tex0);
        if(0.0 == color.r && 0.0 == color.g && 0.0 == color.b){
            fragColor = mix(color,texture(img, texturePosition),color.a);
        }else{
            fragColor = color;
        }
    }else{
        fragColor = texture(img, texturePosition);
    }
}