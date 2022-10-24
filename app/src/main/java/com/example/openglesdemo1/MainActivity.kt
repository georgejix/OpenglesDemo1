package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.stu1.t1.TriangleActivity
import com.example.openglesdemo1.ui.stu1.t10.TextureCameraActivity
import com.example.openglesdemo1.ui.stu1.t14.NativeOpenGlesActivity
import com.example.openglesdemo1.ui.stu1.t15.FilterActivity
import com.example.openglesdemo1.ui.stu1.t2.ColorActivity
import com.example.openglesdemo1.ui.stu1.t3.RectangleActivity
import com.example.openglesdemo1.ui.stu1.t4.VertexBufferActivity
import com.example.openglesdemo1.ui.stu1.t5.VertexArrayActivity
import com.example.openglesdemo1.ui.stu1.t6.SquareActivity
import com.example.openglesdemo1.ui.stu1.t8.TextureActivity
import com.example.openglesdemo1.ui.stu1.t9.SurfaceCameraActivity
import com.example.openglesdemo1.ui.stu2.t11.TextureViewPreviewCamera2Activity
import com.example.openglesdemo1.ui.stu2.t12.GlPreviewCameraActivity
import com.example.openglesdemo1.ui.stu2.t13.GlPreviewCameraActivity2Activity
import com.example.openglesdemo1.ui.stu2.t16.GlPreviewCameraWithRecordActivity
import com.example.openglesdemo1.ui.stu3.t1.Test1Activity
import com.example.openglesdemo1.ui.stu3.t2.Test2Activity
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

                MainBean("-----------------------", null),
                //camera
                MainBean("TextureView预览camera2", TextureViewPreviewCamera2Activity::class.java),
                MainBean("openel预览Camera", GlPreviewCameraActivity::class.java), //camera1
                MainBean("openel预览Camera2", GlPreviewCameraActivity2Activity::class.java),//camera2
                MainBean(
                    "openel预览Camera,并存储",
                    GlPreviewCameraWithRecordActivity::class.java
                ), //camera1

                MainBean("-----------------------", null),
                //学习
                MainBean("测试-画球桌", Test1Activity::class.java),
                MainBean("测试-画球桌2", Test2Activity::class.java),
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