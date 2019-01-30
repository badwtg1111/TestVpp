/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.basicmediadecoder;


import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.media.MediaCodecWrapper;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This activity uses a {@link android.view.TextureView} to render the frames of a video decoded using
 * {@link android.media.MediaCodec} API.
 */
public class MainActivity extends Activity {
    private static final String TAG = "VPP";

    private TextureView mPlaybackView;
    private TextureView mBasaPlaybackView;
    private TimeAnimator mTimeAnimator = new TimeAnimator();

    // A utility that wraps up the underlying input and output buffer processing operations
    // into an east to use API.
    private MediaCodecWrapper mCodecWrapper;
    private MediaCodecWrapper mBaseCodecWrapper;
    private MediaExtractor mExtractor;
    TextView mAttribView = null;
    TextView mBaseAttribView = null;

    private Context mContext = null;
    TextView mTextView = null;
    TextView mFileTextView = null;
    TextView mConfigTextView = null;

    private String[] mVppItems = null;
    private String[] mAieItems = null;
    private String[] mCnrItems = null;
    private String[] mQbrItems = null;
    private String[] mCadeItems = null;
    private String[] mFrcItems = null;

    private HashMap<String, String[]> map = new HashMap<>();


    private static final String KEY_VPP = "vpp";
    private static final String KEY_AIE = "aie";
    private static final String KEY_CNR = "cnr";
    private static final String KEY_QBR = "qbr";
    private static final String KEY_CADE = "cade";
    private static final String KEY_FRC = "frc";

    private static final String MODE_HINT = "HQV_MODE_OFF, HQV_MODE_AUTO, HQV_MODE_MANUAL[0 -> 2]";
    private static final String LEVEL_HINT = "[0 -> 100]";
    private static final String CONTRAST_SATURATION_HINT = "[-50 -> 50]";
    private static final String QBR_MODE_HINT = "QBR_MODE_OFF, QBR_MODE_ON[0 -> 1]";
    private static final String AIE_HUE_HINT = "HQV_HUE_MODE_OFF, HQV_HUE_MODE_ON[0 -> 1]";
    private static final String FRC_MODE_HINT = "FRC_MODE_OFF, FRC_MODE_LOW, FRC_MODE_MED, FRC_MODE_HIGH[0 -> 3]";

    private HashMap<String, ArrayList<String>> hintMap = new HashMap<>();

    private String mDetailsKey = KEY_VPP;
    private int mKeyPosition = -1;
    private String mDetailsValue = ConvertTable.VPP_MODE;
    private int mDetailPosition = -1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mContext = this;

        mPlaybackView = (TextureView) findViewById(R.id.PlaybackView);
        mAttribView = (TextView) findViewById(R.id.AttribView);

        mBasaPlaybackView = findViewById(R.id.base_playbackView);
        mBaseAttribView = findViewById(R.id.base_attribView);

        mFileTextView = findViewById(R.id.file_text);
        mConfigTextView = findViewById(R.id.config_text);

        mTextView = findViewById(R.id.tips);

