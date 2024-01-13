#version 300 es
precision mediump float;

uniform sampler2D texture;
in vec2 oTexturePosition;
out vec4 fragColor;

void main(){
    fragColor = texture(texture, oTexturePosition);
}