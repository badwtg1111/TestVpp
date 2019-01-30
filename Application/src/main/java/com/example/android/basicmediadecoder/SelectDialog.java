package com.example.android.basicmediadecoder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SelectDialog extends Dialog {

    private RecyclerView mRecyView;
    private Button mBtnCancel;
    private SelectDialogAdapter mAdapter;
    private List<DataBean> mDatas;

    public SelectDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select);

        initView();
        initEvn();
    }

    private void initView() {
        mRecyView = findViewById(R.id.recy_view);
        mBtnCancel = findViewById(R.id.btn_submit);

        mRecyView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mDatas = initData();
        mAdapter = new SelectDialogAdapter(mDatas);
        mRecyView.setAdapter(mAdapter);
    }

    private List<DataBean> initData() {
        List<DataBean> items = new ArrayList<>(ConvertTable.getMap().values());
        return items;
    }

    private void initEvn() {
        mBtnCancel.setOnClickListener((v) -> dismiss());
    }

}
