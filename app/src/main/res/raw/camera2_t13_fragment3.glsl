#version 300 es
precision mediump float;
in vec2 v_texCoord;
out vec4 outColor;
uniform sampler2D s_TextureMap;
uniform float u_Offset;  //偏移量 1.0/width
//Y =  0.299R + 0.587G + 0.114B
//U = -0.147R - 0.289G + 0.436B
//V =  0.615R - 0.515G - 0.100B
const vec3 COEF_Y = vec3( 0.299,  0.587,  0.114);
const vec3 COEF_U = vec3(-0.147, -0.289,  0.436);
const vec3 COEF_V = vec3( 0.615, -0.515, -0.100);
const float UV_DIVIDE_LINE = 2.0 / 3.0;
void main()
{
    vec2 texelOffset = vec2(u_Offset, 0.0);
    if(v_texCoord.y <= UV_DIVIDE_LINE) {
        //在纹理坐标 y < (2/3) 范围，需要完成一次对整个纹理的采样，
        //一次采样（加三次偏移采样）4 个 RGBA 像素（R,G,B,A）生成 1 个（Y0,Y1,Y2,Y3），整个范围采样结束时填充好 width*height 大小的缓冲区；

        vec2 texCoord = vec2(v_texCoord.x, v_texCoord.y * 3.0 / 2.0);
        vec4 color0 = texture(s_TextureMap, texCoord);
        vec4 color1 = texture(s_TextureMap, texCoord + texelOffset);
        vec4 color2 = texture(s_TextureMap, texCoord + texelOffset * 2.0);
        vec4 color3 = texture(s_TextureMap, texCoord + texelOffset * 3.0);

        float y0 = dot(color0.rgb, COEF_Y);
        float y1 = dot(color1.rgb, COEF_Y);
        float y2 = dot(color2.rgb, COEF_Y);
        float y3 = dot(color3.rgb, COEF_Y);
        outColor = vec4(y0, y1, y2, y3);
    }
    else {
        //当纹理坐标 y > (2/3) 范围，一次采样（加三次偏移采样）4 个 RGBA 像素（R,G,B,A）生成 1 个（V0,U0,V0,U1），
        //又因为 VU plane 缓冲区的高度为 height/2 ，VU plane 在垂直方向的采样是隔行进行，整个范围采样结束时填充好 width*height/2 大小的缓冲区。
        vec2 texCoord = vec2(v_texCoord.x, (v_texCoord.y - UV_DIVIDE_LINE) * 3.0);
        vec4 color0 = texture(s_TextureMap, texCoord);
        vec4 color1 = texture(s_TextureMap, texCoord + texelOffset);
        vec4 color2 = texture(s_TextureMap, texCoord + texelOffset * 2.0);
        vec4 color3 = texture(s_TextureMap, texCoord + texelOffset * 3.0);

        float v0 = dot(color0.rgb, COEF_V) + 0.5;
        float u0 = dot(color1.rgb, COEF_U) + 0.5;
        float v1 = dot(color2.rgb, COEF_V) + 0.5;
        float u1 = dot(color3.rgb, COEF_U) + 0.5;
        outColor = vec4(v0, u0, v1, u1);
    }
}