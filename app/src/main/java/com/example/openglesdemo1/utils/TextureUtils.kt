package com.example.openglesdemo1.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils

object TextureUtils {
    private val TAG = "TextureUtils"

    fun loadTexture(context: Context, resourceId: Int): Int {
        var textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
        if (0 == textureIds[0]) {
            return 0
        }
        var options: BitmapFactory.Options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
            ?: return 0
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR_MIPMAP_LINEAR
        )
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        // 加载bitmap到纹理中
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle()

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}