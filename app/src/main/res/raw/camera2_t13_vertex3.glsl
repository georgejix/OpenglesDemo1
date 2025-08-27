#version 300 es
in vec4 position;
in vec2 texturePositionIn;
out vec2 v_texCoord;
void main() {
    gl_Position = position;
    v_texCoord = texturePositionIn;
}