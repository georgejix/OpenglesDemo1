#version 300 es

in vec4 aPosition;
in vec4 aTextCoord;
out vec2 vTextCoord;
uniform mat4 matrix;

void main(){
    gl_Position = aPosition;
    gl_PointSize = 10.0;
    vTextCoord = (matrix * aTextCoord).xy;
}