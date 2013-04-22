package com.happyshareproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happyshareproject.sdk.HappyShare;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button mBtnDialog;
	
	private HappyShare mShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		mShare = new HappyShare(this);
	}
	
	private void initView() {
		mBtnDialog = (Button) findViewById(R.id.btn_dialog);
		mBtnDialog.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog:
			mShare.show();
			break;

		default:
			break;
		}
	}

}
