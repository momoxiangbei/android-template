package com.example.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


public class LuckPan extends SurfaceView implements Callback, Runnable {
	
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	
	private Thread t;
	private Boolean isRuning;
	
	private String[] mStrs = new String[]{"img1","img2","img3","img4","img5","img6"};
	private int[] mImgs = new int[]{R.drawable.img,R.drawable.img,R.drawable.img,R.drawable.img,R.drawable.img,R.drawable.img};
	private int[] mColors = new int[]{0XFFFFC300,0XFFF17E01,0XFFFFC300,0XFFF17E01,0XFFFFC300,0XFFF17E01};
	private int mItemCount =6;
	private Bitmap[] mImgsBitmap;
	private RectF mRange = new RectF();
	private int mRadius; 	
	private Paint mArcPaint;
	private Paint mTextPaint;
	private double mSpeed;
	private volatile int mStartAngle;
	private boolean isShouleEnd;
	private int mCenter;
	/**
	 * 所有的Padding以PadddingLeft为准
	 */
	private int mPadding;
	private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
	private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
	
	public LuckPan(Context context) {
		super(context,null);
	}

	public LuckPan(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
		mPadding = getPaddingLeft();
		mRadius = width - mPadding*2;
		mCenter = width/2;
		setMeasuredDimension(width,width);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		//初始化画笔
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);
		mTextPaint = new Paint();
		mTextPaint.setColor(0xffffffff);
		mTextPaint.setTextSize(mTextSize);
		
		//初始化盘快范围
		mRange = new RectF(mPadding,mPadding,mRadius+mPadding,mRadius+mPadding);
		
		//初始化图片
		mImgsBitmap = new Bitmap[mItemCount];
		
		for(int i=0; i<mItemCount; i++){
			mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);
		}
		
		
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
		//50毫秒绘制一次
		while(isRuning){
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			if(end-start<50){
				try {
					Thread.sleep(50-(end-start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		
		try {
			mCanvas = mHolder.lockCanvas();
			if(mCanvas!=null){
				//绘制背景
				drawBg();
				
				//起始角度
				float tmpAngle = mStartAngle;
				float sweepAngle = 360/mItemCount;
				for(int i=0;i<mItemCount;i++){
					
					//绘制盘快
					mArcPaint.setColor(mColors[i]);					
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
					//绘制文本
					drawText(tmpAngle,sweepAngle,mStrs[i]);
					//绘制图片
					drawIcon(tmpAngle,mImgsBitmap[i]);
					
					tmpAngle += sweepAngle;
				}
				mStartAngle += mSpeed;
				
				//如果点击了停止按钮
				if(isShouleEnd){
					
					if(mSpeed>0){
						mSpeed--;
					}else{
						mSpeed = 0;
						isRuning = false;
					}
					
				}

				
			}
		} catch (Exception e) {
		}finally{
			if(mCanvas!=null){
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
		
	}

	//点击启动旋转
	public void luckyStart(){
		mSpeed = 50;
		isShouleEnd = false;
	}
	public void luckyEnd(){
		isShouleEnd = true;
	}
	public boolean isStart(){
		return mSpeed!=0;
	}
	public boolean isShouldEnd(){
		return isShouleEnd;
	}
	
	//绘制图片
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		int imgWidth = mRadius/8;   //图片宽度设置为直径的1/8
		float angle = (float) ((tmpAngle + 360/mItemCount/2)*Math.PI/180);
		int x = (int) (mCenter + mRadius/2/2*Math.cos(angle));
		int y = (int) (mCenter + mRadius/2/2*Math.sin(angle));
		Rect rect = new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
		mCanvas.drawBitmap(bitmap, null, rect,null);
	}

	//绘制文本
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		//利用水平偏移量让文字居中
		float textWidth = mTextPaint.measureText(string);
		int hOffset = (int) (mRadius*Math.PI/mItemCount/2-textWidth/2);
		int vOffset = mRadius/2/6;  //垂直偏移量
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);  //设置垂直和水平偏移量
	}

	//绘制背景
	private void drawBg() {
		mCanvas.drawColor(0xFFFFFFFF);
		mCanvas.drawBitmap(mBgBitmap, null,new Rect(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2), null);
	}

}




