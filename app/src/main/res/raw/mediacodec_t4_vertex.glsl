#version 300 es

in vec4 position;
in vec2 texturePositionIn;
out vec2 texturePosition;
uniform mat4 matrix;

void main() {
    gl_Position = matrix * position;
    texturePosition = texturePositionIn;
}