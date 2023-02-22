#version 300 es

in vec4 aPosition;
in vec2 aTextCoord;
out vec2 vTextCoord;
uniform mat4 matrix;

void main(){
    gl_Position = matrix * aPosition;
    vTextCoord = aTextCoord;
}