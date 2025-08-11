package com.example.openglesdemo1.ui.mediacodec.t7

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class VideoFrontView : View, View.OnTouchListener {
    private val TAG = javaClass.simpleName
    private var mContext: Context
    private var mDownX = 0f
    private var mDownY = 0f
    private var mDownDir: SeekDirection? = null
    private var mTouchSlop = 0
    private var mClickCb: (() -> Unit)? = null
    private var mSeekCb: ((dir: SeekDirection?, change: Float) -> Unit)? = null

    enum class SeekDirection {
        LEFT, RIGHT, NULL
    }

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mContext = context
        init()
    }

    private fun init() {
        mTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop
        setOnTouchListener(this)
    }

    fun setClickListener(cb: (() -> Unit)?) {
        mClickCb = cb
    }

    fun setSeekListener(cb: ((dir: SeekDirection?, change: Float) -> Unit)?) {
        mSeekCb = cb
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        event?.takeIf { view != null }?.let { e ->
            Log.d(TAG, "onTouch action=${e.action} x=${e.rawX} y=${e.rawY} w=$width h=$height")
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = e.rawX
                    mDownY = e.rawY
                    if (mDownX > width / 2) {
                        mDownDir = SeekDirection.RIGHT
                        Log.d(TAG, "down start right")
                    } else {
                        mDownDir = SeekDirection.LEFT
                        Log.d(TAG, "down start left")
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    mDownDir ?: let {
                        if (Math.abs(mDownX - e.rawX) > mTouchSlop) {
                            mDownDir = SeekDirection.NULL
                        } else if (Math.abs(mDownY - e.rawY) > mTouchSlop) {
                            mDownDir = if (SeekDirection.RIGHT == mDownDir) SeekDirection.RIGHT
                            else SeekDirection.LEFT
                        }
                    }
                    mDownDir?.takeIf { SeekDirection.NULL != it }?.let {
                        val changePercent = (mDownY - e.rawY) * 1f / height
                        mSeekCb?.invoke(it, changePercent)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    mDownDir = null
                    if (Math.abs(mDownX - e.rawX) < mTouchSlop && Math.abs(mDownY - e.rawY) < mTouchSlop) {
                        mClickCb?.invoke()
                    } else Unit
                }

                else -> {}
            }
        }
        return true
    }

}