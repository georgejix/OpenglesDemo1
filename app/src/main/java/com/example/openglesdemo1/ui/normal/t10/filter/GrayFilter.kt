package com.example.openglesdemo1.ui.normal.t10.filter

import android.opengl.GLES30
import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils

class GrayFilter : BaseFilter() {
    private var aFilterLocation = 0
    private val filterValue = floatArrayOf(0.299f, 0.587f, 0.114f)

    init {
        initFilter(
            ResReadUtils.readResource(R.raw.normal_t10_gray_filter_vertex_shader),
            ResReadUtils.readResource(R.raw.normal_t10_gray_filter_fragment_shader)
        )
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        super.onSurfaceChanged(width, height)
        aFilterLocation = GLES30.glGetUniformLocation(mProgram, "a_Filter")
    }

    override fun onUpdateDrawFrame() {
        super.onUpdateDrawFrame()
        //更新参数
        GLES30.glUniform3fv(aFilterLocation, 1, filterValue, 0)
    }
}