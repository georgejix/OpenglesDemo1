#version 300 es
precision mediump float;

uniform sampler2D img;
uniform sampler2D lut;
in vec2 vTextCoord;
out vec4 fragColor;
uniform int filterFlag;

//rgb转hsl
vec3 rgb2hsl(vec3 color){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(color.bg, K.wz), vec4(color.gb, K.xy), step(color.b, color.g));
    vec4 q = mix(vec4(p.xyw, color.r), vec4(color.r, p.yzx), step(p.x, color.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

//hsl转rgb
vec3 hsl2rgb(vec3 color){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(color.xxx + K.xyz) * 6.0 - K.www);
    return color.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), color.y);
}

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

//黑白
void blackAndWhite(inout vec4 color){
    float threshold = 0.5;
    float mean = (color.r + color.g + color.b) / 3.0;
    color.r = color.g = color.b = mean >= threshold ? 1.0 : 0.0;
}

//反向
void reverse(inout vec4 color){
    color.r = 1.0 - color.r;
    color.g = 1.0 - color.g;
    color.b = 1.0 - color.b;
}

//亮度
void light(inout vec4 color){
    vec3 hslColor = vec3(rgb2hsl(color.rgb));
    hslColor.z += 0.15;
    color = vec4(hsl2rgb(hslColor), color.a);
}

void light2(inout vec4 color){
    color.r += 0.15;
    color.g += 0.15;
    color.b += 0.15;
}

//色调分离
void posterization(inout vec4 color){
    //计算灰度值
    float grayValue = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    //转换到hsl颜色空间
    vec3 hslColor = vec3(rgb2hsl(color.rgb));
    //根据灰度值区分阴影和高光，分别处理
    if (grayValue < 0.3){
        //添加蓝色
        if (hslColor.x < 0.68 || hslColor.x > 0.66){
            hslColor.x = 0.67;
        }
        //增加饱和度
        hslColor.y += 0.3;
    } else if (grayValue > 0.7){
        //添加黄色
        if (hslColor.x < 0.18 || hslColor.x > 0.16){
            hslColor.x = 0.17;
        }
        //降低饱和度
        hslColor.y -= 0.3;
    }
    color = vec4(hsl2rgb(hslColor), color.a);
}

void main(){
    vec4 tempColor = texture(img, vTextCoord);
    switch (filterFlag){
        case 0:
        break;
        case 1:
        case 2:
        //lut滤镜
        fragColor = lookup(tempColor, 8.0);
        break;
        case 3:
        grey(tempColor);
        break;
        case 4:
        blackAndWhite(tempColor);
        break;
        case 5:
        reverse(tempColor);
        break;
        case 6:
        light(tempColor);
        break;
        case 7:
        light2(tempColor);
        break;
        case 8:
        posterization(tempColor);
        break;
    }
    fragColor = tempColor;
}