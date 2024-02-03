package com.example.openglesdemo1.ui.mediacodec.t1

import android.app.Activity
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.util.Log
import com.example.openglesdemo1.R
import kotlinx.android.synthetic.main.activity_print_media_codec.audio_bitrate
import kotlinx.android.synthetic.main.activity_print_media_codec.audio_codec
import kotlinx.android.synthetic.main.activity_print_media_codec.audio_profile
import kotlinx.android.synthetic.main.activity_print_media_codec.audio_sample_rate
import kotlinx.android.synthetic.main.activity_print_media_codec.video_codec
import kotlinx.android.synthetic.main.activity_print_media_codec.video_profile

class PrintMediaCodecActivity : Activity() {
    private val TAG = "PrintMediaCodecActivity"
    private val VIDEO_CODEC = MediaFormat.MIMETYPE_VIDEO_AVC
    private val AUDIO_CODEC = MediaFormat.MIMETYPE_AUDIO_AAC
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_media_codec)
        initView()
        initListener()
    }

    private fun initView() {
        getEncoderInfoList(VIDEO_CODEC).map {
            it.name
        }.let {
            video_codec.setEntries(it.toTypedArray())
        }
        getEncoderInfoList(AUDIO_CODEC).map {
            it.name
        }.let {
            audio_codec.setEntries(it.toTypedArray())
        }
    }

    private fun initListener() {
        video_codec.mListener = object : LocalSpinner.Listener {
            override fun onChanged(str: String) {
                getEncoderInfoList(VIDEO_CODEC).find {
                    it.name == getVideoCodec()
                }?.getCapabilitiesForType(VIDEO_CODEC)?.let {
                    it.profileLevels?.let {
                        getVideoEncoderProfileAndLevel(it).let {
                            video_profile.setEntries(it.toTypedArray())
                        }
                    }
                }
            }
        }
        audio_codec.mListener = object : LocalSpinner.Listener {
            override fun onChanged(str: String) {
                getEncoderInfoList(AUDIO_CODEC).find {
                    it.name == getAudioCodec()
                }?.getCapabilitiesForType(AUDIO_CODEC)
                    ?.audioCapabilities?.let {
                        it.bitrateRange?.let {
                            val lower = Math.max(it.lower / 1000, 80)
                            val upper = it.upper / 1000
                            var v = lower
                            val list: ArrayList<String> = ArrayList()
                            while (v <= upper) {
                                list.add(v.toString())
                                v += lower
                            }
                            audio_bitrate.setEntries(list.toTypedArray())
                        }

                        it.supportedSampleRates?.map { it.toString() }?.let {
                            audio_sample_rate.setEntries(it.toTypedArray())
                        }

                        getAudioEncoderProfileAndLevel().let { audio_profile.setEntries(it.toTypedArray()) }
                    }
            }
        }
        //videoCapabilities.isSizeSupported(width, height)
        //videoCapabilities.areSizeAndRateSupported(width, height, fps)
        //videoCapabilities.getBitrateRange().contains(selectedBitrate)
    }

    private fun getVideoCodec() = video_codec.getSelected()
    private fun getAudioCodec() = audio_codec.getSelected()
}