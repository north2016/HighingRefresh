package com.android;

import com.high.R;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class HighingRefreshListView extends ListView implements
		OnScrollListener {
	// 下拉刷新状�?
	private enum RefreshState {
		Initial, // 可拉伸状�?		
		Refreshing// 刷新状�?
	}

	// 下拉刷新的接�?	
	public interface OnRefreshListener {
		void onRefresh();
	}

	private Context mContext;
	private OnScrollListener mOnScrollListener;// 滚动监听
	private OnRefreshListener mOnRefreshListener;// 下拉刷新监听

	private HeartView heartview;// 顶部水滴控件
	private View headerView;// 顶部加载控件
	TextView tv_refresh;

	private RefreshState refreshState = RefreshState.Initial;// 初始化为可拉伸状�?	
	private int headerViewHeight;// 顶部高度
	private int slimeViewHeight;// 水滴高度

	private float oldY = 0;// Y轴原来高�?
	private float LastY = 0;// Y轴原来高�?
	private int LastHeight;// 顶部视图原来高度
	boolean ishasrefresh = false;// 是否已经刷新过了

	private boolean mIsTop = true;
	private boolean mIsGoingtoRefresh = false;
	private boolean isInFrefresh = false;

	public HighingRefreshListView(Context context) {
		super(context);
		mContext = context;
		this.init(context);
	}

	public HighingRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.init(context);
	}

	public HighingRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		this.init(context);
	}

	/**
	 * 初始�?	 * 
	 * @param context
	 */

	private void init(Context context) {
		headerView = LayoutInflater.from(context).inflate(R.layout.header_view,
				null);
		heartview = (HeartView) headerView.findViewById(R.id.heartview);
		tv_refresh = (TextView) headerView.findViewById(R.id.tv_refresh);
		addHeaderView(headerView);

		super.setOnScrollListener(this);
		measureView(headerView);
		measureView(tv_refresh);
		measureView(heartview);
		headerViewHeight = headerView.getMeasuredHeight();
		slimeViewHeight = heartview.getMeasuredHeight();
		Hidereset();
		tv_refresh.setText("往下拉，有更新");
	}

	/**
	 * 绘制子试�?	 * 
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (this.mOnScrollListener != null
				&& refreshState != RefreshState.Refreshing) {
			this.mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
		switch (scrollState) {
		// 当不滚动�?		
		case OnScrollListener.SCROLL_STATE_IDLE:
			// 判断滚动到底�?			
			if (this.getLastVisiblePosition() == (this.getCount() - 1)) {
			}
			// 判断滚动到顶�?
			if (this.getFirstVisiblePosition() == 0) {
				mIsTop = true;
			} else {
				mIsTop = false;
			}

			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 手指按下时，记录初始高度为headerViewHeight

			Log.d("SlimeRefreshListView", "ACTION_DOWN");
			oldY = event.getY();
			LastY = event.getY();
			Log.d("LastHeight", LastHeight + "");
			LastHeight = headerViewHeight;
			break;
		case MotionEvent.ACTION_MOVE:// 手指移动过程中，更新头部高度和水滴形�?			Log.d("SlimeRefreshListView", "ACTION_MOVE");
			applyHeaderLayout(event);// 传�?事件处理
			break;
		case MotionEvent.ACTION_UP:// 手指抬起，headerview回复到原来的初始位置
			Log.d("SlimeRefreshListView", "ACTION_UP");
			if (mIsTop && mIsGoingtoRefresh) {
				tv_refresh.setText("嗯~啊~要出来了。。。");
				reset();
				refreshState = RefreshState.Refreshing;// 更改刷新状�?
				heartview.refresh();
				heartview.setRotation(0);
				isInFrefresh = true;
				onRefresh();// 触发刷新事件
				mIsGoingtoRefresh = false;

			} else{ 
				if (!isInFrefresh) {
					Hidereset();	
				}
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void Hidereset() {
		// 复位后修复headerview的高度和padding
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				headerViewHeight);
		headerView.setPadding(0, 50, 0, 0);
		headerView.setLayoutParams(params);

		ishasrefresh = false;

		// 复位后修复waterView的参�?		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(45, 0);
		heartview.setLayoutParams(params2);

	}

	/**
	 * 初始化参�?	 */
	private void reset() {// headerview回复到原来的初始高度

		// 复位后修复headerview的高度和padding
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				headerViewHeight + 240);
		headerView.setPadding(0, 240, 0, 0);
		headerView.setLayoutParams(params);

		ishasrefresh = false;

		// 复位后修复waterView的参�?		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(45,
				slimeViewHeight);
		heartview.setLayoutParams(params2);

	}

	/**
	 * 移动过程�?在可拉动距离之内，更新水滴的形状 ,超过拉动的距离，�?��更新
	 * 
	 * @param ev
	 */
	@SuppressLint("NewApi")
	private void applyHeaderLayout(MotionEvent ev) {
		int historicalY = (int) ev.getY();
		if (!isInFrefresh) {
			if (historicalY != LastY) {
				if (mIsTop) {
					setSelection(0);
				}

				int h = (int) (LastHeight + historicalY - LastY);
				/* 绘制水滴效果 */
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, h);

				headerView.setLayoutParams(params);

				if (mIsGoingtoRefresh) {
					headerView.setPadding(0, h - 220, 0, 0);
				} else {
					headerView.setPadding(0, 140, 0, 0);
					int mH = (h - 220) > 0 ? (h - 220) : 0;

					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
							45, mH);
					params2.setMargins(0, 20, 0, 0);
					heartview.setLayoutParams(params2);
				}
				LastHeight = h;
				LastY = historicalY;
			}

		}

		if (refreshState == RefreshState.Initial && mIsTop) {

			if (historicalY - oldY < 220 && historicalY < 800) {// 在可拉动距离之内，更新水滴的形状

				Log.d("SlimeRefreshListView", "�?��绘制");

				// 未在刷新中，更改状�?
				ishasrefresh = false;
				mIsGoingtoRefresh = false;

			} else {// 超过拉动的距离，�?��更新
				if (!ishasrefresh && historicalY - oldY > 220// 触发刷新动画
						&& historicalY < 800) {// 避免�?��拉到头多次触发更新事�?					// 正在刷新中，更改状�?
					mIsGoingtoRefresh = true;
					ishasrefresh = true;

					// 震动
					Vibrator mVibrator01 = (Vibrator) mContext
							.getSystemService(Service.VIBRATOR_SERVICE);
					mVibrator01.vibrate(new long[] { 10, 10 }, -1);

					// 动画
					Animation rock = new RotateAnimation(0f, 180f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					rock.setFillAfter(false);
					rock.setDuration(150);
					heartview.startAnimation(rock);
					heartview.setRotation(180);
					tv_refresh.setText("爱卿，快放开朕~");
				}
			}
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		// setSelection(0);
	}

	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		this.mOnScrollListener = l;
	}

	public void onRefreshComplete() {
		tv_refresh.setText("往下拉，有更新");
		isInFrefresh = false;
		heartview.stopRefresh();
		refreshState = RefreshState.Initial;
		Hidereset();
	}

	private void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}
}
