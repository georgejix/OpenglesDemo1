#version 300 es
precision mediump float;

uniform sampler2D img1;
uniform sampler2D img2;
in vec2 point;
out vec4 fragColor;

vec4 lookup(in vec4 textureColor, in float matchLut){
    /*mediump float blueColor = textureColor.b * (pow(matchLut, 2.0)-1.0);
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
    lowp vec4 newColor1 = texture(img2, texPos1);
    lowp vec4 newColor2 = texture(img2, texPos2);
    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    return newColor;*/

    //return texture(img2, point);

    // 取出当前像素的纹素
    float blueColor = textureColor.b * 63.0;
    // 计算B通道，看使用哪个像素色块（这里分别对计算结果向上，向下取整，然后再对两者进行线性计算，减小误差）
     vec2 quad1;
    quad1.y = floor(floor(blueColor) / 8.0);
    quad1.x = floor(blueColor) - (quad1.y * 8.0);
    vec2 quad2;
    quad2.y = floor(ceil(blueColor) / 8.0);
    quad2.x = ceil(blueColor) - (quad2.y * 8.0);
    // 计算R、G通道
    vec2 texPos1;
    texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
    texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);
    vec2 texPos2;
    texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
    texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);
    // 根据转换后的纹理坐标，在基准图上取色
    vec4 newColor1 = texture(img2, texPos1);
    vec4 newColor2 = texture(img2, texPos2);
    // 对计算出来的两个色值，线性求平均(fract：取小数点后值)
    vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    // intensity 按需计算滤镜透明度，混合计算前后的色值
    return vec4(newColor.rgb, textureColor.w);
}

void main(){
    fragColor = lookup(texture(img1, point), 8.0);
}