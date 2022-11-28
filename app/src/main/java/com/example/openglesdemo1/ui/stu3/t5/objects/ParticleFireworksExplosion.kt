/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.stu3.t5.objects

import android.graphics.Color
import android.opengl.Matrix.multiplyMV
import android.opengl.Matrix.setRotateEulerM
import com.example.openglesdemo1.ui.stu3.t5.util.Geometry
import java.util.*

class ParticleFireworksExplosion {
    private val random: Random = Random()
    private val rotationMatrix: FloatArray = FloatArray(16)
    private val directionVector: FloatArray = floatArrayOf(0f, 0f, 1f, 1f)
    private val resultVector: FloatArray = FloatArray(4)
    private val hsv: FloatArray = FloatArray(3)

    fun addExplosion(
        particleSystem: ParticleSystem, position: Geometry.Point,
        color: Int, startTime: Long
    ) {
        val currentTime = (System.nanoTime() - startTime) / 1000000000f

        for (trail in 0 until 50) {
            setRotateEulerM(
                rotationMatrix, 0,
                random.nextFloat() * 360f,
                random.nextFloat() * 360f,
                random.nextFloat() * 360f
            )

            multiplyMV(
                resultVector, 0, rotationMatrix, 0,
                directionVector, 0
            )

            val magnitude = 0.5f + (random.nextFloat() / 2f)
            var timeForThisStream = currentTime
            Color.colorToHSV(color, hsv)

            for (particle in 0 until 10) {
                particleSystem.addParticle(
                    position,
                    Color.HSVToColor(hsv),
                    Geometry.Vector(
                        resultVector[0] * magnitude,
                        resultVector[1] * magnitude + 0.5f,
                        resultVector[2] * magnitude
                    ),
                    timeForThisStream
                )
                timeForThisStream += 0.025f
                hsv[2] *= 0.9f
            }
        }
    }
}
