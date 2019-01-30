package com.example.android.basicmediadecoder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SelectDialogAdapter extends RecyclerView.Adapter<SelectDialogHolder> {


    private List<DataBean> datas = new ArrayList<>();

    public SelectDialogAdapter(List<DataBean> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
    }

    @Override
    public SelectDialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recy_item, parent, false);
        return new SelectDialogHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectDialogHolder holder, int position) {
        holder.bindData(holder.itemView, datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
