package com.example.surfaceview;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private LuckPan mLuckyPan;
	private ImageView mStartBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLuckyPan = (LuckPan) findViewById(R.id.lucky_pan);
		mStartBtn = (ImageView) findViewById(R.id.img_btn);
		
		mStartBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!mLuckyPan.isStart()){
					mLuckyPan.luckyStart();
					mStartBtn.setImageResource(R.drawable.btn_end);
				}else{
					if(!mLuckyPan.isShouldEnd()){
						mLuckyPan.luckyEnd();
						mStartBtn.setImageResource(R.drawable.btn_start);
					}
				}
			}
		});
	}



}
