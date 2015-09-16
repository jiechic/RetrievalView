package com.jiechic.library.retrievalview.sample;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jiechic.library.retrievalview.Item;
import com.jiechic.library.retrievalview.LineItem;
import com.jiechic.library.retrievalview.RetrievalView;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    protected String[] mMonths = new String[]{
            "", "F", "Mar", "Apr", "Mayfffffffffff", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    @Bind(R.id.retrievalView)
    RetrievalView retrievalView;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Adapter adapter;

    private String keyMonths;
    private String keyParties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new Adapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        LineItem lineItem = new LineItem("mMonths", "ALL");
        for (String s : mMonths) {
            lineItem.addItem(new Item(s, s));
        }
        retrievalView.addRetrieval(lineItem, null);
        lineItem = new LineItem("mParties", "ALL");
        for (String s : mParties) {
            lineItem.addItem(new Item(s, s));
        }
        retrievalView.addRetrieval(lineItem, null);
        retrievalView.show();
        retrievalView.setListener((List<Item> list, boolean isChange) -> {
                    keyMonths = null;
                    keyParties = null;
                    if (isChange) {
                        for (Item item : list) {
                            if (TextUtils.equals(item.getKey(), "mMonths") && !TextUtils.equals(item.getId(), LineItem.DEFAULTID)) {
                                keyMonths = item.getId();
                            }
                            if (TextUtils.equals(item.getKey(), "mParties") && !TextUtils.equals(item.getId(), LineItem.DEFAULTID)) {
                                keyParties = item.getId();
                            }
                        }
                        loadData(keyMonths, keyParties);
                    }
                }
        );
        swipeRefreshLayout.setOnRefreshListener(() -> loadData(keyMonths, keyParties));

        loadData(keyMonths, keyParties);

    }

    private void loadData(String months, String parties) {
//        Observable.concat(Observable.from(Arrays.asList(months).addAll(Arrays.asList(mParties)))
//                , Observable.from(Arrays.asList(mParties)))

        List<String> list = new ArrayList<>();
        for (String s : Arrays.asList(mMonths)) {
            list.add(s);
        }
        for (String s : Arrays.asList(mParties)) {
            list.add(s);
        }
        Observable.from(list)
                .filter(s -> (!TextUtils.equals(months, s)&& (!TextUtils.equals(parties, s))))
                .subscribe(new Subscriber<String>() {
                    List<String> list;

                    @Override
                    public void onStart() {
                        list = new ArrayList<>();
                    }

                    @Override
                    public void onCompleted() {
                        adapter.setList(list);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        list.add(s);
                    }
                });
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {
        private List<String> list;

        public void setList(List<String> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(viewHolder.LAYOUT_ID, parent, false));
        }

        @Override
        public void onBindViewHolder(viewHolder holder, int position) {
            holder.textView.setText(list.get(position));
        }

        @Override
        public int getItemCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

        class viewHolder extends RecyclerView.ViewHolder {

            static final int LAYOUT_ID = android.R.layout.simple_list_item_1;

            TextView textView;

            public viewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
}
