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
    boolean refreshEnable = true;
    boolean loadMoreEnable = true;
    boolean isCanR = true;
    //    boolean canLoad;
    int firstVisibleItem;
    int lastVisibleItem;
    RefreshListenner refreshListenner;
    LoadMoreListenner loadMoreListenner;
    private float upY;

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

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

    public void init(BaseAdapter insideAdapter) {
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
                moveY = 0;
                canR = false;
                intercept = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                moveY = event.getRawY();
                if (!isRefresh & !isLoading & refreshEnable) {
                    isCanR = true;
                    canR = adapter.move((moveY - downY) / 4);
                    if (moveY - downY > 10) {
                        if (firstVisibleItem == 0) {
//                            return false;
                            return true;
                        }
                    }
                } else {
                    isCanR = false;
                }
                if (lastVisibleItem == insideAdapter.getItemCount()) {
                    if (!isLoading & !isRefresh & loadMoreEnable) {
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
                break;
            }
            case MotionEvent.ACTION_UP: {
                upY = event.getRawY();
                if (isCanR) {
                    if (canR) {
                        smoothScrollToPosition(0);
                        adapter.showHeader(true, true);
                        isRefresh = true;
                        if (refreshListenner != null) {
                            refreshListenner.refresh();
                        }
                    } else {
                        if (upY == downY) {
                            View childView = findChildViewUnder(event.getX(), event.getY());
                            int position = layoutManager.getPosition(childView);
                            adapter.click(position);
                        } else {
                            isRefresh = false;
                            adapter.smoothUp(true, null);
                        }
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
