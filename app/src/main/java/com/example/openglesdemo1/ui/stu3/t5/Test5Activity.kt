/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.stu3.t5;

import android.opengl.GLSurfaceView
import com.example.openglesdemo1.ui.base.BaseActivity

class Test5Activity : BaseActivity() {
    override fun getRender(): GLSurfaceView.Renderer {
        return Test5Render(this)
    }
}