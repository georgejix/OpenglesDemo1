#version 300 es
precision mediump float;

uniform sampler2D u_textureUnit;
in vec2 vTexCoord;
out vec4 fragColor;

void main(){
    fragColor = texture(u_textureUnit, vTexCoord);
}