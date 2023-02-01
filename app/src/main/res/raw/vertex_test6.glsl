#version 300 es

in vec4 a_Position;
in vec2 texturePoint;
uniform mat4 u_Matrix;
out vec2 point;

void main(){
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 10.0;
    point = texturePoint;
}