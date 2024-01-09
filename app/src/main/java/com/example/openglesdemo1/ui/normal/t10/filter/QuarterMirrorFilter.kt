package com.example.openglesdemo1.ui.normal.t10.filter

import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils

class QuarterMirrorFilter : BaseFilter() {
    init {
        initFilter(
            ResReadUtils.readResource(R.raw.normal_t10_quarter_mirror_filter_vertex_shader),
            ResReadUtils.readResource(R.raw.normal_t10_quarter_mirror_filter_fragment_shader)
        )
    }
}