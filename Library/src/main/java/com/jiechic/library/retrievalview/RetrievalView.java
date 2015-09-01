package com.jiechic.library.retrievalview;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jiechic.library.retrieval.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/7/27.
 */
public class RetrievalView extends RelativeLayout {

    /**
     * Contant all item
     */
    private List<LineItem> listMap = new ArrayList<>();

    private int line_heght;
    private int itemMinWith;
    private ColorStateList dividerColor;
    private ColorStateList itemTextColor;

    LinearLayout.LayoutParams recyclerParams;

    private String NoSelectedText = "";

    private Boolean isAnimationRunning = false;
    private Boolean isOpened = false;
    private Integer duration;
    private LinearLayout contentView;
    private TextView headerView;
    private Animation animation;

    private List<LineAdapter> adapterList = new ArrayList<>();

    private onChangeListener listener;

    /**
     * init Context
     *
     * @param context
     */

    public RetrievalView(Context context) {
        this(context, null);
    }

    public RetrievalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RetrievalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RetrievalLayout,
                0, 0);
        try {
            //get animation durtion time
            duration = typedArray.getInt(R.styleable.RetrievalLayout_expandDuration, getResources().getInteger(android.R.integer.config_longAnimTime));
            dividerColor = typedArray.getColorStateList(R.styleable.RetrievalLayout_dividerColor);
            if (dividerColor==null){
                dividerColor = ColorStateList.valueOf(Color.parseColor("#1F000000"));
            }
            line_heght = typedArray.getDimensionPixelSize(R.styleable.RetrievalLayout_line_height, getResources().getDimensionPixelOffset(R.dimen.line_height));
            itemMinWith = typedArray.getDimensionPixelSize(R.styleable.RetrievalLayout_item_MinWidth, getResources().getDimensionPixelOffset(R.dimen.itemMinWidth));
            itemTextColor = typedArray.getColorStateList(R.styleable.RetrievalLayout_item_TextColor);
            if (itemTextColor == null) {
                itemTextColor = getResources().getColorStateList(R.color.retrieavl_text_selector);
            }
        } finally {
            typedArray.recycle();
        }


        final View rootView = View.inflate(context, R.layout.view_retrieval, this);
        headerView = (TextView) rootView.findViewById(R.id.view_headerView);
        headerView.setVisibility(VISIBLE);
        contentView = (LinearLayout) rootView.findViewById(R.id.view_contentView);
        contentView.setVisibility(GONE);

        //init recyclerParams
        recyclerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        headerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAnimationRunning) {
                    if (contentView.getVisibility() == VISIBLE)
                        collapse(contentView);
                    else
                        expand(contentView);

                    isAnimationRunning = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isAnimationRunning = false;
                        }
                    }, duration);
                }
            }
        });
    }

    public void addRetrieval(@NonNull LineItem lineItem, @Nullable String defaultId) {
        listMap.add(lineItem);
        String tempDefaultId = TextUtils.isEmpty(defaultId) ? LineItem.DEFAULTID : defaultId;
        for (Item item : lineItem.getList()) {
            if (TextUtils.equals(item.getId(), tempDefaultId)) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        refreshLayout();
    }

    public void clearRetrieval(){
        listMap.clear();
        refreshLayout();
    }

    public void setNoSelectedText(String noSelectedText){
        this.NoSelectedText=noSelectedText;
    }

    public void setListener(onChangeListener listener) {
        this.listener = listener;
    }

    private void refreshLayout() {
        headerView.setHeight(line_heght);
        contentView.removeAllViews();
        adapterList.clear();

        for (LineItem item : listMap) {
            LineAdapter adapter = new LineAdapter(item.getKey(), item.getList());
            adapter.setTextColor(itemTextColor);
            adapter.setItemWidth(itemMinWith);
            adapterList.add(adapter);
            adapter.setListener((boolean isChange) -> {
                        if (listener != null) {
                            List<Item> list = new ArrayList<>();
                            for (LineItem lineItem : listMap) {
                                for (Item item1 : lineItem.getList()) {
                                    if (item1.isSelected() && !TextUtils.equals(item1.getId(), LineItem.DEFAULTID)) {
                                        list.add(item1);
                                    }
                                }
                            }
                            listener.Change(list, isChange);
                        }
                        reFreshText();
                    }
            );
            RecyclerView recyclerView = new RecyclerView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, line_heght);
            recyclerView.setLayoutParams(layoutParams);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            contentView.addView(recyclerView);
            if (contentView.getChildCount() > 0 && contentView.getChildCount() != listMap.size()) {
                View dividerView = new View(getContext());
                LinearLayout.LayoutParams temp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Dp2Px(getContext(), 0.5f));
                dividerView.setLayoutParams(temp);
                dividerView.setBackgroundColor(dividerColor.getDefaultColor());
                contentView.addView(dividerView);
            }
        }
        contentView.requestLayout();
        contentView.invalidate();

        reFreshText();
    }


    private void reFreshText() {
        StringBuilder sb = new StringBuilder();
        for (LineItem item : listMap) {
            for (Item it : item.getList()) {
                if (it.isSelected() && !TextUtils.equals(it.getId(), LineItem.DEFAULTID)) {
                    if (sb.length() > 0) {
                        sb.append("|").append(it.getName());
                    } else {
                        sb.append(it.getName());
                    }
                }
            }
        }
        if (sb.length() == 0) {
            sb.append(NoSelectedText);
        }
        headerView.setText(sb.toString());
    }

    private void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int mHeight = 0;
        for (int i = 0; i < ((LinearLayout) v).getChildCount(); i++) {
            mHeight = mHeight + ((LinearLayout) v).getChildAt(i).getLayoutParams().height;
        }
//        final int targetHeight = v.getMeasuredHeight();
        final int targetHeight = mHeight;
        v.getLayoutParams().height = 0;
        v.setVisibility(VISIBLE);
        isOpened = true;


        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    headerView.setVisibility(INVISIBLE);
                }
                v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        isOpened = false;

        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    headerView.setVisibility(VISIBLE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    public Boolean isOpened() {
        return isOpened;
    }

    public void show() {
        if (!isAnimationRunning) {
            expand(contentView);
            isAnimationRunning = true;
            new Handler().postDelayed(() ->
                    isAnimationRunning = false, duration);
        }
    }


    public void hide() {
        if (!isAnimationRunning) {
            collapse(contentView);
            isAnimationRunning = true;
            new Handler().postDelayed(() ->
                    isAnimationRunning = false, duration);
        }
    }

    /**
     * CallBack to Update forUser item
     */
    public interface onChangeListener {
        void Change(List<Item> list, boolean isChange);
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
