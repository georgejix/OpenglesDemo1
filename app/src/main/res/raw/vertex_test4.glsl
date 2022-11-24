#version 300 es

in vec4 a_Position;
in vec2 aTexCoord;
uniform mat4 u_Matrix;
out vec2 vTexCoord;

void main(){
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 10.0;
    vTexCoord = aTexCoord;
}