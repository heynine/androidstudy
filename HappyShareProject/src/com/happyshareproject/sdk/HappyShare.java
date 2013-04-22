package com.happyshareproject.sdk;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.happyshareproject.R;

public class HappyShare {

	// 显示内容的形式
	private int mCurrentGetAppType = 0x0000;
	// 显示全部内容
	public static final int TYPE_ALL_CONTENT = 0x0000;
	// 筛选掉
	public static final int TYPE_EXCEPT_SOME = 0x0001;
	// 自定义
	public static final int TYPE_USER_DEFINED = 0x0002;

	// 显示方式
	private int mShowStype = 0x0000;
	// Dialog显示方式
	public static final int TYPE_DIALOG = 0x0000;
	// PopWindow显示方式
	public static final int TYPE_POPWIN = 0x0001;
	// Activity显示方式
	public static final int TYPE_ACTITY = 0x0002;

	// 要移除的apps，名字
	private String[] mExceptApp;
	// 自定义分享的app列表
	private String[] mUserDefinedApp;
	// Intent type
	private String mType = "text/plain";
	
	private String mShareContent = "";

	private List<ResolveInfo> mDatas;

	private List<ResolveInfo> mDatasShow;

	private Context mContext;

	private ListView mListView;
	private SharedAdapter mAdapter;
	private Dialog mDialog;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			createDialog();
		};
	};

	public HappyShare(Context context) {
		// 做一些初始化工作
		this.mContext = context;
		getAllShareApps();
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_view,
				null);
		mListView = (ListView) view.findViewById(R.id.lv);
		mAdapter = new SharedAdapter(mContext, mContext.getPackageManager(),
				mDatasShow);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ResolveInfo launchable = mAdapter.getItem(position);
				ActivityInfo activity = launchable.activityInfo;
				ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				i.setComponent(name);
				i.putExtra(Intent.EXTRA_SUBJECT, "分享");
				i.putExtra(Intent.EXTRA_TEXT, mShareContent);
				mContext.startActivity(i);

				// dimiss掉对话框
				mDialog.dismiss();
			}
		});

		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.dialog_title);
		builder.setCancelable(true);
		builder.setView(view);
		mDialog = builder.create();

	}

	/***************** 配置开始 *********************/
	
	public void setContent(String content) {
		this.mShareContent = content;
	}

	/**
	 * 设置分享形式
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.mType = type;
	}

	/**
	 * 设置获取App方式
	 * 
	 * @param type
	 */
	public void setgetAppType(int type) {
		this.mCurrentGetAppType = type;
	}

	public void setShowType(int type) {
		this.mShowStype = type;
	}

	/**
	 * 设置要配出的内容
	 * 
	 * @param args
	 */
	public void setExceptApp(String[] apps) {
		this.mExceptApp = apps;
	}

	/**
	 * 自定义分享的app
	 * 
	 * @param apps
	 */
	public void setUserDefinedApp(String[] apps) {
		this.mUserDefinedApp = apps;

	}

	/***************** 配置结束 *********************/

	/***************** 查询应用开始 *********************/

	private void getAllShareApps() {
		long startTime = new Date().getTime();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(mType);
		PackageManager packageManager = mContext.getPackageManager();
		mDatas = mContext.getPackageManager().queryIntentActivities(intent, 0);
		Collections.sort(mDatas, new ResolveInfo.DisplayNameComparator(
				packageManager)); // 排序
		mDatasShow = mDatas;
		long endTime = new Date().getTime();
		System.out.println("获取用时：" + (endTime - startTime));
	}

	/**
	 * 筛选
	 */
	private void getAppAfterExcept() {
		if (mExceptApp == null || mExceptApp.length == 0) {
			return;
		}
		mDatasShow.clear();
		mDatasShow = mDatas;
		for (String strName : mExceptApp) {
			for (int i = 0; i < mDatas.size(); i++) {
				ResolveInfo info = mDatas.get(i);
				ActivityInfo activityInfo = info.activityInfo;
				if (activityInfo.packageName.contains(strName)
						|| activityInfo.name.contains(strName)) {
					// 移除
					mDatasShow.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * 获取用户定义要分享的app列表
	 */
	private void getAppFromUserDefined() {
		if (mUserDefinedApp == null || mUserDefinedApp.length == 0) {
			return;
		}
		mDatasShow.clear();
		for (String strName : mUserDefinedApp) {
			for (int i = 0; i < mDatas.size(); i++) {
				ResolveInfo info = mDatas.get(i);
				ActivityInfo activityInfo = info.activityInfo;
				if (activityInfo.packageName.contains(strName)
						|| activityInfo.name.contains(strName)) {
					// 移除
					mDatasShow.add(info);
					break;
				}
			}
		}
	}

	/**
	 * 获得最终要分享的app列表
	 */
	private void getApps() {
		switch (mCurrentGetAppType) {
		case TYPE_ALL_CONTENT:
			// 所有的：初始化的时候已经查询了
			break;
		case TYPE_EXCEPT_SOME:
			// 筛选
			getAppAfterExcept();
		case TYPE_USER_DEFINED:
			// 自定义
			getAppFromUserDefined();
			break;
		default:
			break;
		}
	}

	/***************** 查询应用结束 *********************/

	/******************* 显示开始 *********************/
	/**
	 * 可以分享了
	 */
	public void show() {
		switch (mShowStype) {
		case TYPE_DIALOG:
			showAsDialog();
			break;
		case TYPE_POPWIN:

			break;

		case TYPE_ACTITY:

			break;

		default:
			break;
		}
	}

	private void showAsDialog() {
		new Thread(new GetAppRunnable()).start();
	}

	private void createDialog() {
		mDialog.show();
	}

	private void showAsPopWindow() {
	}

	private void showAsActivity() {
	}

	/******************* 显示结束 *********************/

	class GetAppRunnable implements Runnable {

		@Override
		public void run() {
			// getAllShareApps();

			switch (mCurrentGetAppType) {
			case TYPE_ALL_CONTENT:

				break;
			case TYPE_EXCEPT_SOME:
				getAppAfterExcept();
				break;
			case TYPE_USER_DEFINED:
				getAppFromUserDefined();
				break;

			default:
				break;
			}
			mHandler.sendEmptyMessage(0);
		}
	}

}
