package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.t1.TriangleActivity
import com.example.openglesdemo1.ui.t2.ColorActivity
import com.example.openglesdemo1.ui.t3.RectangleActivity
import com.example.openglesdemo1.ui.t4.VertexBufferActivity
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
                MainBean("纯色", ColorActivity::class.java),
                MainBean("矩形", RectangleActivity::class.java),
                MainBean("顶点缓冲区", VertexBufferActivity::class.java),
            )
        )
        test(6, { a, b -> println("a+b=${a + b}") })
    }

    private fun test(num: Int, f1: (a: Int, b: Int) -> Unit) {
        if (num > 5) {
            f1(5, num)
        } else {
            f1(1, num)
        }
    }
}