package com.example.openglesdemo1.ui.stu1.t12.filter

import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils

class QuarterMirrorFilter : BaseFilter() {
    init {
        initFilter(
            ResReadUtils.readResource(R.raw.quarter_mirror_filter_vertex_shader),
            ResReadUtils.readResource(R.raw.quarter_mirror_filter_fragment_shader)
        )
    }
}