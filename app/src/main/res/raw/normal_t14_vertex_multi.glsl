#version 300 es

in vec4 position;
in vec2 texturePosition;
out vec2 otexturePosition;
uniform mat4 matrix;

void main(){
    gl_Position = matrix * position;
    otexturePosition = texturePosition;
}