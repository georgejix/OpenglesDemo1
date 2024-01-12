#version 300 es

in vec4 a_Position;
in vec3 aTexCoord;
out vec3 vTexCoord;

void main(){
    gl_Position = a_Position;
    vTexCoord = aTexCoord;
}