package com.example.openglesdemo1

import android.app.Activity
import android.os.Bundle
import com.example.openglesdemo1.ui.camera.t1.TextureViewPreviewCamera2Activity
import com.example.openglesdemo1.ui.camera.t2.GlPreviewCameraActivity
import com.example.openglesdemo1.ui.camera.t3.GlPreviewCameraActivity2Activity
import com.example.openglesdemo1.ui.camera.t4.TextureCameraActivity
import com.example.openglesdemo1.ui.camera.t5.GlPreviewCameraWithRecordActivity
import com.example.openglesdemo1.ui.camera.t6.ChangeFilterActivity
import com.example.openglesdemo1.ui.normal.t1.TriangleActivity
import com.example.openglesdemo1.ui.normal.t10.FilterActivity
import com.example.openglesdemo1.ui.normal.t11.TextureAndFilterActivity
import com.example.openglesdemo1.ui.normal.t2.ColorActivity
import com.example.openglesdemo1.ui.normal.t3.RectangleActivity
import com.example.openglesdemo1.ui.normal.t4.VertexBufferActivity
import com.example.openglesdemo1.ui.normal.t5.VertexArrayActivity
import com.example.openglesdemo1.ui.normal.t6.SquareActivity
import com.example.openglesdemo1.ui.normal.t8.TextureActivity
import com.example.openglesdemo1.ui.normal.t9.NativeOpenGlesActivity
import com.example.openglesdemo1.ui.normal2.t1.Test1Activity
import com.example.openglesdemo1.ui.normal2.t2.Test2Activity
import com.example.openglesdemo1.ui.normal2.t3.Test3Activity
import com.example.openglesdemo1.ui.normal2.t4.Test4Activity
import com.example.openglesdemo1.ui.normal2.t5.Test5Activity
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
                MainBean("--------normal--------", null),
                MainBean("三角形", TriangleActivity::class.java),
                MainBean("纯色", ColorActivity::class.java),
                MainBean("矩形", RectangleActivity::class.java),
                MainBean("顶点缓冲区", VertexBufferActivity::class.java),
                MainBean("顶点数组", VertexArrayActivity::class.java),
                MainBean("立方体", SquareActivity::class.java),
                MainBean(
                    "索引法立方体",
                    com.example.openglesdemo1.ui.normal.t7.SquareActivity::class.java
                ),
                MainBean("2d纹理显示bitmap", TextureActivity::class.java),
                MainBean("native opengles", NativeOpenGlesActivity::class.java),
                MainBean("滤镜", FilterActivity::class.java),
                MainBean("纹理+滤镜", TextureAndFilterActivity::class.java),

                MainBean("--------camera--------", null),
                //textureview的surfacetexture直接传入相机预览
                MainBean("TextureView预览camera2", TextureViewPreviewCamera2Activity::class.java),
                //自定义texture，传入相机预览，然后用glsurfaceview画
                MainBean("GLSurfaceView+gl预览Camera", GlPreviewCameraActivity::class.java), //camera1
                MainBean(
                    "GLSurfaceView+gl预览Camera2",
                    GlPreviewCameraActivity2Activity::class.java
                ),//camera2
                //将自定义surfacetexture传入相机预览，textureview与egl绑定，手动把texture内容画到textureview
                MainBean("TextureView+gl预览Camera", TextureCameraActivity::class.java),
                //自定义texture，传入相机预览，glsurfaceview画回显帧，然后同时将texture给gl，画一份到mediacodec创建的surface
                //mediacodec可直接获得编码后数据流
                MainBean("openel预览Camera,并存储", GlPreviewCameraWithRecordActivity::class.java),
                MainBean("GLSurfaceView+gl预览camera2，滤镜", ChangeFilterActivity::class.java),


                MainBean("--------normal2--------", null),
                MainBean("测试-画球桌", Test1Activity::class.java),
                MainBean("测试-画球桌2", Test2Activity::class.java),
                MainBean("测试-画球桌3", Test3Activity::class.java),
                MainBean("测试-画球桌4", Test4Activity::class.java),
                MainBean("测试-画球桌5", Test5Activity::class.java),


                )
        )
    }
}