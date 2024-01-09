#version 300 es
precision mediump float;

out vec4 fragColor;
in vec4 v_Color;

void main(){
    fragColor = v_Color;
}