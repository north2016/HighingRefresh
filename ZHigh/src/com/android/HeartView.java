package com.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.high.R;

@SuppressLint("HandlerLeak")
public class HeartView extends ImageView {
	private Canvas mCanvas;

	private int background = Color.parseColor("#00ccff");
	private int innerColor = Color.parseColor("#ffffff");
	private Paint mPaint;

	int index = 0;
	boolean isright = true;
	private Handler mhandler = new Handler();

	private boolean isRefresh = false;

	public HeartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0);
		mPaint.setStyle(Style.STROKE); // 外框灰色
		mPaint.setColor(background);

	}

	public HeartView(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(0);
		mPaint.setStyle(Style.STROKE); // 外框灰色
		mPaint.setColor(background);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		this.mCanvas = canvas;
		mCanvas.drawColor(background);
		initTabs();
	}

	private void initTabs() {
		int Cell = mCanvas.getWidth() / 7;

		int Height = mCanvas.getHeight();
		int HCell = Cell * 3 / 4;
		Paint blackPaint = new Paint();
		blackPaint.setAntiAlias(true);
		blackPaint.setColor(innerColor); // 填充的颜�?		
		blackPaint.setStyle(Style.FILL);
		blackPaint.setStrokeWidth(2);

		int Padding = 2;
		Paint top = new Paint();

		top.setAntiAlias(true);
		top.setColor(innerColor); // 填充的颜�?		
		top.setStyle(Style.FILL);
		top.setStrokeWidth(2);

		for (int i = 0; i < 7; i++) {
			if (isRefresh) {
				if (i == index) {
					top.setAlpha(255);

				} else {
					if (isright && (index - i) > 0 && (index - i) < 5) {
						top.setAlpha(255 - Math.abs(index - i) * 40);
					} else if (!isright && (i - index) > 0 && (i - index) < 5) {
						top.setAlpha(255 - Math.abs(index - i) * 40);
					} else {
						top.setAlpha(0);
					}
				}
			} else {
				top.setAlpha(255);
			}

			if ((i + 1) % 2 != 0) {
				mCanvas.drawRect(i * Cell + Padding, HCell, i * Cell + Cell,
						2 * HCell, top);

				if (i == 2 || i == 4) {// 中间两个

					mCanvas.drawRect(i * Cell + Padding, 3 * HCell, i * Cell
							+ Cell, Height - 3 * HCell, blackPaint);
				} else {// 边上两个

					mCanvas.drawRect(i * Cell + Padding, 3 * HCell, i * Cell
							+ Cell, Height - 5 * HCell, blackPaint);
				}
			} else if (i % 3 == 0) {//�?��间那�?				
				mCanvas.drawRect(i * Cell + Padding, 2 * HCell,
						i * Cell + Cell, 3 * HCell, top);

				mCanvas.drawRect(i * Cell + Padding, 4 * HCell,
						i * Cell + Cell, Height - 2 * HCell, blackPaint);
			} else {//其他
				mCanvas.drawRect(i * Cell + Padding, 0, i * Cell + Cell, HCell,
						top);

				mCanvas.drawRect(i * Cell + Padding, 2 * HCell,
						i * Cell + Cell, Height - 4 * HCell, blackPaint);
			}

		}

		if (index == 8||index ==-1) {
			Animation rock = AnimationUtils.loadAnimation(getContext(),
					R.anim.rock1);

			this.startAnimation(rock);
		}

	}

	public void stopRefresh() {
		mhandler.removeCallbacks(mRunnable);
		isRefresh = false;
		invalidate();
	}

	public void refresh() {
		if (!isRefresh) {
			mhandler.postDelayed(mRunnable, 100);
		}
	}

	private Runnable mRunnable = new Runnable() {
		int newindex;

		@Override
		public void run() {
			isRefresh = true;
			newindex = index;
			if (isright) {
				if (newindex < 8) {
					isright = true;
					newindex++;
				} else {
					isright = false;
				}
			} else {
				if (index >-1) {
					isright = false;
					newindex--;
				} else {
					isright = true;
				}

			}

			invalidate();
			index = newindex;
			mhandler.postDelayed(this, 100);
		}
	};

}