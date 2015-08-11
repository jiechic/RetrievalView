package com.jiechic.library.retrievalview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/7/29.
 */
public class LineAdapter extends RecyclerView.Adapter<LineAdapter.Holder> {

    private String key;
    private List<Item> itemList = new ArrayList<>();
    private onClickListener listener;
    private int itemWidth= ViewGroup.LayoutParams.WRAP_CONTENT;
    private ColorStateList colorStateList=ColorStateList.valueOf(Color.parseColor("#000000"));


    protected LineAdapter(@NonNull String _key, @NonNull List<Item> _itemList) {
        this.key = _key;
        this.itemList = _itemList;
    }

    protected void setListener(onClickListener _listener) {
        this.listener = _listener;
    }

    protected void setTextColor(ColorStateList textColor){
        colorStateList=textColor;
    }

    @Override
    public LineAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        final int padding = Dp2Px(parent.getContext(), 4);
        textView.setPadding(padding, padding, padding, padding);
        return new Holder(textView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.textView.setMinWidth(itemWidth);
        holder.textView.setText(itemList.get(position).getName());
        holder.textView.setTextColor(colorStateList);
        holder.textView.setSelected(itemList.get(position).isSelected());
        holder.textView.setOnClickListener((View v) -> {
            if (listener != null) {
                if (v.isSelected()) {
                    listener.onClick(false);
                } else {
                    for (int i = 0; i < itemList.size(); i++) {
                        if (position == i) {
                            itemList.get(i).setSelected(true);
                        } else {
                            itemList.get(i).setSelected(false);
                        }
                    }
                    notifyDataSetChanged();
                    listener.onClick(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemWidth(int i) {
        this.itemWidth=i;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private TextView textView;

        public Holder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected interface onClickListener {
        void onClick(boolean isChange);
    }

    public String getKey() {
        return key;
    }
}