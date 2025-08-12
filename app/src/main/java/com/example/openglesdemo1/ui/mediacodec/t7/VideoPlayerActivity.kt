package com.example.openglesdemo1.ui.mediacodec.t7

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.openglesdemo1.GlobalViewModel
import com.example.openglesdemo1.R
import com.example.openglesdemo1.databinding.ActivityVideoPlayerBinding
import com.example.openglesdemo1.utils.getInputVideoPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private var mBinding: ActivityVideoPlayerBinding? = null
    private var mNeedAddListener = true
    private val mLocalMediaPlayer by lazy { LocalMediaPlayer() }
    private val mAudioManager: AudioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }
    private val mVideoList by lazy {
        arrayListOf(
            getInputVideoPath("hzw.mp4"), getInputVideoPath("不可告人01.mp4")
        )
    }

    //3秒自动隐藏操作界面
    private var mHideControlJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_player)
        mBinding?.globalVm = GlobalViewModel
        mNeedAddListener = true
    }

    override fun onResume() {
        super.onResume()
        if (mNeedAddListener) {
            addListener()
            genHideControlJob()
            mNeedAddListener = false
        }
        mLocalMediaPlayer.startListen()
    }

    override fun onPause() {
        super.onPause()
        mLocalMediaPlayer.pause()
        mLocalMediaPlayer.stopListen()
        mBinding?.layoutControl?.visibility = View.GONE
        cancelHideControlJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocalMediaPlayer.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }

    @SuppressLint("SetTextI18n")
    private fun addListener() {
        mBinding?.viewFront?.setClickListener { doubleClick ->
            mBinding?.layoutControl?.let { controlView ->
                if (doubleClick) {
                    mLocalMediaPlayer.togglePlayPause()
                } else {
                    if (View.VISIBLE == controlView.visibility) {
                        controlView.visibility = View.GONE
                        cancelHideControlJob()
                    } else {
                        controlView.visibility = View.VISIBLE
                        genHideControlJob()
                    }
                }
            }
        }
        mBinding?.viewFront?.setSeekListener { dir, change ->
            if (VideoFrontView.SeekDirection.LEFT == dir) {
                setBrightness(change)
            } else if (VideoFrontView.SeekDirection.RIGHT == dir) {
                setVolume(change)
            }
        }
        mBinding?.sv?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (mLocalMediaPlayer.getDataSource().isEmpty()) {
                    play(mVideoList[0]) { mLocalMediaPlayer.setSurface(holder.surface) }
                } else {
                    mLocalMediaPlayer.setSurface(holder.surface)
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mLocalMediaPlayer.setSurface(null)
            }

        })
        mLocalMediaPlayer.setCb { status, what, extra ->
            when (status) {
                PlayerStatusEnum.COMPLETION -> {
                    if (false == mBinding?.seekBar?.isPressed) {
                        play(getNextPath(1)) {
                            mLocalMediaPlayer.setSurface(mBinding?.sv?.holder?.surface)
                        }
                    } else {
                        Log.d(TAG, "seekbar pressed")
                    }
                }

                PlayerStatusEnum.ERROR -> play(getNextPath(1)) {
                    mLocalMediaPlayer.setSurface(mBinding?.sv?.holder?.surface)
                }

                PlayerStatusEnum.VIDEO_SIZE -> {
                    mBinding?.takeIf { what != null && extra != null }?.let {
                        val vWidth = what!!
                        val vHeight = extra!!
                        val pWidth = it.layoutBg.width
                        val pHeight = it.layoutBg.height
                        if (pWidth * vHeight > pHeight * vWidth) {
                            val height = pHeight
                            val width = height * vWidth / vHeight
                            it.sv.layoutParams.width = width
                            it.sv.layoutParams.height = height
                        } else {
                            val width = pWidth
                            val height = width * vHeight / vWidth
                            it.sv.layoutParams.width = width
                            it.sv.layoutParams.height = height
                        }
                    }
                }
            }
        }
        mLocalMediaPlayer.mPlayingLD.observe(this) { isPlaying ->
            mBinding?.imgPlayPause?.setImageResource(
                if (isPlaying) R.drawable.icon_player_pause
                else R.drawable.icon_player_play
            )
        }
        mLocalMediaPlayer.mTitleLD.observe(this) { title -> mBinding?.tvTitle?.text = title }
        mLocalMediaPlayer.mSpeedLD.observe(this) { speed -> mBinding?.tvSpeed?.text = "${speed}X" }
        mLocalMediaPlayer.mCurrentTimeLD.observe(this) { time ->
            mBinding?.tvTime?.text = mLocalMediaPlayer.formatTime(time / 1000)
            mLocalMediaPlayer.getDuration().takeIf { it > 0 }?.let {
                mBinding?.seekBar?.progress = (time * 1000 / it).toInt()
            }
        }
        mLocalMediaPlayer.mDurationLD.observe(this) { time ->
            mBinding?.tvDuration?.text = mLocalMediaPlayer.formatTime(time / 1000)
        }
        mBinding?.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                cancelHideControlJob()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                genHideControlJob()
                seekBar?.takeIf { mLocalMediaPlayer.getDuration() > 0 }?.let {
                    val duration = it.progress * mLocalMediaPlayer.getDuration() / it.max
                    mLocalMediaPlayer.seekTo(duration)
                    mLocalMediaPlayer.start()
                }
            }

        })
    }

    override fun onClick(view: View?) {
        genHideControlJob()
        view?.id?.let {
            when (it) {
                R.id.back_video -> {
                    mLocalMediaPlayer.release()
                    finish()
                }


                R.id.img_play_pause -> {
                    if (mLocalMediaPlayer.isPlaying()) {
                        mLocalMediaPlayer.pause()
                    } else {
                        mLocalMediaPlayer.start()
                    }
                }

                R.id.img_pre -> play(getNextPath(-1)) { mLocalMediaPlayer.setSurface(mBinding?.sv?.holder?.surface) }
                R.id.img_next -> play(getNextPath(1)) { mLocalMediaPlayer.setSurface(mBinding?.sv?.holder?.surface) }
                R.id.img_speed -> mLocalMediaPlayer.changeSpeed()
                else -> {}
            }
        }
    }

    private fun play(path: String, func: (() -> Unit)? = null) {
        Log.d(TAG, "play path=$path")
        mLocalMediaPlayer.initPlayer()
        func?.invoke()
        mLocalMediaPlayer.setDataSource(path)
        mLocalMediaPlayer.prepareAsync()
    }

    private fun getNextPath(next: Int): String {
        val currentData = mLocalMediaPlayer.getDataSource()
        val index = mVideoList.indexOf(currentData)
        return mVideoList[(index + next) % mVideoList.size].also {
            Log.d(TAG, "getNextPath path=$it")
        }
    }

    private fun cancelHideControlJob() {
        mHideControlJob?.cancel()
        mHideControlJob = null
    }

    private fun genHideControlJob() {
        cancelHideControlJob()
        mHideControlJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            mBinding?.layoutControl?.visibility = View.GONE
        }
    }

    private fun setBrightness(percent: Float) {
        val lp = window.attributes
        var value = lp.screenBrightness + percent
        value = Math.min(value, 1f)
        value = Math.max(value, 0.1f)
        lp.screenBrightness = value
        window.attributes = lp
        Log.d(TAG, "setBrightness $value")
    }

    private fun setVolume(percent: Float) {
        val currentV = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val value = currentV + (percent * maxVolume).toInt()
        Log.d(TAG, "setVolume percent=$percent $currentV to $value maxVolume=$maxVolume")
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_SHOW_UI)
    }
}