package com.example.openglesdemo1.ui.mediacodec.t1

import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.CodecProfileLevel
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import android.util.SparseArray
import androidx.core.util.forEach
import java.lang.reflect.Modifier

private val TAG = "MediaCodecUtil"

fun getEncoderInfoList(mimeType: String): List<MediaCodecInfo> =
    MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.filter {
        runCatching {
            it.isEncoder && null != it.getCapabilitiesForType(mimeType)
        }.onSuccess { it }.onFailure { false }.getOrNull() ?: false
    }

fun getVideoEncoderProfileAndLevel(levelList: Array<MediaCodecInfo.CodecProfileLevel>): List<String> {
    val list: ArrayList<String> = ArrayList()
    val videoProfileList = SparseArray<String>()
    val videoLevelList = SparseArray<String>()
    CodecProfileLevel::class.java.getFields().forEach {
        if (it.modifiers and (Modifier.STATIC or Modifier.FINAL) != 0) {
            Log.d(TAG, "video profile: ${it.name}")
            if (it.name.startsWith("AVCProfile")) {
                videoProfileList.put(it.getInt(null), it.name)
            } else if (it.name.startsWith("AVCLevel")) {
                videoLevelList.put(it.getInt(null), it.name)
            }
        }
    }
    levelList.filter {
        null != videoProfileList.get(it.profile) &&
                null != videoLevelList.get((it.level))
    }.forEach {
        list.add("${videoProfileList.get(it.profile)}-${videoLevelList.get((it.level))}")
    }
    return list
}

fun getAudioEncoderProfileAndLevel(): List<String> {
    val list: ArrayList<String> = ArrayList()
    val audioProfileList = SparseArray<String>()
    CodecProfileLevel::class.java.getFields().forEach {
        if (it.modifiers and (Modifier.STATIC or Modifier.FINAL) != 0) {
            Log.d(TAG, "audio profile: ${it.name}")
            if (it.name.startsWith("AACObject")) {
                audioProfileList.put(it.getInt(null), it.name)
            }
        }
    }
    audioProfileList.forEach { key, value -> list.add(value) }
    return list
}

fun getVideoFormat(
    mimeType: String, width: Int, height: Int,
    bitrate: Int, fps: Int, iframeInterval: Int,
    codecProfileLevel: MediaCodecInfo.CodecProfileLevel?
): MediaFormat {
    val format = MediaFormat.createVideoFormat(mimeType, width, height)
    format.setInteger(
        MediaFormat.KEY_COLOR_FORMAT,
        MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
    )
    format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
    format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
    format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframeInterval)
    codecProfileLevel?.let {
        if (codecProfileLevel.profile != 0 && codecProfileLevel.level != 0) {
            format.setInteger(MediaFormat.KEY_PROFILE, codecProfileLevel.profile)
            format.setInteger("level", codecProfileLevel.level)
        }
    }
    return format
}

fun getAudioFormat(
    mimeType: String, sampleRate: Int, channelCount: Int,
    profile: Int, bitRate: Int
): MediaFormat {
    val format = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount)
    format.setInteger(MediaFormat.KEY_AAC_PROFILE, profile)
    format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
    return format
}

