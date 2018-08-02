package cn.hxc.imgrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import cn.hxc.imgrecognition.SensorControler.CameraFocusListener;
import cn.hxc.imgrecognitionSRI_OCR.R;

import android.R.string;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.sax.StartElementListener;
import android.support.v4.view.ActionProvider.VisibilityListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class takePhoto extends Activity implements AutoFocusCallback,
		OnTouchListener{
	public static long Starttime;
	public static long Endtime;
	public static final String TAG = "takePhoto";
	private Button btn_takephoto;
	private Button btn_flash;
	private Button btn_flash_on;
	private SurfaceView surfaceView;
	private View rectView;//中间的绿色框
	private View MidLine;//中间的蓝色线
	private View VerticalLine; //中间的垂直线
	private Camera camera;
	private boolean preview;
	private Activity activity;
	// private View nextLayout;
	// private View fisrtLayout;
	private Parameters parameters;
	private int zoomValue;
	private int oldZoomValue;
	// private SensorControler mSensorControler;
	private boolean isFlashon = false;

	private verticalSeekBar seekBar;
	private ImageView seekBar_imageview;
	private int displayOrientation;

	private static int sWidth;
	private static int sHight;
	static boolean isWriteRecogize;

	// 屏幕的宽高
	private float mScreenWidth;
	private float mScreenHeight;
	private int width;
	private int height;

	// ��������
	private float old_x;
	private float old_y;
	private float new_x;
	private float new_y;
	private float old_x1;
	private float old_y1;
	private float new_x1;
	private float new_y1;
	// private boolean isInRect;// �ж���ָ����ʱ����ھ��ο��λ��
	// private boolean isInTop;
	// private boolean isInBottom;
	private boolean isLeft;
	private boolean isRight;
	private boolean isVerticalMove = false;
	private boolean isDown;
	private Rect rect;
	private Canvas canvas;
	private Paint paint;
	private int margain;
	private tiltImageView tilt;

	// ˫�ֲ���
	private int topBord;
	private int leftBord;
	// private int leftBord;
	private int offsetTop;
	private int offsetLeft;

	private AbsoluteLayout.LayoutParams lp;
	// private LinearLayout.LayoutParams lpLeft;
	// private LinearLayout.LayoutParams lpRight;

	private SharedPreferences preferences;
	static final float scaleLeft = (float) 1.0 / 8;
	static final float scaleTop = (float) ((float) 5.0 / 10 - 0.05);
	// private static int zoom;

	private final float minDistance = 10;
	private static final float maxLeftScale = (float) (2.0 / 5);
	private static final int minLeft = 3;
	private static final float maxBottomSacle = (float) (1.0 / 2);
	private static final float minTopSacle = (float) (1.0 / 2);//0.25
	private static final int minRectHeight = 20;

	private Rect recognizeRect;
		private Drawable picDrawable;

		private View view_focus = null;
		private PreviewFrameLayout frameLayout = null;

	//定义是否退出程序的标记
	private boolean isExit=false;
	//定义接受用户发送信息的handler
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//标记用户不退出状态
			isExit=false;
		}
	};


	@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			Window window = getWindow();
			requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ������
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			setContentView(R.layout.takephoto);

			WindowManager wm = (WindowManager) this
					.getSystemService(this.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels; //屏幕宽度
		mScreenHeight = outMetrics.heightPixels; //屏幕高度

		height = (int) mScreenHeight;
		width = (int) mScreenWidth;
		rectView = findViewById(R.id.rectView);
		MidLine = findViewById(R.id.MidLine);
		VerticalLine = findViewById(R.id.VerticalLine);
		btn_takephoto = (Button) findViewById(R.id.btn_takephoto);
		btn_flash = (Button) findViewById(R.id.flash_btn);
		btn_flash_on = (Button) findViewById(R.id.flash_btn_on);
		// tilt=(tiltImageView) findViewById(R.id.tilt);
		frameLayout = (PreviewFrameLayout) findViewById(R.id.frame_layout);
		// frameLayout.setOnTouchListener(l);
		view_focus = findViewById(R.id.view_focus);

		imageProcess.noequl(scaleLeft + "/", 0);
		imageProcess.noequl(scaleTop + "/", 0);

		preferences = getSharedPreferences("set", MODE_PRIVATE);
		if (!preferences.contains("positionLeft")) {
			savePosition("positionLeft", scaleLeft);
			if (mScreenHeight < 600) {
				savePosition("positionTop", scaleTop - (float) 0.01);
			}
			if (mScreenHeight > 1600) {
				savePosition("positionTop", scaleTop + (float) 0.01);
			}

		}
		if (!preferences.contains("resultCount")) {
			savePosition("resultCount", 1);
		}

		SharedPreferences setting = getSharedPreferences("20180423", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		if (user_first) {//第一次
			setting.edit().putBoolean("FIRST", false).commit();
			File file = new File(
					Environment.getExternalStorageDirectory()
							+ File.separator + "WR_LPAIS");
			if (file.exists()) {
				deleteFile(file);
			}
		} else {
			//Toast.makeText(MainActivity.this, "不是第一次", Toast.LENGTH_LONG).show();
		}
		// imageProcess.noequl("positionLeft=", getBottomPosition());
		// mSensorControler = new SensorControler(this);
		initTakephoto();
	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			//Constants.Logdada("文件不存在！"+"\n");
		}
	}


	public void jumpLoc(){
		Intent intent = new Intent(this, queryLocInfor.class);
		startActivity(intent);
	}

	public void jumpDb(){
		Intent intent = new Intent(this, queryDBInfor.class);
		startActivity(intent);
	}


	void savePosition(String string, float scale) {
		Editor editor = preferences.edit();
		editor.putFloat(string, scale);
		editor.commit();
	}

	float getLeftPosition() {
		return preferences.getFloat("positionLeft", scaleLeft);
	}

	float getBottomPosition() {
		return preferences.getFloat("positionTop", scaleTop);
	}

	int px(float pix) {

		return (int) ((pix + 0.5) * 1.5);
	}

	public void initTakephoto() {
		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE); // 空心 Paint.Style.FILL 实心
		paint.setPathEffect(new DashPathEffect(new float[] { 3, 2 }, 0));
		canvas = new Canvas();
		Path path = new Path();
		path.moveTo(10, 10);//起点
		path.lineTo(10, 400);//终点
		canvas.drawPath(path, paint);

		leftBord = (int) (getLeftPosition() * mScreenWidth);//0.125*屏幕宽度
		topBord = (int) (getBottomPosition() * mScreenHeight);//0.19*屏幕高度

		imageProcess.noequl("getBottomPosition=", getBottomPosition());

		lp = new AbsoluteLayout.LayoutParams(width - 2 * leftBord,
				(int) ((height * minTopSacle - topBord) * 2), leftBord, topBord);//minTopSacle=0.25
		rectView.setLayoutParams(lp);  //中间绿色的框
		AbsoluteLayout.LayoutParams lpMid = new AbsoluteLayout.LayoutParams(
				(width - 2 * leftBord) / 2, 4, leftBord
						+ (width - 2 * leftBord) / 4,
				(int) (height * minTopSacle));
		MidLine.setLayoutParams(lpMid); //中间水平的蓝色的线

		AbsoluteLayout.LayoutParams lpVertical = new AbsoluteLayout.LayoutParams(
				4, 20, width / 2, (int) (height * minTopSacle - 20 / 2));
		VerticalLine.setLayoutParams(lpVertical);

		recognizeRect = new Rect(leftBord, topBord, width - leftBord, (int) (2
				* minTopSacle * height - topBord));

		isWriteRecogize = false;
		activity = this;
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);//surfaceView可以直接从内存或者DMA等硬件接口取得图像数据,是个非常重要的绘图容器

		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.setFocusable(true);
		surfaceView.getHolder().setKeepScreenOn(true);
		surfaceView.getHolder().addCallback(new SufaceListener());
		
		surfaceView.setOnTouchListener(this);
		rectView.setOnTouchListener(this);
	}

	private final class SufaceListener implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@SuppressWarnings("deprecation")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open();
				initCamera();
				try {
					camera.setPreviewDisplay(holder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.i("camera", "preview failed!");
					e.printStackTrace();
				}
				camera.startPreview();
				
				preview = true;
				 onMyFoucs();

			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				if (preview)
					camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
	}

	public void onMyFoucs() {
		if (camera != null&&preview==true) {
			camera.autoFocus(this);
		}
		
	}

	@Override
	public void onAutoFocus(boolean success, final Camera camera) {
		// TODO Auto-generated method stub
		if (success) {
			if (parameters.getSupportedFocusModes().contains(
					Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
			try {
				camera.setParameters(parameters);
			}catch (Exception e){

			}

			if(Build.VERSION.SDK_INT>=14) {
				int tempWidth = view_focus.getWidth();
				int tempHeight = view_focus.getHeight();
				view_focus.setX(mScreenWidth / 2 - (tempWidth / 2));
				view_focus.setY(mScreenHeight / 2 - (tempHeight / 2));

			}
			view_focus.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_focus_focused));

		}
		else {
			view_focus.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.ic_focus_focusing));
		}
		setFocusViewNull();
	}

	 private void setFocusViewNull() {
		    new Handler().postDelayed(new Runnable() {

		      @SuppressWarnings("deprecation")
		      @Override
		      public void run() {
		        view_focus.setBackgroundDrawable(null);

		      }
		    }, 1 * 1000);
		  }
		 private void setFocusedView() {
			    new Handler().postDelayed(new Runnable() {

			      @SuppressWarnings("deprecation")
			      @Override
			      public void run() {
			    	  view_focus.setBackgroundDrawable(getResources().getDrawable(
			  				R.drawable.ic_focus_focused));

			      }
			    }, 1 * 1000);
			  }

	public boolean isFlashlightOn() {
		try {
			Camera.Parameters parameters = camera.getParameters();
			String flashMode = parameters.getFlashMode();
			if (flashMode
					.equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// ��������ĳ�ʼ������
	private void initCamera() {
		try {

			btn_takephoto.setEnabled(true);
			parameters = camera.getParameters();// ����ͷ�Ĳ���
			// List<int[]> range=parameters.getSupportedPreviewFpsRange();
			// parameters.setPreviewFrameRate(20);// ÿ��20֡
			// List<Integer> formerate= parameters.getSupportedPreviewFormats();
			// parameters.setPreviewFormat(formerate.get(formerate.size()/2));

			if (camera.getParameters().isZoomSupported()) {  //缩放
				int zoom = camera.getParameters().getMaxZoom();// 得到的缩放所允许的最大值为快照
				parameters.setZoom(zoom / 5);
				imageProcess.noequl("zoom---------", zoom);
			}

			//闪光灯
			List<String> flashModes = parameters.getSupportedFlashModes();
			// Check if camera flash exists
			if (flashModes == null) {
				// Use the screen as a flashlight (next best thing)
				return;
			}
			String flashMode = parameters.getFlashMode();
			if (isFlashon == true) {   //闪光灯开启
				if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
					// Turn on the flash
					if (flashModes.contains(Parameters.FLASH_MODE_TORCH))
						parameters
								.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				}
				// Toast.makeText(this, "opened", 1).show();
			} else {  //闪光灯关闭
				if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
					// Turn on the flash
					if (flashModes.contains(Parameters.FLASH_MODE_OFF))
						parameters
								.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				}
			}

			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // ��ȡ��ǰ��Ļ����������
			Display display = wm.getDefaultDisplay(); // ��ȡ��Ļ��Ϣ��������
			// parameters.setPreviewSize(display.getWidth(),
			// display.getHeight());
			// // ����

			List<Size> SupportedPictureSizes= parameters
					.getSupportedPictureSizes();// ��ȡ֧��Ԥ����Ƭ�ĳߴ�
//			List<Size> SupportedPictureSizes = parameters
//					.getSupportedPreviewSizes();// ��ȡ֧��Ԥ����Ƭ�ĳߴ�
			// Size previewSize = SupportedPreviewSizes.get(0);// ��Listȡ��Size
			// sWidth = previewSize.width;
			// sHight = previewSize.height;
//			Size previewSize = getOptimalPreviewSize(SupportedPictureSizes,
//					display.getWidth(), display.getHeight());

//			Size previewSize = CameraUtil.getInstance().getPictureSize(SupportedPictureSizes,400);
//			sWidth = previewSize.width;
//			sHight = previewSize.height;

			Size picSize = CameraUtil.getInstance().getPictureSize(parameters
					.getSupportedPictureSizes(),800);
//
//			imageProcess.noequl("**********sWidth=", sWidth);
//			imageProcess.noequl("*********sHight=", sHight);
//			imageProcess.noequl("", mScreenHeight);
//			// Toast.makeText(takePhoto.this, ""+mScreenHeight, 1).show();
			//parameters.setPreviewSize(sWidth, sHight);
		//	parameters.setPictureSize(sWidth, sHight);
			parameters.setPictureSize(picSize.width, picSize.height);

			// ��������Ԥ��
			setCameraDisplayOrientation(activity,
					Camera.CameraInfo.CAMERA_FACING_BACK, camera);//后置摄像头
			
			focosTouchRect();
			camera.cancelAutoFocus();
			camera.setParameters(parameters);

		} catch (Exception e) {
		}

	}

	public void focosTouchRect() {//MotionEvent event
		int[] location = new int[2];

		//获取在整个屏幕内的绝对坐标，这个值是要从屏幕顶端算起，也就是包括了通知栏的高度。
		//location [0]--->x坐标,location [1]--->y坐标
		frameLayout.getLocationOnScreen(location);
		//�۽�����ʾ�ڴ����ĵط�
//		Rect focusRect = calculateTapArea(view_focus.getWidth(),
//				view_focus.getHeight(), 1f, event.getRawX(), event.getRawY(),
//				location[0], location[0] + frameLayout.getWidth(), location[1],
//				location[1] + frameLayout.getHeight());
//		Rect meteringRect = calculateTapArea(view_focus.getWidth(),
//				view_focus.getHeight(), 1.5f, event.getRawX(), event.getRawY(),
//				location[0], location[0] + frameLayout.getWidth(), location[1],
//				location[1] + frameLayout.getHeight());
		//�۽�����ʾ�ڹ̶��㣨1/4*mscreenheight,1/2*mscreenwidth��
		Rect focusRect = calculateTapArea(view_focus.getWidth(),
				view_focus.getHeight(), 1f, mScreenWidth/2, mScreenHeight/4,
				location[0], location[0] + frameLayout.getWidth(), location[1],
				location[1] + frameLayout.getHeight());
		Rect meteringRect = calculateTapArea(view_focus.getWidth(),
				view_focus.getHeight(), 1.5f, mScreenWidth/2, mScreenHeight/4,
				location[0], location[0] + frameLayout.getWidth(), location[1],
				location[1] + frameLayout.getHeight());
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); //设置对焦模式

		// System.out.println("CustomCameraView getMaxNumFocusAreas = " +
		// parameters.getMaxNumFocusAreas());

		if(Build.VERSION.SDK_INT>=14) {
			if (parameters.getMaxNumFocusAreas() > 0) {
				List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
				focusAreas.add(new Camera.Area(focusRect, 1000));

				parameters.setFocusAreas(focusAreas);
			}

			// System.out.println("CustomCameraView getMaxNumMeteringAreas = " +
			// parameters.getMaxNumMeteringAreas());
			if (parameters.getMaxNumMeteringAreas() > 0) {
				List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
				meteringAreas.add(new Camera.Area(meteringRect, 1000));

				parameters.setMeteringAreas(meteringAreas);
			}
		}
		try {
			camera.setParameters(parameters);
		} catch (Exception e) {
		}
		//onMyFoucs();
		onMyFoucs();
	}

	/**
	 * ���㽹�㼰�������
	 * 
	 * @param focusWidth
	 * @param focusHeight
	 * @param areaMultiple
	 * @param x
	 * @param y
	 * @param previewleft
	 * @param previewRight
	 * @param previewTop
	 * @param previewBottom
	 * @return Rect(left,top,right,bottom) : left��top��right��bottom������ʾ��������Ϊԭ�������
	 */
	public Rect calculateTapArea(int focusWidth, int focusHeight,
			float areaMultiple, float x, float y, int previewleft,
			int previewRight, int previewTop, int previewBottom) {
		int areaWidth = (int) (focusWidth * areaMultiple);
		int areaHeight = (int) (focusHeight * areaMultiple);
		int centerX = (previewleft + previewRight) / 2;
		int centerY = (previewTop + previewBottom) / 2;
		double unitx = ((double) previewRight - (double) previewleft) / 2000;
		double unity = ((double) previewBottom - (double) previewTop) / 2000;
		int left = clamp((int) (((x - areaWidth / 2) - centerX) / unitx),
				-1000, 1000);
		int top = clamp((int) (((y - areaHeight / 2) - centerY) / unity),
				-1000, 1000);
		int right = clamp((int) (left + areaWidth / unitx), -1000, 1000);
		int bottom = clamp((int) (top + areaHeight / unity), -1000, 1000);

		return new Rect(left, top, right, bottom);
	}

	public int clamp(int x, int min, int max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}


	public void flash(View v) {
		try {
			if (isFlashon == false) {
				isFlashon = true;
				btn_flash.setVisibility(View.GONE);
				btn_flash_on.setVisibility(View.VISIBLE);

			} else {
				isFlashon = false;
				btn_flash.setVisibility(View.VISIBLE);
				btn_flash_on.setVisibility(View.GONE);
			}
			initCamera();
		} catch (Exception e) {
			Toast.makeText(this, "���صƲ�����", Toast.LENGTH_LONG).show();
		}
	}

	public void takepicture(View v) {
		Starttime=System.currentTimeMillis();
		// nextLayout.setVisibility(ViewGroup.VISIBLE);
		// fisrtLayout.setVisibility(ViewGroup.GONE);
		btn_takephoto.setEnabled(false);
		if (camera != null) {// ����ǰ�жϣ��������������Ϊnull
			try {
				//Camera.startPreview();
				//Camera.autoFocus(mAutoFocusCallback);
				camera.takePicture(null, null, mJpegPictureCallback);

			} catch (Exception e) {
				System.out.println("takepicture failed...");
			}
		}
		Endtime=System.currentTimeMillis();
	}

	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:
		case KeyEvent.KEYCODE_5:
			takepicture(new View(this));
			break;
		case KeyEvent.KEYCODE_BACK:
			back(new View(this));
			break;
		}

		return true;

	}*/

	PictureCallback mJpegPictureCallback = new PictureCallback() {

		// Skipped 47 frames! The application may be doing too much work on its
		// main thread.
		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			// TODO Auto-generated method stub
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,options);

			BitmapFactory.Options opts=getOption(options);
			Bitmap bitmap1= BitmapFactory
					.decodeByteArray(data, 0, data.length,opts);
			preview = true;

			Size size1=camera.getParameters().getPreviewSize();
			Size size2=camera.getParameters().getPictureSize();

			if (bitmap1 != null) {

				FileOutputStream fos,fos1;
				Matrix matrix = new Matrix();
				matrix.postRotate(displayOrientation);
//				Bitmap bitmap = Bitmap.createBitmap(bitmap1, (int) (bitmap1.getWidth()*0.1f), 0,
//						(int) (bitmap1.getWidth()*0.8f),bitmap1.getHeight(), matrix, false);
				/*
				从原始位图剪切图像，这是一种高级的方式。可以用Matrix(矩阵)来实现旋转等高级方式截图
				参数说明：
　　			Bitmap source：要从中截图的原始位图
　　			int x:起始x坐标
　　			int y：起始y坐标
				int width：要截的图的宽度
				int height：要截的图的宽度
				Bitmap.Config  config：一个枚举类型的配置，可以定义截到的新位图的质量
				返回值：返回一个剪切好的Bitmap
				 */
				//Bitmap bitmap = Bitmap.createBitmap(bitmap1, 0, (int)(bitmap1.getHeight()*0.12f),
						 //bitmap1.getWidth(),(int)(bitmap1.getHeight()*0.76f), matrix, false);
				Bitmap bitmap = Bitmap.createBitmap(bitmap1, 0, (int)(bitmap1.getHeight()*0.12f),
						bitmap1.getWidth(),(int)(bitmap1.getHeight()*0.76f), matrix, false);

				float scaleX = bitmap.getWidth() / mScreenWidth;
				float scaleY = bitmap.getHeight() / mScreenHeight;

				int left = (int) (scaleX * leftBord);
				int top = (int)(topBord* scaleY);
				int width = (int) ( (mScreenWidth-2*leftBord) * scaleX);
				int height = (int) ((mScreenHeight/2-topBord)*2* scaleY);
				Bitmap rotaBitmap = Bitmap.createBitmap(bitmap, left, top, width, height);

				try
				{

					File file1 = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "WR_LPAIS");

					if (!file1.exists())
					{
						file1.mkdirs();
					}

					//xyj
					fos1 = new FileOutputStream(file1 + File.separator
							+ "greyTemp.jpg");
					rotaBitmap.compress(CompressFormat.JPEG, 100, fos1);
					fos1.flush();
					fos1.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				//Bitmap.recycle();会释放Bitmap所关联的像素资源，并且释放是单向的，一旦释放不可再用
				bitmap.recycle();
				bitmap=null;
				bitmap1.recycle();
				bitmap1=null;

				Intent intent = new Intent(takePhoto.this,
						processActivity.class);
				startActivity(intent);
			} else {
				// �������
				camera.stopPreview();
				camera.startPreview();
				camera.cancelAutoFocus();
				btn_takephoto.setEnabled(true);
			}
		}
	};

