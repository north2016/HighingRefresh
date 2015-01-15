package com.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.high.R;

public class ListAdapter extends BaseAdapter {

	Context mContext;

	public ListAdapter(Context context) {
		mContext = context;

	}

	@Override
	public int getCount() {
		return 20;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
		}
		TextView text = (TextView) convertView.findViewById(R.id.title);
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		img.setBackgroundResource(R.drawable.e);
		text.setText("≤‚ ‘ ˝æ›");

		return convertView;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
