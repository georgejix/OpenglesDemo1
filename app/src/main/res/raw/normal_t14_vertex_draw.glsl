#version 300 es

in vec4 position;
in vec2 texturePosition;
out vec2 otexturePosition;

void main(){
    gl_Position = position;
    otexturePosition = texturePosition;
}