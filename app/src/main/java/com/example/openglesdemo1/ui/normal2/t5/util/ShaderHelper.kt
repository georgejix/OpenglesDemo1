/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.normal2.t5.util

import android.opengl.GLES30

object ShaderHelper {
    /**
     * Loads and compiles a vertex shader, returning the OpenGL object ID.
     */
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES30.GL_VERTEX_SHADER, shaderCode)
    }

    /**
     * Loads and compiles a fragment shader, returning the OpenGL object ID.
     */
    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES30.GL_FRAGMENT_SHADER, shaderCode)
    }

    /**
     * Compiles a shader, returning the OpenGL object ID.
     */
    private fun compileShader(type: Int, shaderCode: String): Int {
        // Create a new shader object.
        val shaderObjectId = GLES30.glCreateShader(type)

        if (shaderObjectId == 0) {
            return 0
        }

        // Pass in the shader source.
        GLES30.glShaderSource(shaderObjectId, shaderCode)

        // Compile the shader.
        GLES30.glCompileShader(shaderObjectId)

        // Get the compilation status.
        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(
            shaderObjectId, GLES30.GL_COMPILE_STATUS,
            compileStatus, 0
        )

        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            GLES30.glDeleteShader(shaderObjectId)
            return 0
        }

        // Return the shader object ID.
        return shaderObjectId
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {

        // Create a new program object.
        val programObjectId = GLES30.glCreateProgram()

        if (programObjectId == 0) {
            return 0
        }

        // Attach the vertex shader to the program.
        GLES30.glAttachShader(programObjectId, vertexShaderId)

        // Attach the fragment shader to the program.
        GLES30.glAttachShader(programObjectId, fragmentShaderId)

        // Link the two shaders together into a program.
        GLES30.glLinkProgram(programObjectId)

        // Get the link status.
        val linkStatus = IntArray(1)
        GLES30.glGetProgramiv(
            programObjectId, GLES30.GL_LINK_STATUS,
            linkStatus, 0
        )

        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            GLES30.glDeleteProgram(programObjectId)
            return 0
        }

        // Return the program object ID.
        return programObjectId
    }

    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    fun validateProgram(programObjectId: Int): Boolean {
        GLES30.glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        GLES30.glGetProgramiv(
            programObjectId, GLES30.GL_VALIDATE_STATUS,
            validateStatus, 0
        )
        return validateStatus[0] != 0
    }

    /**
     * Helper function that compiles the shaders, links and validates the
     * program, returning the program ID.
     */
    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        // Compile the shaders.
        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)

        // Link them into a shader program.
        return linkProgram(vertexShader, fragmentShader)
    }
}
