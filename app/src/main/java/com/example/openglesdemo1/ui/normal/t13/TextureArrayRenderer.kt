package com.example.openglesdemo1.ui.normal.t13

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils
import com.example.openglesdemo1.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureArrayRenderer(val context: Context) : GLSurfaceView.Renderer {

    private var mVertexBuffer: FloatBuffer
    private var mTextureBuffer: FloatBuffer
    private var mProgramId = 0
    private var mTextureId = 0
    private var mVertexPositionLocation = 0
    private var mTexturePositionLocation = 0
    private var mTextureLocation = 0

    private val mVertexArray = floatArrayOf(
        -1f, 1f,
        -1f, 0f,
        0f, 1f,
        0f, 0f,
        -1f, 0f,
        0f, 1f,
        0f, 0f,
        0f, -1f,
        1f, 0f,
        1f, -1f,
        0f, -1f,
        1f, 0f
    )

    private val mTextureArray = floatArrayOf(
        0f, 0f, 0f,
        0f, 1f, 0f,
        1f, 0f, 0f,
        1f, 1f, 0f,
        0f, 1f, 0f,
        1f, 0f, 0f,
        0f, 0f, 1f,
        0f, 1f, 1f,
        1f, 0f, 1f,
        1f, 1f, 1f,
        0f, 1f, 1f,
        1f, 0f, 1f
    )

    init {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexArray.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertexArray).position(0)
        mTextureBuffer = ByteBuffer.allocateDirect(mTextureArray.size * java.lang.Float.SIZE)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureBuffer.put(mTextureArray).position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexId =
            ShaderUtils.compileVertexShader(ResReadUtils.readResource(R.raw.normal_t13_vertex))
        val fragmentId =
            ShaderUtils.compileFragmentShader(ResReadUtils.readResource(R.raw.normal_t13_fragment))
        mProgramId = ShaderUtils.linkProgram(vertexId, fragmentId)

        GLES30.glUseProgram(mProgramId)
        mVertexPositionLocation = GLES30.glGetAttribLocation(mProgramId, "a_Position")
        mTexturePositionLocation = GLES30.glGetAttribLocation(mProgramId, "aTexCoord")
        mTextureLocation = GLES30.glGetUniformLocation(mProgramId, "u_textureUnit")

        val textures = IntArray(1)
        GLES30.glGenTextures(textures.size, textures, 0)
        mTextureId = textures[0]

        // 注意这里是GL_TEXTURE_2D_ARRAY
        // Note that the type is GL_TEXTURE_2D_ARRAY
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D_ARRAY, mTextureId)
        val imgId = R.mipmap.img_bg2
        val bitmap = BitmapFactory.decodeResource(context.resources, imgId)
        GLES30.glTexStorage3D(
            GLES30.GL_TEXTURE_2D_ARRAY, 1, GLES30.GL_RGBA8,
            bitmap.width, bitmap.height, 2
        )

        // 通过glTexSubImage3D指定每层的纹理
        // Specify the texture of each layer via glTexSubImage3D
        for (i in 0 until 2) {
            val bitmap = BitmapFactory.decodeResource(context.resources, imgId)
            val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
            bitmap.copyPixelsToBuffer(b)
            b.position(0)
            GLES30.glTexSubImage3D(
                GLES30.GL_TEXTURE_2D_ARRAY,
                0,
                0,
                0,
                i,
                bitmap.width,
                bitmap.height,
                1,
                GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,
                b
            )
            bitmap.recycle()
        }

        // 启动对应位置的参数，这里直接使用LOCATION_UNIFORM_TEXTURE，而无需像OpenGL 2.0那样需要先获取参数的location
        GLES30.glUniform1i(mTextureLocation, 0)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        GLES30.glEnableVertexAttribArray(mVertexPositionLocation)
        GLES30.glVertexAttribPointer(
            mVertexPositionLocation,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            mVertexBuffer
        )
        GLES30.glEnableVertexAttribArray(mTexturePositionLocation)
        GLES30.glVertexAttribPointer(
            mTexturePositionLocation,
            3,
            GLES30.GL_FLOAT,
            false,
            0,
            mTextureBuffer
        )

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexArray.size / 2)

    }
}