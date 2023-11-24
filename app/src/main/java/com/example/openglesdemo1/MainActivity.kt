package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.mytest1.t1.MyTest1Activity
import com.example.openglesdemo1.ui.stu1.t1.TriangleActivity
import com.example.openglesdemo1.ui.stu1.t10.TextureCameraActivity
import com.example.openglesdemo1.ui.stu1.t11.NativeOpenGlesActivity
import com.example.openglesdemo1.ui.stu1.t12.FilterActivity
import com.example.openglesdemo1.ui.stu1.t2.ColorActivity
import com.example.openglesdemo1.ui.stu1.t3.RectangleActivity
import com.example.openglesdemo1.ui.stu1.t4.VertexBufferActivity
import com.example.openglesdemo1.ui.stu1.t5.VertexArrayActivity
import com.example.openglesdemo1.ui.stu1.t6.SquareActivity
import com.example.openglesdemo1.ui.stu1.t8.TextureActivity
import com.example.openglesdemo1.ui.stu1.t9.SurfaceCameraActivity
import com.example.openglesdemo1.ui.stu2.t1.TextureViewPreviewCamera2Activity
import com.example.openglesdemo1.ui.stu2.t2.GlPreviewCameraActivity
import com.example.openglesdemo1.ui.stu2.t3.GlPreviewCameraActivity2Activity
import com.example.openglesdemo1.ui.stu2.t4.GlPreviewCameraWithRecordActivity
import com.example.openglesdemo1.ui.stu3.t1.Test1Activity
import com.example.openglesdemo1.ui.stu3.t2.Test2Activity
import com.example.openglesdemo1.ui.stu3.t3.Test3Activity
import com.example.openglesdemo1.ui.stu3.t4.Test4Activity
import com.example.openglesdemo1.ui.stu3.t5.Test5Activity
import com.example.openglesdemo1.ui.stu3.t6.Test6Activity
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
                MainBean("--------stu1--------", null),
                MainBean("三角形", TriangleActivity::class.java),
                MainBean("纯色", ColorActivity::class.java),
                MainBean("矩形", RectangleActivity::class.java),
                MainBean("顶点缓冲区", VertexBufferActivity::class.java),
                MainBean("顶点数组", VertexArrayActivity::class.java),
                MainBean("立方体", SquareActivity::class.java),
                MainBean("索引法立方体", com.example.openglesdemo1.ui.stu1.t7.SquareActivity::class.java),
                MainBean("2d纹理显示bitmap", TextureActivity::class.java),
                MainBean("GLSurfaceView+gl预览camera1、2", SurfaceCameraActivity::class.java),
                MainBean("TextureView+gl预览Camera", TextureCameraActivity::class.java),
                MainBean("native opengles", NativeOpenGlesActivity::class.java),
                MainBean("滤镜", FilterActivity::class.java),

                MainBean("--------stu2--------", null),
                //camera
                MainBean("TextureView预览camera2", TextureViewPreviewCamera2Activity::class.java),
                MainBean("GLSurfaceView+gl预览Camera", GlPreviewCameraActivity::class.java), //camera1
                MainBean("GLSurfaceView+gl预览Camera2", GlPreviewCameraActivity2Activity::class.java),//camera2
                MainBean(
                    "openel预览Camera,并存储", GlPreviewCameraWithRecordActivity::class.java
                ), //camera1

                MainBean("--------stu3--------", null),
                //学习
                MainBean("测试-画球桌", Test1Activity::class.java),
                MainBean("测试-画球桌2", Test2Activity::class.java),
                MainBean("测试-画球桌3", Test3Activity::class.java),
                MainBean("测试-画球桌4", Test4Activity::class.java),
                MainBean("测试-画球桌5", Test5Activity::class.java),
                MainBean("叠加滤镜", Test6Activity::class.java),

                MainBean("--------mytest1--------", null),
                MainBean("GLSurfaceView+gl预览camera2，滤镜", MyTest1Activity::class.java),
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