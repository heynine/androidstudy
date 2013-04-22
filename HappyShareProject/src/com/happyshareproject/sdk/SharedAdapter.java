package com.happyshareproject.sdk;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.happyshareproject.R;

public class SharedAdapter extends ArrayAdapter<ResolveInfo> {
	
	private Context mContext;
	private PackageManager mPackageManager;
	
	public SharedAdapter(Context context, PackageManager pm, List<ResolveInfo> datas) {
		super(context, R.layout.share_layout, datas);
		this.mContext = context;
		this.mPackageManager = pm;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoler holder = new ViewHoler();
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.share_layout, null);
		}
		holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon_happyshare);
		holder.tvName = (TextView) convertView.findViewById(R.id.tv_name_happyshare);
		holder.ivIcon.setImageDrawable(getItem(position).loadIcon(mPackageManager));
		holder.tvName.setText(getItem(position).loadLabel(mPackageManager));
		convertView.setTag(holder);
		return convertView;
			
	}
	
	static class ViewHoler {
		ImageView ivIcon;
		TextView tvName;
	}


}
