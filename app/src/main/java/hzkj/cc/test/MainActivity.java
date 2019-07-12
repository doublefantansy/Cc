package hzkj.cc.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import hzkj.cc.ccrecyclerview.CcRrefreshAndLoadMoreRecyclerView;
import hzkj.cc.ccrecyclerview.ClickItemListenner;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  CcRrefreshAndLoadMoreRecyclerView loadLayout;
  private List<String> list;
  TestAdapter adapter;
  int i;

  @Override
  protected void onResume() {
    super.onResume();
    loadLayout.resumeRefresh();
  }

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
        for (i = 0; i < 5; i++) {
          list.add("" + i);
        }
        loadLayout.refreshComplete(true);
      }
    }, 0);
    adapter = new TestAdapter(this, list);
    loadLayout = findViewById(R.id.layout);
    loadLayout.init(adapter);
    loadLayout.setClickItemListenner(new ClickItemListenner() {
      @Override
      public void click(int position) {
        startActivity(new Intent(MainActivity.this, Main2Activity.class));
      }
    });
    loadLayout.setRefreshListenner(new CcRrefreshAndLoadMoreRecyclerView.RefreshListenner() {
      @Override
      public void refresh() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            list.clear();
            for (i = 0; i < 2; i++) {
              list.add("" + i);
            }
            loadLayout.refreshComplete(false);
          }
        }, 1000);
      }
    });
    loadLayout.setLoadMoreListenner(new CcRrefreshAndLoadMoreRecyclerView.LoadMoreListenner() {
      @Override
      public void loadMore() {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            if (i > 15) {
              loadLayout.loadComplete(true, true);
            } else {
//                            i++;
//                            list.add(i + "");
              loadLayout.loadComplete(true, false);
            }
          }
        }, 1000);
      }
    });
  }
}
