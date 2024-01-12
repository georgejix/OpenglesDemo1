#version 300 es
precision mediump float;

uniform sampler2DArray u_textureUnit;
in vec3 vTexCoord;
out vec4 fragColor;

void main(){
    fragColor = texture(u_textureUnit, vTexCoord);
}