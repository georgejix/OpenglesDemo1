/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/

package com.example.openglesdemo1.ui.normal2.t5

import android.content.Context
import android.graphics.Color
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.normal2.t5.objects.ParticleShooter
import com.example.openglesdemo1.ui.normal2.t5.objects.ParticleSystem
import com.example.openglesdemo1.ui.normal2.t5.programs.ParticleShaderProgram
import com.example.openglesdemo1.ui.normal2.t5.util.Geometry
import com.example.openglesdemo1.ui.normal2.t5.util.MatrixHelper
import com.example.openglesdemo1.ui.normal2.t5.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test5Render(val context: Context) : GLSurfaceView.Renderer {
    private val projectionMatrix: FloatArray = FloatArray(16)
    private val viewMatrix: FloatArray = FloatArray(16)
    private val viewProjectionMatrix: FloatArray = FloatArray(16)
    private var particleProgram: ParticleShaderProgram? = null
    private var particleSystem: ParticleSystem? = null
    private var redParticleShooter: ParticleShooter? = null
    private var greenParticleShooter: ParticleShooter? = null
    private var blueParticleShooter: ParticleShooter? = null
    private var globalStartTime: Long = 0
    private var texture: Int = 0

    @Override
    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection = Geometry.Vector(0f, 0.5f, 0f)
        val angleVarianceInDegrees = 5f
        val speedVariance = 1f

        redParticleShooter = ParticleShooter(
            Geometry.Point(-1f, 0f, 0f),
            particleDirection,
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees,
            speedVariance
        )

        greenParticleShooter = ParticleShooter(
            Geometry.Point(0f, 0f, 0f),
            particleDirection,
            Color.rgb(25, 255, 25),
            angleVarianceInDegrees,
            speedVariance
        )

        blueParticleShooter = ParticleShooter(
            Geometry.Point(1f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance
        )

        texture = TextureHelper.loadTexture(context, R.mipmap.particle_texture)
    }

    @Override
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(
            projectionMatrix, 45f, width.toFloat() / height.toFloat(), 1f, 10f
        )
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(
            viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0
        )
    }

    @Override
    override fun onDrawFrame(glUnused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        val currentTime =(System.nanoTime() - globalStartTime) / 1000000000f
        redParticleShooter?.addParticles(particleSystem, currentTime, 5)
        greenParticleShooter?.addParticles(particleSystem, currentTime, 5)
        blueParticleShooter?.addParticles(particleSystem, currentTime, 5)
        particleProgram?.useProgram()
        particleProgram?.setUniforms(viewProjectionMatrix, currentTime, texture)
        particleSystem?.bindData(particleProgram)
        particleSystem?.draw()
    }
}