        initMaps();

    }

    private void initMaps() {
        mVppItems = getResources().getStringArray(R.array.vpp);
        mAieItems = getResources().getStringArray(R.array.aie);
        mCnrItems = getResources().getStringArray(R.array.cnr);
        mQbrItems = getResources().getStringArray(R.array.qbr);
        mCadeItems = getResources().getStringArray(R.array.cade);
        mFrcItems = getResources().getStringArray(R.array.frc);

        map.put(KEY_VPP, mVppItems);
        map.put(KEY_AIE, mAieItems);
        map.put(KEY_CNR, mCnrItems);
        map.put(KEY_QBR, mQbrItems);
        map.put(KEY_CADE, mCadeItems);
        map.put(KEY_FRC, mFrcItems);

        hintMap.put(KEY_VPP, new ArrayList(Arrays.asList(
                MODE_HINT
        )));
        hintMap.put(KEY_CADE, new ArrayList<String>(Arrays.asList(
                MODE_HINT, LEVEL_HINT, CONTRAST_SATURATION_HINT, CONTRAST_SATURATION_HINT
        )));
        hintMap.put(KEY_QBR, new ArrayList<String>(Arrays.asList(
                QBR_MODE_HINT
        )));
        hintMap.put(KEY_CNR, new ArrayList<String>(Arrays.asList(
                MODE_HINT, LEVEL_HINT
        )));
        hintMap.put(KEY_AIE, new ArrayList<String>(Arrays.asList(
                MODE_HINT, AIE_HUE_HINT, LEVEL_HINT, LEVEL_HINT
        )));
        hintMap.put(KEY_FRC, new ArrayList<String>(Arrays.asList(
                FRC_MODE_HINT
        )));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConvertTable.initDefaultSettings(mContext);
    }


    @Override
    protected void onPause() {
        super.onPause();

//        stopPlayback();
        stopPlayback(mBaseMediaBean);
        stopPlayback(mMediaBean);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_play) {
            mAttribView.setVisibility(View.VISIBLE);
//            startPlayack();
            startPlay();
            item.setEnabled(false);
        }
        return true;
    }

    private static final int REQUESTCODE_FROM_ACTIVITY = 1000;
    private static final int REQUESTCODE_FROM_CONFIG = 1001;

    private String mSelectFile = Environment.getExternalStorageDirectory().getPath() + "/mp4/21.mp4";
    private String mConfigFile = Environment.getExternalStorageDirectory().getPath() + "/vpp/config_vpp.txt";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //如果是文件选择模式，需要获取选择的所有文件的路径集合
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
                //如果是文件夹选择模式，需要获取选择的文件夹路径
                String path = data.getStringExtra("path");
                Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();

                for (String str : list) {
                    Log.d(TAG, "list:" + str);
                }

                String str = list.get(0);
                if (str != null) {
                    mSelectFile = str;
                    mFileTextView.setText(str);
                }

                Log.d(TAG, "选中的路径为:" + path + ", select file:" + mSelectFile);
            } else if (requestCode == REQUESTCODE_FROM_CONFIG) {
                List<String> list = data.getStringArrayListExtra("paths");
                String str = list.get(0);
                if (str != null) {
                    mConfigFile = str;
                    mConfigTextView.setText(str);

                    Log.d(TAG, "CONFIG file:" + mConfigFile);
                    readMapFromConfigFile();
                }
            }
        }
    }

    private Map<String, String> mConfigMap = new LinkedHashMap<>();

    private void readMapFromConfigFile() {
        mConfigMap.clear();

        try {
            FileInputStream inputStream = new FileInputStream(mConfigFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                if (line.contains(":")) {
                    String[] strings = line.split(":");
                    mConfigMap.put(strings[0], strings[1]);
                }
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found" + e);
        } catch (IOException e) {
            Log.d(TAG, "read error" + e);
        }
    }

    public void onButtonSelectConfig(View view) {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUESTCODE_FROM_CONFIG)
                .withStartPath("/storage/emulated/0/vpp")
                .withIsGreater(true)
                .withFileSize(1)
                .start();
    }

    public void onButtonSelectFile(View view) {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                .withStartPath("/storage/emulated/0/mp4")//指定初始显示路径
                .withIsGreater(true)//过滤文件大小 小于指定大小的文件
                .withFileSize(1 * 1024)//指定文件大小为500K
                .start();
    }

    public void onButtonGray(View view) {
        if (mMediaBean != null && mMediaBean.wrapper.mDecoder != null) {
            MediaCodec decoder = mMediaBean.wrapper.mDecoder;
            Log.i(TAG, "onButtonTest");
            Bundle bundle = new Bundle();
            bundle.putInt("vendor.qti-ext-vpp-aie.ltm-sat-gain", 0);
            bundle.putInt("vendor.qti-ext-vpp-aie.ltm-sat-offset", 0);

            decoder.setParameters(bundle);
        }
    }

    public void onButtonColor(View view) {
        if (mMediaBean != null && mMediaBean.wrapper.mDecoder != null) {
            MediaCodec decoder = mMediaBean.wrapper.mDecoder;
            Log.i(TAG, "onButtonTest");
            Bundle bundle = new Bundle();
            bundle.putInt("vendor.qti-ext-vpp-aie.ltm-sat-gain", 100);
            bundle.putInt("vendor.qti-ext-vpp-aie.ltm-sat-offset", 100);

            decoder.setParameters(bundle);
        }
    }

    public void onButtonReset(View view) {
        ConvertTable.initDefaultSettings(mContext);
    }

    public void onButtonStart(View view) {
        mAttribView.setVisibility(View.VISIBLE);
//        startPlayback();
        startPlay();
    }

    public void onButtonStop(View view) {
//        stopPlayback();
        stopPlayback(mBaseMediaBean);
        stopPlayback(mMediaBean);
    }

    public void onButtonSet(View view) {

        SelectDialog dialog = new SelectDialog(this);

        dialog.show();

    }

    private MediaFormat mediaFormat = null;
    private MediaFormat baseMediaFormat = null;

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }

    private void updateMediaFormatFromConfig(MediaBean bean) {
        for (Map.Entry entry : mConfigMap.entrySet()) {

            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (isNumeric(value)) {
                Log.d(TAG, "key:" + key + ", num:" + value);

                bean.mediaFormat.setInteger(key, Integer.parseInt(value));
            } else {
                Log.d(TAG, "key:" + key + ", value string:" + value);
                bean.mediaFormat.setString(key, value);
            }
        }
    }

    private void updateMediaFormat(MediaBean bean) {
        for (Map.Entry entry : ConvertTable.getMap().entrySet()) {
            Log.d(TAG, "updateMediaFormat:" + entry.getKey());

            DataBean item = (DataBean) entry.getValue();
            if (item.type == ConvertTable.INT_TYPE) {
                bean.mediaFormat.setInteger(ConvertTable.VPP_PREFIX + item.key, item.num);
            } else {
                bean.mediaFormat.setString(ConvertTable.VPP_PREFIX + item.key,
                        ConvertTable.getOptionString(item.key, item.num));
            }
        }
    }


    private MediaBean createMediaBean(String filePath , SurfaceTexture surface) {
        return MediaBean.create(filePath, surface);
    }

    private MediaBean initMedia(MediaBean bean, boolean isUpdate) {
        Uri uri = Uri.fromFile(new File(bean.filePath));
        Log.d(TAG, "startPlayback, file:" + bean.filePath + ", videoUri:" + uri);

        try {

            bean.extractor = new MediaExtractor();

            // BEGIN_INCLUDE(initialize_extractor)
            bean.extractor.setDataSource(this, uri, null);
            int nTracks = bean.extractor.getTrackCount();

            // Begin by unselecting all of the tracks in the extractor, so we won't see
            // any tracks that we haven't explicitly selected.
            for (int i = 0; i < nTracks; ++i) {
                bean.extractor.unselectTrack(i);
            }

            // Find the first video track in the stream. In a real-world application
            // it's possible that the stream would contain multiple tracks, but this
            // sample assumes that we just want to play the first one.
            for (int i = 0; i < nTracks; ++i) {
                bean.mediaFormat = bean.extractor.getTrackFormat(i);

//                if (isUpdate) updateMediaFormat(bean);
                if (isUpdate) updateMediaFormatFromConfig(bean);

                Log.d(TAG, "configure format:" + bean.mediaFormat);

                // Try to create a video codec for this track. This call will return null if the
                // track is not a video track, or not a recognized video format. Once it returns
                // a valid MediaCodecWrapper, we can break out of the loop.
                bean.wrapper = MediaCodecWrapper.fromVideoFormat(bean.mediaFormat,
                        new Surface(bean.surfaceTexture));

                if (bean.wrapper != null) {
                    bean.extractor.selectTrack(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bean;
    }


    private void timeAnimatorRun(MediaBean bean) {

        bean.timeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(final TimeAnimator animation,
                                     final long totalTime,
                                     final long deltaTime) {

                boolean isEos = ((bean.extractor.getSampleFlags() & MediaCodec
                        .BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                // BEGIN_INCLUDE(write_sample)
                if (!isEos) {
                    // Try to submit the sample to the codec and if successful advance the
                    // extractor to the next available sample to read.
                    boolean result = bean.wrapper.writeSample(bean.extractor, false,
                            bean.extractor.getSampleTime(), bean.extractor.getSampleFlags());

                    if (result) {
                        // Advancing the extractor is a blocking operation and it MUST be
                        // executed outside the main thread in real applications.
                        bean.extractor.advance();
                    }
                }
                // END_INCLUDE(write_sample)

                // Examine the sample at the head of the queue to see if its ready to be
                // rendered and is not zero sized End-of-Stream record.
                MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
                bean.wrapper.peekSample(out_bufferInfo);

                // BEGIN_INCLUDE(render_sample)
                if (out_bufferInfo.size <= 0 && isEos) {
//                    stopPlayback();
                    stopPlayback(bean);
                } else if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) {
                    // Pop the sample off the queue and send it to {@link Surface}
                    bean.wrapper.popSample(true);
                }
                // END_INCLUDE(render_sample)

            }
        });

        // We're all set. Kick off the animator to process buffers and render video frames as
        // they become available
        bean.timeAnimator.start();
    }


    private MediaBean mBaseMediaBean;
    private MediaBean mMediaBean;
    public void startPlay() {
        mBaseMediaBean = createMediaBean(mSelectFile, mBasaPlaybackView.getSurfaceTexture());
        timeAnimatorRun(initMedia(mBaseMediaBean, false));

        mMediaBean = createMediaBean(mSelectFile, mPlaybackView.getSurfaceTexture());
        timeAnimatorRun(initMedia(mMediaBean, true));

    }

    public void startPlayback() {

        // Construct a URI that points to the video resource that we want to play
//        Uri videoUri = Uri.parse("android.resource://"
//                + getPackageName() + "/"
//                + R.raw.vid_bigbuckbunny);

        Uri videoUri = Uri.fromFile(new File(mSelectFile));
        Log.d(TAG, "startPlayback, file:" + mSelectFile + ", videoUri:" + videoUri);

        try {

            mExtractor = new MediaExtractor();

            // BEGIN_INCLUDE(initialize_extractor)
            mExtractor.setDataSource(this, videoUri, null);
            int nTracks = mExtractor.getTrackCount();

            // Begin by unselecting all of the tracks in the extractor, so we won't see
            // any tracks that we haven't explicitly selected.
            for (int i = 0; i < nTracks; ++i) {
                mExtractor.unselectTrack(i);
            }

            // Find the first video track in the stream. In a real-world application
            // it's possible that the stream would contain multiple tracks, but this
            // sample assumes that we just want to play the first one.
            for (int i = 0; i < nTracks; ++i) {
                baseMediaFormat = mediaFormat = mExtractor.getTrackFormat(i);

//                updateMediaFormat();

                Log.d(TAG, "configure format:" + mediaFormat);

                // Try to create a video codec for this track. This call will return null if the
                // track is not a video track, or not a recognized video format. Once it returns
                // a valid MediaCodecWrapper, we can break out of the loop.
                mCodecWrapper = MediaCodecWrapper.fromVideoFormat(mediaFormat,
                        new Surface(mPlaybackView.getSurfaceTexture()));

                mBaseCodecWrapper = MediaCodecWrapper.fromVideoFormat(baseMediaFormat,
                        new Surface(mBasaPlaybackView.getSurfaceTexture()));
                if (mCodecWrapper != null && mBaseCodecWrapper != null) {
                    mExtractor.selectTrack(i);
                    break;
                }
            }
            // END_INCLUDE(initialize_extractor)


            // By using a {@link TimeAnimator}, we can sync our media rendering commands with
            // the system display frame rendering. The animator ticks as the {@link Choreographer}
            // receives VSYNC events.
            mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
                @Override
                public void onTimeUpdate(final TimeAnimator animation,
                                         final long totalTime,
                                         final long deltaTime) {

                    boolean isEos = ((mExtractor.getSampleFlags() & MediaCodec
                            .BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                    // BEGIN_INCLUDE(write_sample)
                    if (!isEos) {
                        // Try to submit the sample to the codec and if successful advance the
                        // extractor to the next available sample to read.
                        boolean result = mCodecWrapper.writeSample(mExtractor, false,
                                mExtractor.getSampleTime(), mExtractor.getSampleFlags());

                        if (result) {
                            // Advancing the extractor is a blocking operation and it MUST be
                            // executed outside the main thread in real applications.
                            mExtractor.advance();
                        }
                    }
                    // END_INCLUDE(write_sample)

                    // Examine the sample at the head of the queue to see if its ready to be
                    // rendered and is not zero sized End-of-Stream record.
                    MediaCodec.BufferInfo out_bufferInfo = new MediaCodec.BufferInfo();
                    mCodecWrapper.peekSample(out_bufferInfo);

                    // BEGIN_INCLUDE(render_sample)
                    if (out_bufferInfo.size <= 0 && isEos) {
//                        stopPlayback();
                        stopPlayback();

                    } else if (out_bufferInfo.presentationTimeUs / 1000 < totalTime) {
                        // Pop the sample off the queue and send it to {@link Surface}
                        mCodecWrapper.popSample(true);
                    }
                    // END_INCLUDE(render_sample)

                }
            });

            // We're all set. Kick off the animator to process buffers and render video frames as
            // they become available
            mTimeAnimator.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback() {
        if (mTimeAnimator != null && mTimeAnimator.isRunning()) {
            mTimeAnimator.end();
        }

        if (mCodecWrapper != null) {
            mCodecWrapper.stopAndRelease();
            mExtractor.release();
            mExtractor = null;
            mCodecWrapper = null;
        }
    }

    public void stopPlayback(MediaBean bean) {
        if (bean == null) return;
        if (bean.timeAnimator != null && bean.timeAnimator.isRunning()) {
            bean.timeAnimator.end();
        }

        if (bean.wrapper != null) {
            bean.wrapper.stopAndRelease();
            bean.extractor.release();
            bean.extractor = null;
            bean.wrapper = null;
        }
    }


}
