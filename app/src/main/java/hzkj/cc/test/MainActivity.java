package hzkj.cc.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView.LoadMoreListenner;
import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView.RefreshListenner;
import hzkj.cc.ccrecyclerview.ClickItemListenner;
import hzkj.cc.ccrecyclerview.RecyclerLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  RecyclerLayout loadLayout;
  private List<String> list;
  TestAdapter adapter;
  int i;

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
        list.clear();
        for (i = 0; i < 5; i++) {
          list.add("" + i);
        }
        loadLayout.refreshComplete(true);
      }
    }, 1000);
    adapter = new TestAdapter(this, list);
    loadLayout = findViewById(R.id.layout);
    loadLayout.init(adapter);
    loadLayout.setRefreshListenner(new RefreshListenner() {
      @Override
      public void refresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            list.clear();
            for (i = 0; i < 5; i++) {
              list.add("" + i);
            }
            loadLayout.refreshComplete(true);
          }
        }, 1000);
      }
    });
    loadLayout.setClickItemListenner(new ClickItemListenner() {
      @Override
      public void click(int position) {
        Log.d("ccc", position + "");
      }
    });
    loadLayout.setLoadMoreListenner(new LoadMoreListenner() {
      @Override
      public void loadMore() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            list.add("" + i);
            loadLayout.loadComplete(true);
          }
        }, 1000);
      }
    });
  }
}
