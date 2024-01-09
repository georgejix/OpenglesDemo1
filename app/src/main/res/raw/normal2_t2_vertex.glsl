#version 300 es

in vec4 a_Position;
in vec4 a_Color;
out vec4 v_Color;
uniform mat4 u_Matrix;

void main(){
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 10.0;
    v_Color = a_Color;
}