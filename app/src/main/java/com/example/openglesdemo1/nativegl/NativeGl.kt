package com.example.openglesdemo1.nativegl

class NativeGl {
    companion object {
        init {
            System.loadLibrary("native-window")
        }
    }

    external fun drawColor(surface: Any?, color: Int)
    external fun drawBitmap(surface: Any?, bitmap: Any?)
}