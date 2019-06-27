package hzkj.cc.ccrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class CcRrefreshAndLoadMoreRecyclerView1 extends LinearLayout {

    RecyclerView recyclerView;

    public CcRrefreshAndLoadMoreRecyclerView1(Context context) {
        super(context);
    }

    public CcRrefreshAndLoadMoreRecyclerView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.item, null);
        recyclerView = view.findViewById(R.id.recyclerView);
        addView(view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
    }

    public void init(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    interface RefreshListenner {
        void refresh();
    }

    interface LoadMoreListenner {
        void loadMore();
    }
}
