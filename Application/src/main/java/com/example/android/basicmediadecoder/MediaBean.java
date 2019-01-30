package com.example.android.basicmediadecoder;

import android.animation.TimeAnimator;
import android.graphics.SurfaceTexture;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import com.example.android.common.media.MediaCodecWrapper;

public class MediaBean {
    public String filePath;
    public MediaExtractor extractor;
    public MediaCodecWrapper wrapper;
    public MediaFormat mediaFormat;
    public SurfaceTexture surfaceTexture;
    public TimeAnimator timeAnimator = new TimeAnimator();

    public MediaBean(String filePath, SurfaceTexture surface) {
        this.filePath = filePath;
        this.surfaceTexture = surface;
    }

    public static MediaBean create(String filePath, SurfaceTexture surface) {
        return new MediaBean(filePath, surface);
    }
}
