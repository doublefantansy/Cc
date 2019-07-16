package hzkj.cc.ccrecyclerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private Context context;
  public static int HEADERTYPE = -1;
  public static int FOOTTYPE = 1;
  BaseAdapter adapter;
  FootHolder footholder;
  HeaderHolder headerHolder;
  ObjectAnimator footerAnimator;
  ObjectAnimator headAnimator;
  ValueAnimator animator;
  boolean isEnd;
  int headerHeight;
  int footerHeight;
  CcRrefreshAndLoadMoreRecyclerView.CallBack callBack;
  ClickItemListenner itemListenner;
  int mPrevious;

  public void setItemListenner(ClickItemListenner itemListenner) {
    this.itemListenner = itemListenner;
  }

  public void setCallBack(CcRrefreshAndLoadMoreRecyclerView.CallBack callBack) {
    this.callBack = callBack;
  }

  public MyAdapter(BaseAdapter adapter, Context context) {
    // 初始化变量
    this.adapter = adapter;
    this.context = context;
  }

  void click(int p) {
    if (p == 0 | p == getItemCount() - 1) {
      return;
    }
    if (itemListenner != null) {
      itemListenner.click(p - 1);
    }
  }

  @Override
  public int getItemCount() {
    return adapter.getItemCount() + 2;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return HEADERTYPE;
    } else if (position == getItemCount() - 1) {
      return FOOTTYPE;
    } else {
      return adapter.getItemViewType(position);
    }
  }

  void smoothDown(final String text) {
    footholder.tips.setVisibility(View.GONE);
    footholder.loadingText.setText(text);
    new android.os.Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        ValueAnimator animator = ValueAnimator.ofInt(footerHeight, 0);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            ViewGroup.LayoutParams layoutParams = footholder.itemView.getLayoutParams();
            layoutParams.height = (int) animation.getAnimatedValue();
            footholder.itemView.setLayoutParams(layoutParams);
          }
        });
        animator.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animation) {
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            callBack.callBack();
          }

          @Override
          public void onAnimationCancel(Animator animation) {
          }

          @Override
          public void onAnimationRepeat(Animator animation) {
          }
        });
        animator.start();
      }
    }, 1000);
//        footholder.tips.setVisibility(View.GONE);
//        footholder.loadingText.setText(text);
//        footholder.loadingText.setTextColor(context.getResources()
//                .getColor(R.color.red));
//        callBack.callBack();
  }

  //
  public void smoothUp(final boolean toTop, String text) {
    int delay = 0;
    if (text != null
        ) {
      headerHolder.loading.setVisibility(View.GONE);
      headerHolder.text.setText(text);
      if (text.equals("刷新失败")) {
        headerHolder.text.setTextColor(context.getResources()
            .getColor(R.color.red));
      } else {
        headerHolder.text.setTextColor(context.getResources()
            .getColor(R.color.myBlue));
      }
      delay = 1000;
    }
    new android.os.Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        ValueAnimator animator = ValueAnimator
            .ofInt(headerHolder.itemView.getPaddingTop(), toTop ? -headerHeight : 0);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        mPrevious = -1;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            if (((Integer) animation.getAnimatedValue()) != mPrevious) {
              Log.d("lsycc", (Integer) animation.getAnimatedValue() + "");
              headerHolder.itemView.setPadding(headerHolder.itemView.getPaddingLeft(),
                  (int) animation.getAnimatedValue(),
                  headerHolder.itemView.getPaddingRight(),
                  headerHolder.itemView.getPaddingBottom());
              headerHolder.itemView.invalidate();
            }
            mPrevious = (Integer) animation.getAnimatedValue();
          }
        });
        animator.start();
      }
    }, delay);
  }

  public void showHeader(boolean showLoading, boolean isSuccess) {
    if (showLoading) {
      smoothUp(false, null);
      headerHolder.loading.setVisibility(View.VISIBLE);
      headerHolder.text.setTextColor(context.getResources()
          .getColor(R.color.myGray));
      headerHolder.text.setText("正在刷新中");
      ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams())
          .removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
      ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams())
          .addRule(RelativeLayout.CENTER_IN_PARENT);
    } else {
      smoothUp(true, isSuccess ? "刷新成功" : "刷新失败");
    }
