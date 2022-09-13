package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.t1.TriangleActivity
import com.example.openglesdemo1.ui.t10.TextureCameraActivity
import com.example.openglesdemo1.ui.t11.TextureViewPreviewCamera2Activity
import com.example.openglesdemo1.ui.t12.GlPreviewCameraActivity
import com.example.openglesdemo1.ui.t13.GlPreviewCameraActivity2Activity
import com.example.openglesdemo1.ui.t14.NativeOpenGlesActivity
import com.example.openglesdemo1.ui.t15.FilterActivity
import com.example.openglesdemo1.ui.t2.ColorActivity
import com.example.openglesdemo1.ui.t3.RectangleActivity
import com.example.openglesdemo1.ui.t4.VertexBufferActivity
import com.example.openglesdemo1.ui.t5.VertexArrayActivity
import com.example.openglesdemo1.ui.t6.SquareActivity
import com.example.openglesdemo1.ui.t8.TextureActivity
import com.example.openglesdemo1.ui.t9.SurfaceCameraActivity
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
                //opengl
                MainBean("三角形", TriangleActivity::class.java),
                MainBean("纯色", ColorActivity::class.java),
                MainBean("矩形", RectangleActivity::class.java),
                MainBean("顶点缓冲区", VertexBufferActivity::class.java),
                MainBean("顶点数组", VertexArrayActivity::class.java),
                MainBean("立方体", SquareActivity::class.java),
                MainBean("索引法立方体", SquareActivity::class.java),
                MainBean("2d纹理显示bitmap", TextureActivity::class.java),
                MainBean("surface camera", SurfaceCameraActivity::class.java),
                MainBean("texture camera", TextureCameraActivity::class.java),
                MainBean("native opengles", NativeOpenGlesActivity::class.java),
                MainBean("滤镜", FilterActivity::class.java),

                //camera
                MainBean("TextureView预览camera2", TextureViewPreviewCamera2Activity::class.java),
                MainBean("openel预览Camera", GlPreviewCameraActivity::class.java), //camera1
                MainBean("openel预览Camera2", GlPreviewCameraActivity2Activity::class.java),//camera2
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