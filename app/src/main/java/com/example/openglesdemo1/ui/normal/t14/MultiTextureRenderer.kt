package com.example.openglesdemo1.ui.normal.t14

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

class MultiTextureRenderer(val context: Context) : GLSurfaceView.Renderer {
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
        "multi" to Bean(),
        "draw" to Bean()
    )

    private val mVertexArray = floatArrayOf(
        -1f, 1f, -1f, -1f, 1f, 1f,
        1f, -1f, -1f, -1f, 1f, 1f,
    )

    private val mTextureArray = floatArrayOf(
        0f, 0f, 0f, 1f, 1f, 0f,
        1f, 1f, 0f, 1f, 1f, 0f,
    )
    private var mVertexBuffer: FloatBuffer
    private var mTextureBuffer: FloatBuffer
    private val mDrawVertexArrays = arrayOf(
        floatArrayOf(
            -1f, 1f, -1f, 0f, 0f, 1f,
            0f, 0f, -1f, 0f, 0f, 1f,
        ),
        floatArrayOf(
            0f, 1f, 0f, 0f, 1f, 1f,
            1f, 0f, 0f, 0f, 1f, 1f,
        ),
        floatArrayOf(
            -1f, 0f, -1f, -1f, 0f, 0f,
            0f, -1f, -1f, -1f, 0f, 0f,
        )
    )
    private val mDrawVertexBuffers = ArrayList<FloatBuffer>()
    private val mDrawTextureArray = floatArrayOf(
        0f, 1f, 0f, 0f, 1f, 1f,
        1f, 0f, 0f, 0f, 1f, 1f,
    )
    private var mDrawTextureBuffer: FloatBuffer

    private val mTextures = IntArray(3)
    private var mFrameBufferId = 0

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexArray.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexArray).position(0)
        mTextureBuffer = ByteBuffer.allocateDirect(mTextureArray.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBuffer.put(mTextureArray).position(0)
        mDrawVertexArrays.forEach { array ->
            val buffer =
                ByteBuffer.allocateDirect(array.size * java.lang.Float.SIZE / 8)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer()
            buffer.put(array).position(0)
            mDrawVertexBuffers.add(buffer)
        }
        mDrawTextureBuffer =
            ByteBuffer.allocateDirect(mDrawTextureArray.size * java.lang.Float.SIZE/8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mDrawTextureBuffer.put(mDrawTextureArray).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgramMap["multi"]?.apply {
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t14_vertex_multi))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t14_fragment_multi))
            programId = ShaderUtils.linkProgram(vertexId, fragmentId)
            vertexPositionLocation = GLES30.glGetAttribLocation(programId, "position")
            texturePositionLocation = GLES30.glGetAttribLocation(programId, "texturePosition")
            matrixLocation = GLES30.glGetUniformLocation(programId, "matrix")
            textureLocation = GLES30.glGetUniformLocation(programId, "texture")
            textureId = TextureUtils.loadTexture(context, R.mipmap.main)
        }
        mProgramMap["draw"]?.apply {
            val vertexId =
                ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t14_vertex_draw))
            val fragmentId =
                ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t14_fragment_draw))
            programId = ShaderUtils.linkProgram(vertexId, fragmentId)
            vertexPositionLocation = GLES30.glGetAttribLocation(programId, "position")
            texturePositionLocation = GLES30.glGetAttribLocation(programId, "texturePosition")
            textureLocation = GLES30.glGetUniformLocation(programId, "texture")
            textureId = TextureUtils.loadTexture(context, R.mipmap.main)
        }
        mProgramMap["draw"]?.apply {
            textureId = TextureUtils.loadTexture(context, R.mipmap.main)
            GLES30.glGenTextures(mTextures.size, mTextures, 0)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        mProgramMap["multi"]?.apply {
            Matrix.orthoM(
                matrixArray, 0, -1f, 1f,
                -height * 1f / width, height * 1f / width,
                -1f, 1f
            )
        }
        val frameBuffer = IntArray(1)
        GLES30.glGenFramebuffers(frameBuffer.size, frameBuffer, 0)
        mFrameBufferId = frameBuffer[0]
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId)
        // 将3个渲染目标绑定到frame buffer上的3个attachment上
        for (i in mTextures.indices) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[i])
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexParameteri(
                GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height,
                0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null
            )
            GLES30.glFramebufferTexture2D(
                GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0 + i,
                GLES30.GL_TEXTURE_2D, mTextures[i], 0
            )
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        }
        // 将0~2号attachment设置为draw目标
        val attachments = intArrayOf(
            GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_COLOR_ATTACHMENT1,
            GLES30.GL_COLOR_ATTACHMENT2
        )
        val attachBuffer = IntBuffer.allocate(attachments.size)
        attachBuffer.put(attachments).position(0)
        //加上会渲染GL_COLOR_ATTACHMENT1和GL_COLOR_ATTACHMENT2对应的纹理，不加只有GL_COLOR_ATTACHMENT0纹理有内容
        GLES30.glDrawBuffers(attachments.size, attachBuffer)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        mProgramMap["multi"]?.apply {
            GLES30.glUseProgram(programId)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId)
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

        mProgramMap["draw"]?.apply {
            GLES30.glUseProgram(programId)
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
            GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1f)
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
            for (i in mTextures.indices) {
                GLES30.glEnableVertexAttribArray(vertexPositionLocation)
                GLES30.glVertexAttribPointer(
                    vertexPositionLocation,
                    2,
                    GLES30.GL_FLOAT,
                    false,
                    0,
                    mDrawVertexBuffers[i]
                )
                GLES30.glEnableVertexAttribArray(texturePositionLocation)
                GLES30.glVertexAttribPointer(
                    texturePositionLocation,
                    2,
                    GLES30.GL_FLOAT,
                    false,
                    0,
                    mDrawTextureBuffer
                )
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextures[i])
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexArray.size / 2)
            }
        }

    }
}