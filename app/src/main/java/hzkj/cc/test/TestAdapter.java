package hzkj.cc.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TestAdapter extends hzkj.cc.ccrecyclerview.BaseAdapter<TestAdapter.ViewHolder> {
    Context context;
    private List<String> datas; // 数据源

    public TestAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
