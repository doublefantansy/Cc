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
import android.widget.Toast;

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
    int lastCompleteVisibleItem;
    RefreshListenner refreshListenner;
    LoadMoreListenner loadMoreListenner;
    private float upY;
    float temp;
    ClickItemListenner listenner;
    private boolean first = true;
    private boolean isMove;

    public void setClickItemListenner(ClickItemListenner listenner) {
        this.listenner = listenner;
        adapter.setItemListenner(listenner);
    }

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
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                lastCompleteVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.d("ccnb11111", lastCompleteVisibleItem + "");
            }
        });
        adapter.setCallBack(new CallBack() {
            @Override
            public void callBack() {
                adapter.showFooter(false);
            }
        });
    }

    public void refreshComplete(boolean isSuccess, boolean isFirst) {
        isRefresh = false;
        smoothScrollToPosition(0);
        if (isSuccess) {
            adapter.notifyDataSetChanged();
        }
        if (!isFirst) {
            adapter.showHeader(false, isSuccess);
            Toast.makeText(getContext(), "刷新完成", Toast.LENGTH_SHORT)
                    .show();
        }
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
                    if (firstVisibleItem == 1 | firstVisibleItem == 0) {
                        canR = adapter.move((moveY - downY) / 4);
                    } else {
                        canR = false;
                    }
                    if (adapter.headerHolder.itemView.getPaddingTop() > -adapter.headerHeight) {
                        isMove = true;
                    } else {
                        isMove = false;
                    }
                    if (firstVisibleItem == 0 & isMove) {
                        return true;
                    }
                } else {
                    isCanR = false;
                }
                if (lastCompleteVisibleItem == insideAdapter.getItemCount() + 1 & adapter.footholder != null) {
                    if (!isLoading & !isRefresh & loadMoreEnable) {
                        Log.d("cctag", "in1");
                        if (moveY - downY < 0) {
                            if (loadMoreListenner != null) {
                                adapter.showFooter(true);
                                loadMoreListenner.loadMore();
                                isLoading = true;
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                upY = event.getRawY();
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
                if (upY == downY) {
                    View childView = findChildViewUnder(event.getX(), event.getY());
                    if (childView != null) {
                        int position = layoutManager.getPosition(childView);
                        adapter.click(position);
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
