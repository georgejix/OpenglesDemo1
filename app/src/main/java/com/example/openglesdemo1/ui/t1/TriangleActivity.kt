package com.example.openglesdemo1.ui.t1

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log

class TriangleActivity : Activity() {
    private var CONTEXT_CLIENT_VERSION = 3;
    private lateinit var mGLSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_test1)
        initView()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView.onPause()
    }

    private fun initView() {
        mGLSurfaceView = GLSurfaceView(this);
        if (detectOpenGLES30()) {
            // Tell the surface view we want to create an OpenGL ES 3.0-compatible
            // context, and set an OpenGL ES 3.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
            mGLSurfaceView.setRenderer(HelloTriangleRenderer(this));
        } else {
            Log.e("HelloTriangle", "OpenGL ES 3.0 not supported on device.  Exiting...");
            finish();
        }

        setContentView(mGLSurfaceView);
    }

    private fun detectOpenGLES30(): Boolean {
        var am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        var info: ConfigurationInfo = am.deviceConfigurationInfo
        return (info.reqGlEsVersion >= 0x30000);
    }

}