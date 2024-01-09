/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.normal2.t5.programs

import android.content.Context
import android.opengl.GLES30
import com.example.openglesdemo1.R

class ParticleShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.normal2_t5_vertex,
    R.raw.normal2_t5_fragment
) {
    // Uniform locations
    private val uMatrixLocation: Int by lazy { GLES30.glGetUniformLocation(program, U_MATRIX) }
    private val uTimeLocation: Int by lazy { GLES30.glGetUniformLocation(program, U_TIME) }

    // Attribute locations
    private val aPositionLocation: Int by lazy { GLES30.glGetAttribLocation(program, A_POSITION) }
    private val aColorLocation: Int by lazy { GLES30.glGetAttribLocation(program, A_COLOR) }
    private val aDirectionVectorLocation: Int by lazy {
        GLES30.glGetAttribLocation(program, A_DIRECTION_VECTOR)
    }
    private val aParticleStartTimeLocation: Int by lazy {
        GLES30.glGetAttribLocation(
            program,
            A_PARTICLE_START_TIME
        )
    }
    private val uTextureUnitLocation: Int by lazy {
        GLES30.glGetUniformLocation(
            program,
            U_TEXTURE_UNIT
        )
    }

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES30.glUniform1f(uTimeLocation, elapsedTime)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation(): Int = aPositionLocation

    fun getColorAttributeLocation(): Int = aColorLocation

    fun getDirectionVectorAttributeLocation(): Int = aDirectionVectorLocation

    fun getParticleStartTimeAttributeLocation(): Int = aParticleStartTimeLocation
}
