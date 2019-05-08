package hzkj.cc.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView;

public class MainActivity extends AppCompatActivity {
    CcRrefreshAndLoadMoreRecyclerView loadLayout;
    private List<String> list;
    TestAdapter adapter;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button down = findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        list = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    list.add("" + i);
                }
                loadLayout.notify1();
            }
        }, 3000);
        adapter = new TestAdapter(this, list);
        loadLayout = findViewById(R.id.layout);
        loadLayout.init(adapter);
        loadLayout.setLoadMoreListenner(new CcRrefreshAndLoadMoreRecyclerView.LoadMoreListenner() {
            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.add("sd");
                        loadLayout.loadComplete(false);
                    }
                }, 3000);
            }
        });
    }
}
