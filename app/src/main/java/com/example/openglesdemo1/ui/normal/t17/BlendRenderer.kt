package com.example.openglesdemo1.ui.normal.t17

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import com.example.openglesdemo1.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlendRenderer(val context: Context) : GLSurfaceView.Renderer {
    private data class Bean(
        var programId: Int = 0,
        var textureId: Int = 0,
        var vertexPositionLocation: Int = 0,
        var texturePositionLocation: Int = 0,
        var textureLocation: Int = 0,
        var matrixLocation: Int = 0,
        var matrixArray: FloatArray = FloatArray(16)
    )

    private val mProgramMap = mapOf(
        "1" to Bean(),
        "2" to Bean()
    )

    private val mVertexArray = floatArrayOf(
        -1f, 1f, -1f, -1f, 1f, 1f,
        1f, -1f, -1f, -1f, 1f, 1f,
    )
    private val mVertex2Array = floatArrayOf(
        -0.1f, 0.1f, -0.1f, -0.1f, 0.1f, 0.1f,
        0.1f, -0.1f, -0.1f, -0.1f, 0.1f, 0.1f,
    )
    private val mTextureArray = floatArrayOf(
        0f, 0f, 0f, 1f, 1f, 0f,
        1f, 1f, 0f, 1f, 1f, 0f,
    )
    private var mVertexBuffer: FloatBuffer
    private var mVertex2Buffer: FloatBuffer
    private var mTextureBuffer: FloatBuffer

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexArray.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexArray).position(0)
        mVertex2Buffer = ByteBuffer.allocateDirect(mVertex2Array.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertex2Buffer.put(mVertex2Array).position(0)
        mTextureBuffer = ByteBuffer.allocateDirect(mTextureArray.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBuffer.put(mTextureArray).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgramMap["1"]?.apply {
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t17_vertex))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t17_fragment))
            programId = ShaderUtils.linkProgram(vertexId, fragmentId)
            vertexPositionLocation = GLES30.glGetAttribLocation(programId, "vertexPosition")
            texturePositionLocation = GLES30.glGetAttribLocation(programId, "texturePosition")
            matrixLocation = GLES30.glGetUniformLocation(programId, "matrix")
            textureLocation = GLES30.glGetUniformLocation(programId, "texture")
            textureId = TextureUtils.loadTexture(context, R.mipmap.main)
        }
        mProgramMap["2"]?.apply {
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t17_vertex))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t17_fragment))
            programId = ShaderUtils.linkProgram(vertexId, fragmentId)
            vertexPositionLocation = GLES30.glGetAttribLocation(programId, "vertexPosition")
            texturePositionLocation = GLES30.glGetAttribLocation(programId, "texturePosition")
            matrixLocation = GLES30.glGetUniformLocation(programId, "matrix")
            textureLocation = GLES30.glGetUniformLocation(programId, "texture")
            textureId = TextureUtils.loadTexture(context, R.mipmap.ic_camera)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        mProgramMap["1"]?.apply {
            Matrix.orthoM(
                matrixArray, 0, -1f, 1f,
                -height * 1f / width, height * 1f / width,
                -1f, 1f
            )
        }
        mProgramMap["2"]?.apply {
            Matrix.orthoM(
                matrixArray, 0, -1f, 1f,
                -height * 1f / width, height * 1f / width,
                -1f, 1f
            )
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        mProgramMap["1"]?.apply {
            GLES30.glUseProgram(programId)
            GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            GLES30.glEnableVertexAttribArray(vertexPositionLocation)
            GLES30.glVertexAttribPointer(
                vertexPositionLocation,
                2,
                GLES30.GL_FLOAT,
                false,
                0,
                mVertexBuffer
            )
            GLES30.glEnableVertexAttribArray(texturePositionLocation)
            GLES30.glVertexAttribPointer(
                texturePositionLocation,
                2,
                GLES30.GL_FLOAT,
                false,
                0,
                mTextureBuffer
            )
            GLES30.glUniformMatrix4fv(matrixLocation, 1, false, matrixArray, 0)
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexArray.size / 2)
        }

        //同一个画布上，2个program渲染，不加blend，第二个渲染的texture会有黑底
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE)
        mProgramMap["2"]?.apply {
            GLES30.glUseProgram(programId)
            GLES30.glEnableVertexAttribArray(vertexPositionLocation)
            GLES30.glVertexAttribPointer(
                vertexPositionLocation,
                2,
                GLES30.GL_FLOAT,
                false,
                0,
                mVertex2Buffer
            )
            GLES30.glEnableVertexAttribArray(texturePositionLocation)
            GLES30.glVertexAttribPointer(
                texturePositionLocation,
                2,
                GLES30.GL_FLOAT,
                false,
                0,
                mTextureBuffer
            )
            GLES30.glUniformMatrix4fv(matrixLocation, 1, false, matrixArray, 0)
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexArray.size / 2)
        }
        GLES30.glDisable(GLES30.GL_BLEND)

    }
}