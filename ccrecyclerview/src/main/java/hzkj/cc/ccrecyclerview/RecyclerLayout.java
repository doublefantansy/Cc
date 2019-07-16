package hzkj.cc.ccrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView.LoadMoreListenner;
import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView.RefreshListenner;
import hzkj.cc.stateful.StateFulLayout;

public class RecyclerLayout extends LinearLayout {

  StateFulLayout stateFulLayout;
  CcRrefreshAndLoadMoreRecyclerView recyclerView;
  BaseAdapter adapter;
  //  RefreshListenner refreshListenner;
//  LoadMoreListenner loadMoreListenner;
  StateLayoutRefreshListenner stateLayoutRefreshListenner;

  public void setRefreshEnable(boolean refreshEnable) {
    recyclerView.setRefreshEnable(refreshEnable);
  }

  public void setClickItemListenner(ClickItemListenner clickItemListenner) {
    recyclerView.setClickItemListenner(clickItemListenner);
  }

  public void setLoadEnable(boolean loadEnable) {
    recyclerView.setLoadMoreEnable(loadEnable);
  }

  public void setRefreshListenner(RefreshListenner refreshListenner) {
    recyclerView.setRefreshListenner(refreshListenner);
  }

  public void setLoadMoreListenner(
      LoadMoreListenner loadMoreListenner) {
    recyclerView.setLoadMoreListenner(loadMoreListenner);
  }

  public void setStateLayoutRefreshListenner(
      StateLayoutRefreshListenner stateLayoutRefreshListenner) {
    this.stateLayoutRefreshListenner = stateLayoutRefreshListenner;
  }

  public RecyclerLayout(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(BaseAdapter adapter) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.recycler_layout, this, false);
    stateFulLayout = view.findViewById(R.id.stateLayout);
    recyclerView = view.findViewById(R.id.recyclerView);
    stateFulLayout.init(new StateFulLayout.RefreshListenner() {
      @Override
      public void refresh() {
        stateFulLayout.showState(StateFulLayout.LOADING);
        if (stateLayoutRefreshListenner != null) {
          stateLayoutRefreshListenner.refresh();
        }
      }
    }, recyclerView);
    this.adapter = adapter;
    recyclerView.init(this.adapter);
    stateFulLayout.showState(StateFulLayout.LOADING);
    addView(view);
  }

  public void loadComplete(boolean isSuccess) {
    if (adapter.getItemCount() == 0) {
      recyclerView.loadComplete(true, isSuccess);
    } else {
      recyclerView.loadComplete(false, isSuccess);
    }
  }

  public void refreshComplete(boolean isSuccess) {
    if (isSuccess) {
      if (adapter.getItemCount() == 0) {
        stateFulLayout.showState(StateFulLayout.EMPTY);
      } else {
        stateFulLayout.showState(StateFulLayout.CONTENT);
        recyclerView.refreshComplete(isSuccess);
      }
    } else {
      if (adapter.getItemCount() == 0) {
        stateFulLayout.showState(StateFulLayout.NETERROR);
      } else {
        stateFulLayout.showState(StateFulLayout.CONTENT);
        recyclerView.refreshComplete(isSuccess);
      }
    }
  }

  public void onResume() {
    if (adapter.getItemCount() == 0) {
      stateFulLayout.showState(StateFulLayout.LOADING);
      stateLayoutRefreshListenner.refresh();
    } else {
      recyclerView.resumeRefresh();
    }
  }


  public interface StateLayoutRefreshListenner {

    void refresh();
  }
}
