package com.example.openglesdemo1.ui.mediacodec.t7

import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class LocalMediaPlayer {
    private val TAG = javaClass.simpleName
    private var mDataSourcePath: String = ""
    private var mMediaPlayer: MediaPlayer? = null
    private var mCirculationJob: Job? = null
    private var mNeedQueryPlayStatus = false
    private val mSupportSpeedList = arrayListOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    private var mCb: ((status: PlayerStatusEnum, what: Int?, extra: Int?) -> Unit)? = null

    var mPlayingLD = MutableLiveData(false)
    var mCurrentTimeLD = MutableLiveData(0L)
    var mDurationLD = MutableLiveData(0L)
    var mTitleLD = MutableLiveData("")
    var mSpeedLD = MutableLiveData(1f)

    fun startListen() {
        Log.d(TAG, "startListen")
        mNeedQueryPlayStatus = true
        mCirculationJob?.cancel()
        mCirculationJob = CoroutineScope(Dispatchers.IO).launch {
            while (mNeedQueryPlayStatus) {
                mPlayingLD.postValue(isPlaying())
                mCurrentTimeLD.postValue(getCurrentPosition())
                getSpeed().takeIf { it != 0f }?.let {
                    mSpeedLD.postValue(it)
                }
                delay(1000)
            }
        }
    }

    fun stopListen() {
        Log.d(TAG, "stopListen")
        mNeedQueryPlayStatus = false
        mCirculationJob?.cancel()
    }

    fun setCb(cb: ((status: PlayerStatusEnum, what: Int?, extra: Int?) -> Unit)?) {
        Log.d(TAG, "setCb $cb")
        mCb = cb
    }

    fun initPlayer() {
        mMediaPlayer?.let {
            runCatching {
                it.reset()
                it.release()
            }
        }
        mMediaPlayer = MediaPlayer()
        Log.d(TAG, "initPlayer mMediaPlayer=$mMediaPlayer")
        mMediaPlayer?.setOnCompletionListener {
            Log.d(TAG, "onCompletion")
            mCb?.invoke(PlayerStatusEnum.COMPLETION, null, null)
        }
        mMediaPlayer?.setOnErrorListener { mp, what, extra ->
            Log.d(TAG, "onError what=$what extra=$extra")
            mCb?.invoke(PlayerStatusEnum.ERROR, what, extra)
            false
        }
        mMediaPlayer?.setOnInfoListener { mp, what, extra ->
            Log.d(TAG, "onInfo what:$what extra:$extra")
            mCb?.invoke(PlayerStatusEnum.INFO, what, extra)
            false
        }
        mMediaPlayer?.setOnPreparedListener {
            Log.d(TAG, "onPrepared")
            mDurationLD.postValue(getDuration())
            mTitleLD.postValue(mDataSourcePath.substring(mDataSourcePath.lastIndexOf(File.separator) + 1))
            it.start()
            mCb?.invoke(PlayerStatusEnum.PREPARED, null, null)
        }
        mMediaPlayer?.setOnSeekCompleteListener {
            Log.d(TAG, "onSeekComplete")
            mCb?.invoke(PlayerStatusEnum.SEEK_COMPLETE, null, null)
        }
        mMediaPlayer?.setOnVideoSizeChangedListener { mp, width, height ->
            Log.d(TAG, "onVideoSizeChanged width=$width height=$height")
            mCb?.invoke(PlayerStatusEnum.VIDEO_SIZE, width, height)
        }
    }

    fun release() {
        Log.d(TAG, "release")
        mMediaPlayer?.let {
            runCatching {
                if (it.isPlaying) {
                    it.pause()
                    it.stop()
                }
                it.reset()
                it.release()
            }
        }
        mMediaPlayer = null
    }

    fun setDataSource(path: String) {
        Log.d(TAG, "setDataSource path=$path mMediaPlayer=$mMediaPlayer")
        mDataSourcePath = path
        runCatching { mMediaPlayer?.setDataSource(path) }.onFailure {
            Log.d(TAG, "setDataSource fail")
        }
    }

    fun getDataSource(): String {
        Log.d(TAG, "getDataSource $mDataSourcePath mMediaPlayer=$mMediaPlayer")
        return mDataSourcePath
    }

    fun setSurface(surface: Surface?) {
        Log.d(TAG, "setSurface $surface mMediaPlayer=$mMediaPlayer")
        runCatching { mMediaPlayer?.setSurface(surface) }
            .onFailure { Log.d(TAG, "setSurface fail") }
    }

    fun start() {
        Log.d(TAG, "start mMediaPlayer=$mMediaPlayer")
        runCatching { mMediaPlayer?.start() }.onFailure { Log.d(TAG, "start fail") }
    }

    fun pause() {
        Log.d(TAG, "pause mMediaPlayer=$mMediaPlayer")
        runCatching {
            if (isPlaying()) {
                mMediaPlayer?.pause()
            }
        }.onFailure { Log.d(TAG, "pause fail") }
    }

    fun stop() {
        Log.d(TAG, "stop mMediaPlayer=$mMediaPlayer")
        runCatching { mMediaPlayer?.stop() }.onFailure { Log.d(TAG, "stop fail") }
    }

    fun seekTo(seek: Long) {
        Log.d(TAG, "seekTo $seek mMediaPlayer=$mMediaPlayer")
        runCatching { mMediaPlayer?.seekTo(seek.toInt()) }
            .onFailure { Log.d(TAG, "seekTo fail") }
    }

    fun setSpeed(speed: Float) {
        Log.d(TAG, "setSpeed speed=$speed mMediaPlayer=$mMediaPlayer")
        runCatching {
            val param = mMediaPlayer?.playbackParams
            param?.speed = speed
            param?.let { mMediaPlayer?.playbackParams = param }
        }.onFailure {
            Log.d(TAG, "setSpeed fail")
        }
    }

    fun getSpeed(): Float {
        Log.d(TAG, "getSpeed mMediaPlayer=$mMediaPlayer")
        return try {
            mMediaPlayer?.playbackParams?.speed ?: 1f
        } catch (e: Exception) {
            Log.d(TAG, "getSpeed err")
            0f
        }.also {
            Log.d(TAG, "getSpeed $it")
        }
    }

    fun prepareAsync() {
        Log.d(TAG, "prepareAsync mMediaPlayer=$mMediaPlayer")
        runCatching { mMediaPlayer?.prepareAsync() }
            .onFailure { Log.d(TAG, "prepareAsync fail") }
    }

    fun getCurrentPosition(): Long {
        Log.d(TAG, "getCurrentPosition mMediaPlayer=$mMediaPlayer")
        return try {
            mMediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (e: Exception) {
            Log.d(TAG, "getCurrentPosition err")
            0L
        }.also {
            Log.d(TAG, "getCurrentPosition $it")
        }
    }

    fun getDuration(): Long {
        Log.d(TAG, "getDuration mMediaPlayer=$mMediaPlayer")
        return try {
            mMediaPlayer?.duration?.toLong() ?: 0L
        } catch (e: Exception) {
            Log.d(TAG, "getDuration err")
            0L
        }.also {
            Log.d(TAG, "getDuration $it")
        }
    }

    fun isPlaying(): Boolean {
        Log.d(TAG, "isPlaying mMediaPlayer=$mMediaPlayer")
        return try {
            mMediaPlayer?.isPlaying ?: false
        } catch (e: Exception) {
            Log.d(TAG, "isPlaying err")
            false
        }.also {
            Log.d(TAG, "isPlaying $it")
        }
    }

    /***功能类函数***/

    fun formatTime(time: Long): String {
        val hour = (time / 3600).toString().let { if (it.length < 2) "0$it" else it }
        val minute = ((time / 60) % 60).toString().let { if (it.length < 2) "0$it" else it }
        val second = (time % 60).toString().let { if (it.length < 2) "0$it" else it }
        //return if (time > 3600) "$hour:$minute:$second" else "$minute:$second"
        return "$hour:$minute:$second"
    }

    fun changeSpeed() {
        val currentSpeed = getSpeed()
        val index = mSupportSpeedList.indexOf(currentSpeed)
        val nextSpeed = mSupportSpeedList[(index + 1) % mSupportSpeedList.size]
        Log.d(TAG, "changeSpeed from$currentSpeed to $nextSpeed")
        setSpeed(nextSpeed)
    }
}