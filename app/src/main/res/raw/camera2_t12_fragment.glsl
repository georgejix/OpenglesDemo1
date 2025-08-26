#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
//precision highp float;
precision mediump float;
uniform samplerExternalOES img;
uniform sampler2D icon;
in vec2 texturePosition;
//将vertexPosition透传
in vec2 vertexPosition;
out vec4 fragColor;
vec2 leftBottom = vec2(-0.99,0.89);
vec2 rightTop = vec2(-0.89,0.99);
void main() {
    //判断如果在左上角区域，绘制icon texture
    if(vertexPosition.x >= leftBottom.x && vertexPosition.x <= rightTop.x &&
        vertexPosition.y >= leftBottom.y && vertexPosition.y <= rightTop.y){
        //缩放
        vec2 tex0 = vec2((vertexPosition.x-leftBottom.x)/(rightTop.x-leftBottom.x),
             1.0-(vertexPosition.y-leftBottom.y)/(rightTop.y-leftBottom.y));
        vec4 color = texture(icon, tex0);
        //fragColor = color*color.a + texture(icon, texturePosition)*(1.0-color.a);
        if(0.0 == color.r && 0.0 == color.g && 0.0 == color.b){
            //如果空数据，直接绘制底部img
            fragColor = texture(img, texturePosition);
        }else{
            //可以使用mix混合图层
            //fragColor = mix(color,texture(img, texturePosition),0.5);
            fragColor = color;
        }
    }else{
        fragColor = texture(img, texturePosition);
    }
}