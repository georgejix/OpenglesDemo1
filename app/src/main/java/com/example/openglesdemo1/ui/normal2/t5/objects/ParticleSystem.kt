/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.normal2.t5.objects

import android.graphics.Color
import android.opengl.GLES30
import com.example.openglesdemo1.ui.normal2.t5.data.VertexArray
import com.example.openglesdemo1.ui.normal2.t5.programs.ParticleShaderProgram
import com.example.openglesdemo1.ui.normal2.t5.util.Geometry

class ParticleSystem(val maxParticleCount: Int) {
    private val POSITION_COMPONENT_COUNT: Int = 3
    private val COLOR_COMPONENT_COUNT: Int = 3
    private val VECTOR_COMPONENT_COUNT: Int = 3
    private val PARTICLE_START_TIME_COMPONENT_COUNT: Int = 1
    private val TOTAL_COMPONENT_COUNT: Int =
        POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT
    private val STRIDE: Int = TOTAL_COMPONENT_COUNT * 4
    private val particles: FloatArray
    private val vertexArray: VertexArray
    private var currentParticleCount: Int = 0
    private var nextParticle: Int = 0

    init {
        particles = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
        vertexArray = VertexArray(particles)
    }

    fun addParticle(
        position: Geometry.Point, color: Int, direction: Geometry.Vector,
        particleStartTime: Float
    ) {
        val particleOffset = nextParticle * TOTAL_COMPONENT_COUNT

        var currentOffset = particleOffset
        nextParticle++

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0
        }

        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset++] = particleStartTime

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData(particleProgram: ParticleShaderProgram?) {
        particleProgram ?: return
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, STRIDE
        )
        dataOffset += POSITION_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT, STRIDE
        )
        dataOffset += COLOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getDirectionVectorAttributeLocation(),
            VECTOR_COMPONENT_COUNT, STRIDE
        )
        dataOffset += VECTOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.getParticleStartTimeAttributeLocation(),
            PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE
        )
    }

    fun draw() {
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, currentParticleCount)
    }
}
