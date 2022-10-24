package com.example.openglesdemo1.ui.stu1.t15

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.stu1.t15.filter.GrayFilter
import com.example.openglesdemo1.ui.stu1.t15.filter.OriginFilter
import com.example.openglesdemo1.ui.stu1.t15.filter.QuarterMirrorFilter
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : Activity() {
    private val mGLView: GlView by lazy { GlView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        initView()
    }

    private fun initView() {
        t1.setOnClickListener(this::onClick)
        t2.setOnClickListener(this::onClick)
        t3.setOnClickListener(this::onClick)
        layout_surface.addView(mGLView)
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.t1 -> {
                mGLView.setFilter(OriginFilter())
            }
            R.id.t2 -> {
                mGLView.setFilter(GrayFilter())
            }
            R.id.t3 -> {
                mGLView.setFilter(QuarterMirrorFilter())
            }
        }
    }
}