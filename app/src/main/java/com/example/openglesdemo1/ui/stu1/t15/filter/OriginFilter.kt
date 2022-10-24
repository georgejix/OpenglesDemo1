package com.example.openglesdemo1.ui.stu1.t15.filter

import com.example.openglesdemo1.R
import com.example.openglesdemo1.utils.ResReadUtils

class OriginFilter : BaseFilter() {
    init {
        initFilter(
            ResReadUtils.readResource(R.raw.no_filter_vertex_shader),
            ResReadUtils.readResource(R.raw.no_filter_fragment_shader)
        )
    }
}