//        Log.d("lsyssd", headerHolder.itemView.getPaddingTop() + "");
  }

  public void showFooter(int status) {
    if (status == 0) {
      footholder.loadingText.setTextColor(context.getResources()
          .getColor(R.color.myGray));
      footholder.tips.setVisibility(View.VISIBLE);
      footholder.loadingText.setText("正在加载中");
    } else if (status == 1) {
      footholder.tips.setVisibility(View.GONE);
      footholder.loadingText.setText("");
    } else if (status == 2) {
      footholder.loadingText.setTextColor(context.getResources()
          .getColor(R.color.myBlue));
      footholder.tips.setVisibility(View.GONE);
      footholder.loadingText.setText("已经全部加载完毕");
//            callBack.callBack();
    } else {
      footholder.loadingText.setTextColor(context.getResources()
          .getColor(R.color.red));
      footholder.tips.setVisibility(View.GONE);
      footholder.loadingText.setText("网络错误");
    }
//        ViewGroup.LayoutParams layoutParams = footholder.itemView.getLayoutParams();
  }

  public void resumeShowHeader() {
//    adapter.notifyDataSetChanged();
    headerHolder.itemView.setPadding(headerHolder.itemView.getPaddingLeft(), 0,
        headerHolder.itemView.getPaddingRight(), headerHolder.itemView.getPaddingBottom());
    headerHolder.loading.setVisibility(View.VISIBLE);
    headerHolder.text.setTextColor(context.getResources()
        .getColor(R.color.myGray));
    headerHolder.text.setText("正在刷新中");
  }

  class HeaderHolder extends RecyclerView.ViewHolder {

    private ImageView loading;
    private RelativeLayout layout;
    private TextView text;

    public HeaderHolder(final View itemView) {
      super(itemView);
      loading = itemView.findViewById(R.id.loading);
      layout = itemView.findViewById(R.id.pullToRefreshPart);
      text = itemView.findViewById(R.id.pullToRefreshText);
      itemView.measure(0, 0);
      headerHeight = itemView.getMeasuredHeight();
      loading.setVisibility(View.VISIBLE);
      text.setTextColor(context.getResources()
          .getColor(R.color.myGray));
      text.setText("正在刷新中");
//            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
//            layoutParams.topMargin = -headerHeight;
//      itemView.setPadding(itemView.getPaddingLeft(), -headerHeight,
//          itemView.getPaddingRight(), itemView.getPaddingBottom());
      Log.d("ccnb1111", itemView.getPaddingTop() + "");
//            itemView.invalidate();
//            itemView.setLayoutParams(layoutParams);
    }
  }

  public boolean move(float distance) {
    headerHolder.text.setTextColor(context.getResources()
        .getColor(R.color.myGray));
    headerHolder.loading.setVisibility(View.GONE);
    ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams())
        .removeRule(RelativeLayout.CENTER_IN_PARENT);
    ((RelativeLayout.LayoutParams) headerHolder.layout.getLayoutParams())
        .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    headerHolder.text.setText("下拉可刷新");
    if (distance < 0) {
      return false;
    }
//        Log.d("lsyssd", headerHolder.itemView.getPaddingTop() + "");
////        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) headerHolder.itemView.getLayoutParams();
////        layoutParams.topMargin = (int) distance - headerHeight;
////        headerHolder.itemView.measure(0, 0);
    headerHolder.itemView
        .setPadding(headerHolder.itemView.getPaddingLeft(), (int) distance - headerHeight,
            headerHolder.itemView.getPaddingRight(), headerHolder.itemView.getPaddingBottom());
    Log.d("lsyssd", headerHolder.itemView.getPaddingTop() + "");
    if (headerHolder.itemView.getPaddingTop() > 0) {
      headerHolder.text.setText("释放可刷新");
//            headerHolder.itemView.setLayoutParams(layoutParams);
      return true;
    } else {
      headerHolder.text.setText("下拉可刷新");
//            headerHolder.itemView.setLayoutParams(layoutParams);
      return false;
    }
  }

  class FootHolder extends RecyclerView.ViewHolder {

    ImageView tips;
    TextView loadingText;

    public FootHolder(View itemView) {
      super(itemView);
      tips = itemView.findViewById(R.id.loading);
      loadingText = itemView.findViewById(R.id.message);
      footerHeight = itemView.getLayoutParams().height;
      tips.setVisibility(View.GONE);
      loadingText.setText("");
//            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
//            layoutParams.height = 0;
      Log.d("ccnb111", footerHeight + "");
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == FOOTTYPE) {
      footholder = new FootHolder(LayoutInflater.from(context)
          .inflate(R.layout.footview, parent, false));
      footerAnimator = ObjectAnimator.ofFloat(((FootHolder) footholder).tips, "rotation", 360f);
      footerAnimator.setDuration(2000);
      footerAnimator.setRepeatCount(ValueAnimator.INFINITE);
//            footerAnimator.start();
      return footholder;
    } else if (viewType == HEADERTYPE) {
      headerHolder = new HeaderHolder(LayoutInflater.from(context)
          .inflate(R.layout.headview, parent, false));
      headAnimator = ObjectAnimator.ofFloat(headerHolder.loading, "rotation", 360f);
      headAnimator.setDuration(2000);
      headAnimator.setRepeatCount(ValueAnimator.INFINITE);
      return headerHolder;
    } else {
      return adapter.onCreateViewHolder(parent, viewType);
    }
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof FootHolder) {
      footerAnimator.start();
    } else if (holder instanceof HeaderHolder) {
      headAnimator.start();
      Log.d("ccllsy", headerHolder.itemView.getMeasuredHeight() + "");
    } else {
      adapter.onBindViewHolder(holder, position - 1);
    }
  }
}


