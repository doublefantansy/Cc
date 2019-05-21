package hzkj.cc.ccrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CcRrefreshAndLoadMoreRecyclerView extends RecyclerView {
    //    RecyclerView recyclerView;
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
//        recyclerView = new RecyclerView(getContext());
        this.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.setLayoutManager(layoutManager);
        this.setAdapter(adapter);
        this.setItemAnimator(new DefaultItemAnimator());
        this.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        adapter.setCallBack(new CallBack() {
            @Override
            public void callBack() {
                adapter.showFooter(false);
            }
        });
    }

    public void refreshComplete(boolean isSuccess) {
        isRefresh = false;
        if (isSuccess) {
            adapter.notifyDataSetChanged();
        }
        adapter.showHeader(false, isSuccess);
    }

    public void loadComplete(boolean isEmpty, boolean isSuccess) {
        isLoading = false;
        if (isSuccess) {
            if (isEmpty) {
                adapter.smoothDown("已到最后");
//            adapter.smoothDown("加载成功");
            } else {
                adapter.notifyDataSetChanged();
                adapter.showFooter(false);
            }
        } else {
            adapter.smoothDown("加载失败");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                    canR = adapter.move((moveY - downY) / 4);
                    if (moveY - downY > 0) {
                        if (firstVisibleItem == 0) {
//                            return false;
                            return true;
                        }
                    } else {
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
            }
            case MotionEvent.ACTION_UP: {
                if (isCanR) {
                    if (canR) {
                        smoothScrollToPosition(0);
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
        return super.onTouchEvent(event);
    }

    public interface CallBack {
        void callBack();
    }

    public interface RefreshListenner {
        void refresh();
    }

    public interface LoadMoreListenner {
        void loadMore();
    }
}
