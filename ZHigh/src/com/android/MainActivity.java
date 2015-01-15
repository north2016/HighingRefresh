package com.android;

import com.android.HighingRefreshListView.OnRefreshListener;
import com.high.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static Toast mToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final HighingRefreshListView listView = (HighingRefreshListView) findViewById(R.id.listView);
		
		ListAdapter mListAdapter = new ListAdapter(MainActivity.this);
		listView.setAdapter(mListAdapter);
		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 刷新
				Log.d("MainActivity", "进入刷新页面");
				showToast(MainActivity.this, "正在刷新");
				new Thread() {
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								listView.onRefreshComplete(); // 刷新完毕�?
								showToast(MainActivity.this, "刷新结束");
							}
						});
					};
				}.start();
			}
		});

	}

	public static void showToast(Context context, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, 500);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}
}
