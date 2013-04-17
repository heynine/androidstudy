package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		String str = stringFromJNI();
		tv.setText(str);
		setContentView(tv);
	}

	static {
		System.loadLibrary("demo");
		Log.i("load_so", "加载完成");
	}

	public native String say();

	public native String stringFromJNI();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