public BitmapFactory.Options getOption(BitmapFactory.Options opts){

		opts.inPreferredConfig = Bitmap.Config.RGB_565;

		opts.inPurgeable = true;

	Size size1=camera.getParameters().getPreviewSize();

	opts.inSampleSize =(int) (opts.outHeight/mScreenHeight);
		opts.inInputShareable = true;
		opts.inJustDecodeBounds=false;
		return  opts;
}
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;

		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;// compensate the mirror
		} else {// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		displayOrientation = result;
		camera.setDisplayOrientation(result);
	}


	private static final int MODE_INIT = 0;

	private static final int MODE_POINTER = 1;
	private int mode = MODE_INIT;

	private float startDis;
	private float endDis;

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = MODE_INIT;
			isDown = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			old_x = event.getX(0);
			old_y = event.getY(0);
			old_x1 = event.getX(1);
			old_y1 = event.getY(1);

			mode = MODE_POINTER;
			startDis = distance(event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == MODE_POINTER) {
				if (event.getPointerCount() < 2) {
					return true;
				}
				onTouchMove(event);

			}
			break;
		case MotionEvent.ACTION_UP:
			if (mode == MODE_POINTER) {
				topBord -= offsetTop / 2;
				imageProcess.noequl("topBord b��=", topBord);
				leftBord -= offsetLeft / 2;

			} else {
				view_focus.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_focus_focusing));
				focosTouchRect();
			}
			savePosition("positionLeft", (float) leftBord / mScreenWidth);
			savePosition("positionTop", (float) topBord / mScreenHeight);

			break;
		}

		return true;
	}

	private void onTouchMove(MotionEvent event) {

		endDis = distance(event);
		float distance_PRENTER_y = (float) Math.sqrt((old_y1 - old_y)
				* (old_y1 - old_y));
		float distance_PRENTER_x = (float) Math.sqrt((old_x1 - old_x)
				* (old_x1 - old_x));
		if (isDown == true) {
			if (distance_PRENTER_y > distance_PRENTER_x) {
				isVerticalMove = true;
			} else {
				isVerticalMove = false;
			}
			isDown = false;
		}
		if (isVerticalMove) {
			offsetLeft = 0;
			offsetTop = (int) ((endDis - startDis) / 2);
			if (offsetTop / 2 > topBord - height / 8) {
				offsetTop = (topBord - height / 8) * 2;
			}
			if (-offsetTop / 2 > -topBord + height * minTopSacle - 15) {
				offsetTop = (int) (topBord - height * minTopSacle + 15) * 2;
			}

		} else {
			offsetTop = 0;
			offsetLeft = (int) ((endDis - startDis) / 2);
			if (leftBord - offsetLeft / 2 < 5) {
				offsetLeft = (leftBord - 5) * 2;
			}
			if (leftBord - offsetLeft / 2 > 2 * width / 5) {
				offsetLeft = (leftBord - 2 * width / 5) * 2;
			}

		}

		// (int) mScreenHeight / 4 + (int) mScreenHeight / 12
		lp = new AbsoluteLayout.LayoutParams(width - 2 * leftBord + offsetLeft,
				(int) ((height * minTopSacle - topBord) * 2 + offsetTop),
				leftBord - offsetLeft / 2, topBord - offsetTop / 2);
		// lp.setMargins(leftBord - offsetLeft / 2, topBord - offsetTop / 2, 0,
		// 0);
		rectView.setLayoutParams(lp);
	}

	/** ����������ָ��ľ��� */
	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** ʹ�ù��ɶ���������֮��ľ��� */
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	//手机界面返回按钮执行的操作
	public void back(View v){
		this.finish();
		onBackPressed();
		destoryView();
	}

	//监听手机的物理按键点击事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//判断用户是否点击的是返回键
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//如果isExit标记为false，提示用户再次按键
			if(!isExit){
				isExit=true;
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				//如果用户没有在2秒内再次按返回键的话，就发送消息标记用户为不退出状态
				mHandler.sendEmptyMessageDelayed(0, 3000);
			}
			//如果isExit标记为true，退出程序
			else{
				//退出程序
				finish();
				BackPressed();
			}
		}
		return false;
	}

	public void BackPressed(){
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			System.exit(0);
		} else {// android2.1
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
		}
	}

	void destoryView(){
		if(rect!=null){
			rect=null;
		}
		if(rectView!=null){
			rectView=null;
		}
	}
	public void shujuzhengli(View v) {
		Intent intent = new Intent(this, chooseImage.class);
		startActivity(intent);
	}
	public void botmSet(View v){
		Intent intent = new Intent(this, set.class);
		startActivity(intent);
	}
	public void botmContrast(View v){
		Intent intent = new Intent(this, takePhoto.class);
		startActivity(intent);
	}
	public void botmBlacklist(View v){
		Intent intent = new Intent(this, blackList.class);
		startActivity(intent);
	}
	public void botmQueryLoc(View v){
		Intent intent = new Intent(this, queryLocInfor.class);
		startActivity(intent);
	}
	public void botmQueryDB(View v){
		Intent intent = new Intent(this, queryDBInfor.class);
		startActivity(intent);
	}
}
