package com.example.surfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class SurfaceViewTempl extends SurfaceView implements Callback, Runnable {
	
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	
	private Thread t;
	private Boolean isRuning;
	
	

	public SurfaceViewTempl(Context context) {
		super(context,null);
	}

	public SurfaceViewTempl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		isRuning = true;
		t = new Thread(this);
		t.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		isRuning = false;
	}

	@Override
	public void run() {
		while(isRuning){
			draw();
		}
	}

	private void draw() {
		
		try {
			mCanvas = mHolder.lockCanvas();
			if(mCanvas!=null){
				//draw someting
			}
		} catch (Exception e) {
		}finally{
			if(mCanvas!=null){
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		
	}

}




