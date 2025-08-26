#version 300 es
in vec4 position2;
in vec2 texturePositionIn2;
out vec2 texturePosition2;
void main() {
    gl_Position = position2;
    texturePosition2 = texturePositionIn2;
}