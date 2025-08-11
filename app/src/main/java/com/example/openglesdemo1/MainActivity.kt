package com.example.openglesdemo1

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.example.openglesdemo1.ui.camera.t1.TextureViewPreviewCamera2Activity
import com.example.openglesdemo1.ui.camera.t10.TwoEglPreviewCameraActivity
import com.example.openglesdemo1.ui.camera.t2.SurfaceViewPreviewCameraActivity
import com.example.openglesdemo1.ui.camera.t3.GlPreviewCameraActivity
import com.example.openglesdemo1.ui.camera.t4.GlPreviewCamera2Activity
import com.example.openglesdemo1.ui.camera.t5.TextureCameraActivity
import com.example.openglesdemo1.ui.camera.t6.GlPreviewCameraWithRecordActivity
import com.example.openglesdemo1.ui.camera.t7.ChangeFilterActivity
import com.example.openglesdemo1.ui.camera.t8.Camera2DataActivity
import com.example.openglesdemo1.ui.camera.t9.CameraDataActivity
import com.example.openglesdemo1.ui.mediacodec.t1.PrintMediaCodecActivity
import com.example.openglesdemo1.ui.mediacodec.t2.MediaCodecSaveVideoActivity
import com.example.openglesdemo1.ui.mediacodec.t3.MediaCodecPlayVideoActivity
import com.example.openglesdemo1.ui.mediacodec.t4.MCAndGlPlayVideoActivity
import com.example.openglesdemo1.ui.mediacodec.t5.MCAndEGlPlayVideoActivity
import com.example.openglesdemo1.ui.mediacodec.t6.BackRecordActivity
import com.example.openglesdemo1.ui.mediacodec.t7.VideoPlayerActivity
import com.example.openglesdemo1.ui.normal.t1.TriangleActivity
import com.example.openglesdemo1.ui.normal.t10.FilterActivity
import com.example.openglesdemo1.ui.normal.t11.TextureAndFilterActivity
import com.example.openglesdemo1.ui.normal.t12.FboFenceActivity
import com.example.openglesdemo1.ui.normal.t13.TextureArrayActivity
import com.example.openglesdemo1.ui.normal.t14.MultiTextureActivity
import com.example.openglesdemo1.ui.normal.t15.VboIboActivity
import com.example.openglesdemo1.ui.normal.t16.EglActivity
import com.example.openglesdemo1.ui.normal.t17.BlendActivity
import com.example.openglesdemo1.ui.normal.t18.TwoThreadActivity
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
import kotlinx.android.synthetic.main.activity_main.rv_list

class MainActivity : Activity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    /**
     * 纹理坐标原点在左下，android坐标原点在左上，所以mipmap纹理贴图是反的
     */
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
                MainBean("fbo,fence", FboFenceActivity::class.java),
                MainBean("纹理数组", TextureArrayActivity::class.java),
                MainBean("多重纹理渲染", MultiTextureActivity::class.java),
                MainBean("vbo ibo", VboIboActivity::class.java),
                MainBean("surfaceview用egl渲染", EglActivity::class.java),
                MainBean("blend", BlendActivity::class.java),
                MainBean("双线程渲染", TwoThreadActivity::class.java),

                MainBean("--------camera--------", null),
                //textureview的surfacetexture直接传入相机预览
                MainBean("TextureView预览camera2", TextureViewPreviewCamera2Activity::class.java),
                //surfaceview的holder.surface传入camera2
                MainBean("surfaceview预览camera2", SurfaceViewPreviewCameraActivity::class.java),
                //自定义texture，传入相机预览，然后用glsurfaceview画
                MainBean("GLSurfaceView预览Camera", GlPreviewCameraActivity::class.java), //camera1
                MainBean("GLSurfaceView预览Camera2", GlPreviewCamera2Activity::class.java),//camera2
                //将自定义surfacetexture传入相机预览，textureview与egl绑定，手动把texture内容画到textureview
                MainBean("TextureView+gl预览Camera", TextureCameraActivity::class.java),
                //创建texture，用于获取camera数据，用gl将texture画面画到glsurfaceview,同时也画到mediacodec的surface
                //glsurfaceview和mediacodec.surface共享gl_context
                MainBean("opengl预览Camera,并存储", GlPreviewCameraWithRecordActivity::class.java),
                MainBean("GLSurfaceView预览camera2，滤镜", ChangeFilterActivity::class.java),
                //nv21,jpeg数据都可以
                MainBean("获取camera2每帧数据", Camera2DataActivity::class.java),
                //nv21数据
                MainBean("获取camera每帧数据", CameraDataActivity::class.java),
                //第一个egl线程创建texture，给相机进行预览，然后用它的eglcontext当作sharecontext创建第二个egl线程
                MainBean("2个egl同时预览camera", TwoEglPreviewCameraActivity::class.java),


                MainBean("--------normal2--------", null),
                MainBean("测试-画球桌", Test1Activity::class.java),
                MainBean("测试-画球桌2", Test2Activity::class.java),
                MainBean("测试-画球桌3", Test3Activity::class.java),
                MainBean("测试-画球桌4", Test4Activity::class.java),
                MainBean("测试-画球桌5", Test5Activity::class.java),

                MainBean("--------mediacodec--------", null),
                MainBean("mediaMuxer,mediaCodec", PrintMediaCodecActivity::class.java),
                MainBean("mediaCodec保存视频", MediaCodecSaveVideoActivity::class.java),
                MainBean("硬解码+SurfaceView播放视频", MediaCodecPlayVideoActivity::class.java),
                MainBean("硬解码+GlSurfaceView播放视频", MCAndGlPlayVideoActivity::class.java),
                MainBean("硬解码+egl+SurfaceView播放视频", MCAndEGlPlayVideoActivity::class.java),
                MainBean("mediacodec后台录制不中断", BackRecordActivity::class.java),
                MainBean("视频播放器", VideoPlayerActivity::class.java),

                )
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }
}