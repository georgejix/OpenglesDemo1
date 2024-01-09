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
import com.example.openglesdemo1.ui.normal2.t5.util.ShaderHelper
import com.example.openglesdemo1.ui.normal2.t5.util.TextResourceReader

abstract class ShaderProgram(
    context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int
) {
    // Uniform constants
    protected val U_MATRIX: String = "u_Matrix"
    protected val U_COLOR: String = "u_Color"
    protected val U_TEXTURE_UNIT: String = "u_TextureUnit"
    protected val U_TIME: String = "u_Time"

    // Attribute constants
    protected val A_POSITION: String = "a_Position"
    protected val A_COLOR: String = "a_Color"
    protected val A_TEXTURE_COORDINATES: String = "a_TextureCoordinates"

    protected val A_DIRECTION_VECTOR: String = "a_DirectionVector"
    protected val A_PARTICLE_START_TIME: String = "a_ParticleStartTime"

    // Shader program
    val program: Int = ShaderHelper.buildProgram(
        TextResourceReader
            .readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader
            .readTextFileFromResource(context, fragmentShaderResourceId)
    )

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        GLES30.glUseProgram(program)
    }
}
