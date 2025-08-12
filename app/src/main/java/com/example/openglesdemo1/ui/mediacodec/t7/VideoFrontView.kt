package com.example.openglesdemo1.ui.mediacodec.t7

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoFrontView : View, View.OnTouchListener {
    private val TAG = javaClass.simpleName
    private var mContext: Context
    private var mDownX = 0f
    private var mDownY = 0f
    private val mEventHistory = ArrayDeque<MotionEvent>(10)
    private val mEventMaxSize = 9
    private var mDownDir: SeekDirection? = null
    private var mTouchSlop = 0
    private var mClickCb: ((doubleClick: Boolean) -> Unit)? = null
    private var mSeekCb: ((dir: SeekDirection?, change: Float) -> Unit)? = null
    private var mLastTouchTime = 0L
    private val mDoubleClickInterval = 500L
    private var mClickJob: Job? = null
    private val mVelocityTracker by lazy { VelocityTracker.obtain() }

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
        mTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop * 2
        Log.d(TAG, "init mTouchSlop=$mTouchSlop")
        setOnTouchListener(this)
    }

    fun setClickListener(cb: ((doubleClick: Boolean) -> Unit)?) {
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
                    Log.d(TAG, if (mDownX > width / 2) "down start right" else "down start left")
                }

                MotionEvent.ACTION_MOVE -> {
                    mDownDir ?: let {
                        if (Math.abs(mDownX - e.rawX) > mTouchSlop) {
                            mDownDir = SeekDirection.NULL
                        } else if (Math.abs(mDownY - e.rawY) > mTouchSlop) {
                            mDownDir = if (mDownX > width / 2) SeekDirection.RIGHT
                            else SeekDirection.LEFT
                        }
                    }
                    if (mEventHistory.size >= mEventMaxSize) {
                        mEventHistory.removeFirst()
                    }
                    mEventHistory.addLast(MotionEvent.obtain(e))
                    mDownDir?.takeIf { SeekDirection.NULL != it }?.let {
                        val changePercent =
                            (mEventHistory.first().rawY - e.rawY) * getSpeed() / height
                        mSeekCb?.invoke(it, changePercent)
                        Log.d(TAG, "mSeekCb changePercent=$changePercent ${mEventHistory.size} ${mEventHistory.first().rawY} ${e.rawY}")
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (null == mDownDir || SeekDirection.NULL == mDownDir) {
                        if (System.currentTimeMillis() - mLastTouchTime < mDoubleClickInterval) {
                            mClickJob?.cancel()
                            mClickJob = null
                            mClickCb?.invoke(true)
                        } else {
                            mClickJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                mClickCb?.invoke(false)
                            }
                        }
                    }
                    mVelocityTracker.clear()
                    mEventHistory.clear()
                    mDownDir = null
                    mLastTouchTime = System.currentTimeMillis()
                }

                else -> {}
            }
        }
        return true
    }

    private fun getSpeed(): Float {
        mVelocityTracker.clear()
        mEventHistory.forEach { mVelocityTracker.addMovement(it) }
        mVelocityTracker.computeCurrentVelocity(300)
        val speed =
            (Math.cbrt(Math.abs(mVelocityTracker.yVelocity).toDouble()) * 0.25).toFloat()
        Log.d(TAG, "getSpeed $speed")
        return speed
    }

}