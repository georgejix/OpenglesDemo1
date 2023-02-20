package com.example.openglesdemo1.ui.stu1.t12.filter

interface RendererFilter {
    /**
     * 创建回调
     */
    fun onSurfaceCreated()

    /**
     * 宽高改变回调
     *
     * @param width
     * @param height
     */
    fun onSurfaceChanged(width: Int, height: Int)

    /**
     * 绘制回调
     */
    fun onDrawFrame()

    /**
     * 销毁回调
     */
    fun onDestroy()
}