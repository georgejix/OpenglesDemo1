#version 300 es
precision mediump float;
layout(location = 0) out vec4 fragColor;
in vec2 v_textureCoordinate;
uniform sampler2D u_texture;
void main() {
    float offset = 0.01;
    vec4 colorCenter = texture(u_texture, vec2(v_textureCoordinate.x, v_textureCoordinate.y));
    vec4 colorLeft = texture(u_texture, vec2(v_textureCoordinate.x - offset, v_textureCoordinate.y));
    vec4 colorTop = texture(u_texture, vec2(v_textureCoordinate.x, v_textureCoordinate.y + offset));
    vec4 colorRight = texture(u_texture, vec2(v_textureCoordinate.x + offset, v_textureCoordinate.y));
    vec4 colorBottom = texture(u_texture, vec2(v_textureCoordinate.x, v_textureCoordinate.y - offset));
    fragColor = (colorCenter + colorLeft + colorTop + colorRight + colorBottom) / 5.0;
}