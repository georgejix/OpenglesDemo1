package com.example.openglesdemo1.ui.camera.t5.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.openglesdemo1.R;
import com.example.openglesdemo1.ui.camera.t5.encoder.MediaAudioEncoder;
import com.example.openglesdemo1.ui.camera.t5.encoder.MediaEncoder;
import com.example.openglesdemo1.ui.camera.t5.encoder.MediaMuxerWrapper;
import com.example.openglesdemo1.ui.camera.t5.encoder.MediaVideoEncoder;

import java.io.IOException;

public class CameraFragment extends Fragment {
    private static final boolean DEBUG = false;    // TODO set false on release
    private static final String TAG = "CameraFragment";

    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;
    /**
     * for scale mode display
     */
    private TextView mScaleModeView;
    /**
     * button for start/stop recording
     */
    private ImageView mRecordImg;
    /**
     * muxer for audio/video recording
     */
    private MediaMuxerWrapper mMuxer;

    public CameraFragment() {
        super();
        // need default constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_gl_preview_camera_with_record, container, false);
        mCameraView = (CameraGLView) rootView.findViewById(R.id.cameraView);
        mCameraView.setVideoSize(1280, 720);
        mCameraView.setOnClickListener(mOnClickListener);
        mScaleModeView = rootView.findViewById(R.id.scalemode_textview);
        updateScaleModeText();
        mRecordImg = rootView.findViewById(R.id.record_button);
        mRecordImg.setOnClickListener(mOnClickListener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        stopRecording();
        mCameraView.onPause();
        super.onPause();
    }

    /**
     * method when touch record button
     */
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.cameraView:
                    final int scale_mode = (mCameraView.getScaleMode() + 1) % 4;
                    mCameraView.setScaleMode(scale_mode);
                    updateScaleModeText();
                    break;
                case R.id.record_button:
                    if (mMuxer == null)
                        startRecording();
                    else
                        stopRecording();
                    break;
            }
        }
    };

    private void updateScaleModeText() {
        final int scale_mode = mCameraView.getScaleMode();
        mScaleModeView.setText(
                scale_mode == 0 ? "scale to fit"
                        : (scale_mode == 1 ? "keep aspect(viewport)"
                        : (scale_mode == 2 ? "keep aspect(matrix)"
                        : (scale_mode == 3 ? "keep aspect(crop center)" : ""))));
    }

    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        try {
            mRecordImg.setColorFilter(0xffff0000);    // turn red
            mMuxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            mRecordImg.setColorFilter(0);
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        mRecordImg.setColorFilter(0);    // return to default color
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };
}
