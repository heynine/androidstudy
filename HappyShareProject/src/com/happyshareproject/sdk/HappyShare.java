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

	// ��ʾ���ݵ���ʽ
	private int mCurrentGetAppType = 0x0000;
	// ��ʾȫ������
	public static final int TYPE_ALL_CONTENT = 0x0000;
	// ɸѡ��
	public static final int TYPE_EXCEPT_SOME = 0x0001;
	// �Զ���
	public static final int TYPE_USER_DEFINED = 0x0002;

	// ��ʾ��ʽ
	private int mShowStype = 0x0000;
	// Dialog��ʾ��ʽ
	public static final int TYPE_DIALOG = 0x0000;
	// PopWindow��ʾ��ʽ
	public static final int TYPE_POPWIN = 0x0001;
	// Activity��ʾ��ʽ
	public static final int TYPE_ACTITY = 0x0002;

	// Ҫ�Ƴ���apps������
	private String[] mExceptApp;
	// �Զ�������app�б�
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
		// ��һЩ��ʼ������
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
				i.putExtra(Intent.EXTRA_SUBJECT, "����");
				i.putExtra(Intent.EXTRA_TEXT, mShareContent);
				mContext.startActivity(i);

				// dimiss���Ի���
				mDialog.dismiss();
			}
		});

		Builder builder = new Builder(mContext);
		builder.setTitle(R.string.dialog_title);
		builder.setCancelable(true);
		builder.setView(view);
		mDialog = builder.create();

	}

	/***************** ���ÿ�ʼ *********************/
	
	public void setContent(String content) {
		this.mShareContent = content;
	}

	/**
	 * ���÷�����ʽ
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.mType = type;
	}

	/**
	 * ���û�ȡApp��ʽ
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
	 * ����Ҫ���������
	 * 
	 * @param args
	 */
	public void setExceptApp(String[] apps) {
		this.mExceptApp = apps;
	}

	/**
	 * �Զ�������app
	 * 
	 * @param apps
	 */
	public void setUserDefinedApp(String[] apps) {
		this.mUserDefinedApp = apps;

	}

	/***************** ���ý��� *********************/

	/***************** ��ѯӦ�ÿ�ʼ *********************/

	private void getAllShareApps() {
		long startTime = new Date().getTime();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(mType);
		PackageManager packageManager = mContext.getPackageManager();
		mDatas = mContext.getPackageManager().queryIntentActivities(intent, 0);
		Collections.sort(mDatas, new ResolveInfo.DisplayNameComparator(
				packageManager)); // ����
		mDatasShow = mDatas;
		long endTime = new Date().getTime();
		System.out.println("��ȡ��ʱ��" + (endTime - startTime));
	}

	/**
	 * ɸѡ
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
					// �Ƴ�
					mDatasShow.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * ��ȡ�û�����Ҫ�����app�б�
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
					// �Ƴ�
					mDatasShow.add(info);
					break;
				}
			}
		}
	}

	/**
	 * �������Ҫ�����app�б�
	 */
	private void getApps() {
		switch (mCurrentGetAppType) {
		case TYPE_ALL_CONTENT:
			// ���еģ���ʼ����ʱ���Ѿ���ѯ��
			break;
		case TYPE_EXCEPT_SOME:
			// ɸѡ
			getAppAfterExcept();
		case TYPE_USER_DEFINED:
			// �Զ���
			getAppFromUserDefined();
			break;
		default:
			break;
		}
	}

	/***************** ��ѯӦ�ý��� *********************/

	/******************* ��ʾ��ʼ *********************/
	/**
	 * ���Է�����
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

	/******************* ��ʾ���� *********************/

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
