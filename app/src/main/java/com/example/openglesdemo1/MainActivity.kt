package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.t1.TriangleActivity
import com.example.openglesdemo1.ui.t2.ColorActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        rv_list.adapter = MainAdapter(
            listOf(
                MainBean("三角形", TriangleActivity::class.java),
                MainBean("纯色", ColorActivity::class.java)
            )
        )
    }
}