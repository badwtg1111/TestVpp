package com.example.android.basicmediadecoder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SelectDialogHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "VPPHolder";

    private TextView mTvKey1;
    private TextView mTvKey2;
    private TextView mTvKey3;
    private SeekBar mSeekBar;
    private TextView mTvDes;

    private DataBean mDataBean;

    public SelectDialogHolder(View itemView) {
        super(itemView);
    }

    public void bindData(View view , final DataBean dataBean) {
        mDataBean = dataBean;

        mTvKey1 = view.findViewById(R.id.tv_key1);
        mTvKey2 = view.findViewById(R.id.tv_key2);
        mTvKey3 = view.findViewById(R.id.tv_key3);
        mSeekBar = view.findViewById(R.id.sb_value);
        mTvDes = view.findViewById(R.id.tv_des);

        if (dataBean.max < 3) {
//            mSeekBar.setBackgroundResource(R.color.colorSeekBar);
            mSeekBar.setBackgroundResource(R.drawable.seek_bar_draw);
        }

        mTvKey1.setText(dataBean.key);
        mTvKey2.setText("[" + dataBean.min + " -> " + dataBean.max + "]");
        if (dataBean.type == ConvertTable.INT_TYPE) {
            mTvKey3.setText("" + dataBean.num);
        } else if (dataBean.type == ConvertTable.STRING_TYPE) {
            mTvKey3.setText(ConvertTable.getOptionString(dataBean.key, dataBean.num) +
                    "(" + dataBean.num + ")");
        }

        if (dataBean.min < 0) {
            mSeekBar.setMin(dataBean.min + ConvertTable.CONTRAST_MAX);
            mSeekBar.setMax(dataBean.max + ConvertTable.CONTRAST_MAX);
            mSeekBar.setProgress(dataBean.num + ConvertTable.CONTRAST_MAX);
        } else {
            mSeekBar.setMin(dataBean.min);
            mSeekBar.setMax(dataBean.max);
            mSeekBar.setProgress(dataBean.num);
        }

        mTvDes.setText(dataBean.description);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (dataBean.min < 0) {
                    dataBean.num = progress - ConvertTable.CONTRAST_MAX;
                } else {
                    dataBean.num = progress;
                }

                if (dataBean.type == ConvertTable.INT_TYPE) {
                    mTvKey3.setText("" + dataBean.num);
                } else {
                    mTvKey3.setText(ConvertTable.getOptionString(dataBean.key, dataBean.num) +
                            "(" + dataBean.num + ")");
                }

                Log.d(TAG, "onProgressChanged, key:" + mDataBean.key + ", v:" + mDataBean.num);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch, key:" + mDataBean.key + ", v:" + mDataBean.num);

                ConvertTable.updateMap(dataBean.key, dataBean.type,
                        dataBean.min, dataBean.max, dataBean.num, dataBean.description);
            }
        });

    }
}
