package cn.hxc.imgrecognition;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class mySurfaceView extends FrameLayout {
	private SurfaceView surfaceView;
	private rectView imageView;
	private int maskWidth;
	private int maskHeight;
	private int screenWidth;
	private int screenHeight;
	public mySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceView = new SurfaceView(context);
		imageView = new rectView(context);
		this.addView(surfaceView,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addView(imageView,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		
	}
	

	

	
}
