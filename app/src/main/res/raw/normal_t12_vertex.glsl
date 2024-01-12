#version 300 es
precision mediump float;
layout(location = 0) in vec4 a_Position;
layout(location = 1) in vec2 a_textureCoordinate;
out vec2 v_textureCoordinate;
void main() {
    v_textureCoordinate = a_textureCoordinate;
    gl_Position = a_Position;
}