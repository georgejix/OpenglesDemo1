package com.example.openglesdemo1.ui.normal.t18

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceView
import com.example.openglesdemo1.R
import com.example.openglesdemo1.ui.base.BaseActivity2

class TwoThreadActivity : BaseActivity2() {
    private val TAG = "TwoThreadActivity"

    private val mSv1: SurfaceView by lazy { findViewById(R.id.sv1) }
    private val mSv2: SurfaceView by lazy { findViewById(R.id.sv2) }

    private val INIT = 1001
    private val DRAW = 1002

    private var mDrawPic1: DrawPic? = null
    private var mDrawPic2: DrawPic? = null

    private val mHandlerThread1 by lazy { HandlerThread("sv1") }
    private val mHandler1: Handler by lazy {
        mHandlerThread1.start()
        Handler(mHandlerThread1.looper, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                when (msg.what) {
                    INIT -> mDrawPic1?.init()
                    DRAW -> mDrawPic1?.draw()
                }
                return true
            }
        })
    }

    private val mHandlerThread2 by lazy { HandlerThread("sv2") }
    private val mHandler2: Handler by lazy {
        mHandlerThread2.start()
        Handler(mHandlerThread2.looper, object : Handler.Callback {
            override fun handleMessage(msg: Message): Boolean {
                when (msg.what) {
                    INIT -> mDrawPic2?.init()
                    DRAW -> mDrawPic2?.draw()
                }
                return true
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t18)
        mSv1.post {
            mDrawPic1 = DrawPic(mSv1, R.mipmap.main)
            mDrawPic1?.setMatrix(mSv1.width, mSv1.height)
            mHandler1.sendEmptyMessage(INIT)
            mHandler1.sendEmptyMessage(DRAW)
        }
        //将sv2置于z轴最上方
        mSv2.setZOrderOnTop(true)
        mSv2.setZOrderMediaOverlay(true)
        mSv2.post {
            mDrawPic2 = DrawPic(mSv2, R.mipmap.img_bg2)
            mDrawPic2?.setMatrix(mSv2.width, mSv2.height)
            mHandler2.sendEmptyMessage(INIT)
            mHandler2.sendEmptyMessage(DRAW)
        }
    }

}