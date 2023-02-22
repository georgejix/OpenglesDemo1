#version 300 es
precision mediump float;

uniform sampler2D img;
uniform sampler2D lut;
in vec2 vTextCoord;
out vec4 fragColor;
uniform int filterFlag;

//查找表滤镜
vec4 lookup(in vec4 textureColor, in float matchLut){
    mediump float blueColor = textureColor.b * (pow(matchLut, 2.0)-1.0);
    mediump vec2 quad1;
    quad1.y = floor(floor(blueColor) / matchLut);
    quad1.x = floor(blueColor) - (quad1.y * matchLut);
    mediump vec2 quad2;
    quad2.y = floor(ceil(blueColor) / matchLut);
    quad2.x = ceil(blueColor) - (quad2.y * matchLut);
    highp vec2 texPos1;
    texPos1.x = (quad1.x *(1.0/matchLut)) + 0.5/(pow(matchLut, 3.0))+ ((1.0/matchLut - 1.0/pow(matchLut, 3.0)) * textureColor.r);
    texPos1.y = (quad1.y *(1.0/matchLut)) + 0.5/(pow(matchLut, 3.0)) + ((1.0/matchLut - 1.0/pow(matchLut, 3.0)) * textureColor.g);
    highp vec2 texPos2;
    texPos2.x = (quad2.x *(1.0/matchLut)) + 0.5/(pow(matchLut, 3.0)) + ((1.0/matchLut - 1.0/pow(matchLut, 3.0)) * textureColor.r);
    texPos2.y = (quad2.y *(1.0/matchLut)) + 0.5/(pow(matchLut, 3.0)) + ((1.0/matchLut - 1.0/pow(matchLut, 3.0)) * textureColor.g);
    lowp vec4 newColor1 = texture(lut, texPos1);
    lowp vec4 newColor2 = texture(lut, texPos2);
    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    return newColor;
}

//灰度
void grey(inout vec4 color){
    float weightMean = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    color.r = color.g = color.b = weightMean;
}

void main(){
    vec4 tempColor = texture(img, vTextCoord);
    if (0 == filterFlag){

    } else if (1 == filterFlag){
        //lut滤镜
        fragColor = lookup(tempColor, 8.0);
        return;
    } else if (2 == filterFlag){
        //lut滤镜
        fragColor = lookup(tempColor, 8.0);
        return;
    } else if (3==filterFlag){
        grey(tempColor);
    }
    fragColor = tempColor;
}