/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/

package com.example.openglesdemo1.ui.normal2.t5.objects

import android.opengl.Matrix
import com.example.openglesdemo1.ui.normal2.t5.util.Geometry
import java.util.*

/** This class shoots particles in a particular direction. */

class ParticleShooter(
    val position: Geometry.Point, val direction: Geometry.Vector, val color: Int,
    val angleVariance: Float, val speedVariance: Float
) {
    private val random: Random = Random()

    private val rotationMatrix: FloatArray = FloatArray(16)
    private val directionVector: FloatArray = FloatArray(4)
    private val resultVector: FloatArray = FloatArray(4)

    init {
        directionVector[0] = direction.x
        directionVector[1] = direction.y
        directionVector[2] = direction.z
    }

    fun addParticles(particleSystem: ParticleSystem?, currentTime: Float, count: Int) {
        particleSystem ?: return
        for (i in 0 until count) {
            Matrix.setRotateEulerM(
                rotationMatrix, 0,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance
            )

            Matrix.multiplyMV(
                resultVector, 0,
                rotationMatrix, 0,
                directionVector, 0
            )

            val speedAdjustment = 1f + random.nextFloat() * speedVariance

            val thisDirection = Geometry.Vector(
                resultVector[0] * speedAdjustment,
                resultVector[1] * speedAdjustment,
                resultVector[2] * speedAdjustment
            )

            particleSystem.addParticle(position, color, thisDirection, currentTime)
        }
    }
}
