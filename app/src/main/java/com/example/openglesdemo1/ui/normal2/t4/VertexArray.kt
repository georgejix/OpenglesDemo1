package com.example.openglesdemo1.ui.normal2.t4

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(vertexData: FloatArray) {
    val mFloatBuffer: FloatBuffer

    init {
        mFloatBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        mFloatBuffer.position(0)
    }

    fun setVertexAttriPointer(offset: Int, location: Int, count: Int, stride: Int) {
        mFloatBuffer.position(offset)
        GLES30.glVertexAttribPointer(
            location, count, GLES30.GL_FLOAT,
            false, stride, mFloatBuffer
        )
        GLES30.glEnableVertexAttribArray(location)
    }
}