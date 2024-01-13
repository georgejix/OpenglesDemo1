#version 300 es

in vec4 vertexPosition;
in vec2 texturePosition;
out vec2 oTexturePosition;
uniform mat4 matrix;

void main(){
    gl_Position = matrix * vertexPosition;
    oTexturePosition = texturePosition;
}