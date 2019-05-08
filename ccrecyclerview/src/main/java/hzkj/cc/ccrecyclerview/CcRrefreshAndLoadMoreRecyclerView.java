package hzkj.cc.ccrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class CcRrefreshAndLoadMoreRecyclerView extends LinearLayout implements View.OnTouchListener {
    RecyclerView recyclerView;
    RecyclerView.Adapter insideAdapter;
    float downY = 0;
    float moveY;
    MyAdapter adapter;
    private LinearLayoutManager layoutManager;
    boolean isRefresh = false;
    boolean isLoading = false;
    boolean canR;
    boolean canL;
    boolean isCanR = true;
    //    boolean canLoad;
    int firstVisibleItem;
    int lastVisibleItem;
    RefreshListenner refreshListenner;
    LoadMoreListenner loadMoreListenner;

    public void setRefreshListenner(RefreshListenner refreshListenner) {
        this.refreshListenner = refreshListenner;
    }

    public void setLoadMoreListenner(LoadMoreListenner loadMoreListenner) {
        this.loadMoreListenner = loadMoreListenner;
    }

    public CcRrefreshAndLoadMoreRecyclerView(Context context) {
        super(context);
    }

    public CcRrefreshAndLoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(RecyclerView.Adapter insideAdapter) {
        this.insideAdapter = insideAdapter;
        adapter = new MyAdapter(insideAdapter, getContext());
        initRecyclerView();
    }

    public void update() {
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        setOrientation(VERTICAL);
        recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        addView(recyclerView);
    }

    public void refreshComplete(boolean isSuccess) {
        isRefresh = false;
        if (isSuccess) {
            adapter.notifyDataSetChanged();
        }
        adapter.showHeader(false, isSuccess);
    }

    public void loadComplete(boolean isEmpty) {
        isLoading = false;
        adapter.showFooter(false);
        if (isEmpty) {
            adapter.smoothDown("暂无数据");
//            adapter.smoothDown("加载成功");
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean intercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downY = event.getRawY();
                intercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                moveY = event.getRawY();
                if (!isRefresh & !isLoading) {
                    isCanR = true;
                    canR = adapter.move((moveY - downY) / 3);
                    if (moveY - downY >= 0) {
                        if (firstVisibleItem == 0) {
                            return true;
                        }
                    }
                } else {
                    isCanR = false;
                }
                if (lastVisibleItem == insideAdapter.getItemCount()) {
                    if (!isLoading & !isRefresh) {
                        Log.d("cctag", "in1");
                        if (moveY - downY < 0) {
                            if (loadMoreListenner != null) {
                                adapter.showFooter(true);
                                loadMoreListenner.loadMore();
                            }
                            isLoading = true;
                        }
                    }
                }
                return false;
            }
            case MotionEvent.ACTION_UP: {
                if (isCanR) {
                    if (canR) {
                        adapter.showHeader(true, true);
                        isRefresh = true;
                        if (refreshListenner != null) {
                            refreshListenner.refresh();
                        }
                    } else {
                        isRefresh = false;
                        adapter.smoothUp(true, null);
                    }
                }
                break;
            }
        }
        return intercept;
    }

    public  interface RefreshListenner {
        void refresh();
    }

    public interface LoadMoreListenner {
        void loadMore();
    }
}
