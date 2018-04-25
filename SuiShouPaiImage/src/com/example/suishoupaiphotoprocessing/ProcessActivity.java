package com.example.suishoupaiphotoprocessing;


import java.io.IOException;
import java.io.InputStream;

import com.example.suishoupaiimage.R;
import com.example.suishoupaiimage.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

//引入图像效果处理类
//引入图像框架处理类
//引入图像增强处理类
//引入图像查看处理类
//引入图像交互保存类


public class ProcessActivity extends Activity implements OnSeekBarChangeListener {
	
	//自定义变量
	private TextView textShow;               //显示图片操作
	private ImageView imageShow;         //显示图片
	private Bitmap bmp;                          //载入图片
	private Bitmap mbmp;                       //复制模版
	//布局
	private LinearLayout layoutWatch;             //查看图片
	private LinearLayout layoutIncrease;          //增强图片
	private LinearLayout layoutEffect;              //图片特效
	private LinearLayout layoutFrame;             //图片边框
	private LinearLayout layoutPerson;            //图片美白
	private RelativeLayout toolbarLayout;        //底部布局
	//图标
	private ImageView imageWatch;    
	private ImageView imageIncrease;  
	private ImageView imageEffect;  
	private ImageView imageFrame;  
	private ImageView imagePerson;  
	//弹出按钮
	private PopupWindow popupWindow1;     
	private PopupWindow popupWindow2;
	private PopupWindow popupWindow3;
	private PopupWindow popupWindow4;
	private PopupWindow popupWindow5;
	//自定义引用图像处理类
	EffectProcessImage effectProcess = null;
	FrameProcessImage frameProcess = null;
	IncreaseProcessImage increaseProcess = null;
	WatchProcessImage watchProcess = null;
	PersonProcessImage personProcess = null;
	
	//SeekBar 饱和度 色相 亮度
	private SeekBar seekBar1;
	private SeekBar seekBar2;
	private SeekBar seekBar3;
	//标记变量 Watch查看图片中
	private int flagWatch1 = 0;  //旋转次数,一次为45度 模360/45=8
	private int flagWatch2 = 0;  //水平翻转  =0第一次翻转 =1第二次翻转(原图)
	private int flagWatch3 = 0;  //垂直翻转  =0第一次翻转 =1第二次翻转(原图)
	
	//触屏标志变量 1-缩放图片 2-画图
	private int flagOnTouch = 0;    
	private Bitmap alteredBitmap;                      //备份图片
	private Canvas canvas;                                 //画布
	private Paint paint;                                       //画刷
	//触屏缩放图片
	private static final int NONE = 0;                //初始状态
	private static final int DRAG = 1;                 //拖动
	private static final int ZOOM = 2;                //缩放
	private int mode = NONE;                            //当前事件
	private float oldDist;
	private PointF startPoint = new PointF();
	private PointF middlePoint = new PointF();
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	
	//创建操作
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.activity_process);
	    
	     //获取控件
	     textShow = (TextView) findViewById(R.id.textView1);
	     imageShow = (ImageView) findViewById(R.id.imageView1);
	     //布局
         toolbarLayout = (RelativeLayout) findViewById(R.id.MyLayout_bottom);
         layoutWatch = (LinearLayout) findViewById(R.id.layout_watch);
         layoutIncrease = (LinearLayout) findViewById(R.id.layout_increase);
         layoutEffect = (LinearLayout) findViewById(R.id.layout_effect);
         layoutFrame = (LinearLayout) findViewById(R.id.layout_frame);
         layoutPerson = (LinearLayout) findViewById(R.id.layout_person);
         //图标
         imageWatch = (ImageView) findViewById(R.id.image_watch);
         imageIncrease = (ImageView) findViewById(R.id.image_increase);
         imageEffect = (ImageView) findViewById(R.id.image_effect);
         imageFrame = (ImageView) findViewById(R.id.image_frame);
         imagePerson = (ImageView) findViewById(R.id.image_person);
         
	     //载入数据
	     Intent intent = getIntent();
	     //Toast.makeText(this, "传递参数", Toast.LENGTH_SHORT).show();
	     String path = intent.getStringExtra("path"); //对应putExtra("path", path);
	     //自定义函数 显示图片
	     ShowPhotoByImageView(path);
	     //自定义函数 设置监听事件
	     SetClickTouchListener();
	     
	     /*
	      * 设置缩放图片监听 注:XML中修改android:scaleType="matrix"
	      * 此时采用点击按钮时动态设置matrix
	      */
	     imageShow.setOnTouchListener(new OnTouchListener() {

	    	//设置两个点 按下坐标(downx, downy)和抬起坐标(upx, upy)
	 		float downx = 0;
	 		float downy = 0;
	 		float upx = 0;
	 		float upy = 0;
	 		
	 		//触摸事件
	 		@Override
	 		public boolean onTouch(View v, MotionEvent event) {
	 			ImageView view = (ImageView) v;
	 			if(flagOnTouch == 1) { 
	 				//图片缩放
	 				switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN: //手指按下
						savedMatrix.set(matrix);
						startPoint.set(event.getX(), event.getY());
						mode = DRAG;
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						mode = NONE;
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						oldDist = spacing(event); //如果两点距离大于10 多点模式
						if (oldDist > 10f) {
							savedMatrix.set(matrix);
							midPoint(middlePoint, event);
							mode = ZOOM;
						}
						break;
					case MotionEvent.ACTION_MOVE:
						if (mode == DRAG) { //拖动
							matrix.set(savedMatrix);
							matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
						} else if (mode == ZOOM) { //缩放
							float newDist = spacing(event);
							if (newDist > 10f) {
								matrix.set(savedMatrix);
								float scale = newDist / oldDist;
								matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
							}
						}
						break;
					} //end switch
					view.setImageMatrix(matrix);
					return true;
	 			}
	 			else if(flagOnTouch == 2) {
	 				//图片绘制
	 				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						downx = event.getX();
						downy = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						upx = event.getX();
						upy = event.getY();
						canvas.drawLine(downx, downy, upx, upy, paint);
						imageShow.invalidate();
						downx = upx;
						downy = upy;
						break;
					case MotionEvent.ACTION_UP:
						upx = event.getX();
						upy = event.getY();
						canvas.drawLine(downx, downy, upx, upy, paint);
						imageShow.invalidate();
						break;
					case MotionEvent.ACTION_CANCEL:
						break;
					default:
						break;
					}
					return true;
	 			}
	 			else {
	 				return false;
	 			}
	 		} //end onTouch
	 		//两点距离
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}
			//两点中点
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
	     }); //end 缩放监听
	
	} 
	
	/*
	 * 函数功能 显示图片
	 * 参数 String path 图片路径,源自MainActivity选择传参
	 */
	private void ShowPhotoByImageView(String path)
	{
		if (null == path) {
			Toast.makeText(this, "载入图片失败", Toast.LENGTH_SHORT).show();
			finish();
		}
		/*
		 * 问题:
		 * 获取Uri不知道getStringExtra()没对应uri参数
		 * 使用方法Uri uri=Uri.parse(path)获取路径不能显示图片
		 * mBitmap=BitmapFactory.decodeFile(path)方法不能适应大小
		 * 解决:
		 * 但我惊奇的发现decodeFile(path,opts)函数可以实现,哈哈哈
		 */
		//获取分辨率
		DisplayMetrics dm = new DisplayMetrics();  
	    getWindowManager().getDefaultDisplay().getMetrics(dm);  
	    int width = dm.widthPixels;    //屏幕水平分辨率  
	    int height = dm.heightPixels;  //屏幕垂直分辨率  
	    try {
	    	//Load up the image's dimensions not the image itself  
	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
	        bmpFactoryOptions.inJustDecodeBounds = true; 
	        bmp = BitmapFactory.decodeFile(path,bmpFactoryOptions);
	        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);  
	        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);  
	        //压缩显示
	        if(heightRatio>1&&widthRatio>1) {  
	            if(heightRatio>widthRatio) {  
	                bmpFactoryOptions.inSampleSize = heightRatio*2;  
	            }  
	            else {  
	                bmpFactoryOptions.inSampleSize = widthRatio*2;  
	            }  
	        }  
	        //图像真正解码   
	        bmpFactoryOptions.inJustDecodeBounds = false;                 
	        bmp = BitmapFactory.decodeFile(path,bmpFactoryOptions);
	        mbmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
	        imageShow.setImageBitmap(bmp); //显示照片
	        /*
	         * [失败] 动态设置属性
	         当设置android:scaleType="matrix"后图像显示左上角
	         设置图片居中 起点=未使用屏幕/2=(屏幕分辨率-图片宽高)/2   
	         int widthCenter=imageShow.getWidth()/2-bmp.getWidth()/2;   
	         int heightCenter=imageShow.getHeight()/2-bmp.getHeight()/2;  
	         Matrix matrix = new Matrix();  
	         matrix.postTranslate(widthCenter, heightCenter);  
	         imageShow.setImageMatrix(matrix); 
	         imageShow.setImageBitmap(bmp);
	         */  
	        //加载备份图片 绘图使用
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
					.getHeight(), bmp.getConfig());
			canvas = new Canvas(alteredBitmap);  //画布
			paint = new Paint(); //画刷
			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(5);
			paint.setTextSize(30);
			paint.setTypeface(Typeface.DEFAULT_BOLD);  //无线粗体
			matrix = new Matrix();
			canvas.drawBitmap(bmp, matrix, paint);
			//imageShow.setImageBitmap(alteredBitmap);

	    } catch(Exception e) {   
            e.printStackTrace();    
        }    
	}
	
	
	/*
	 * 函数功能 设置监听事件
	 * 触摸监听事件 点击监听事件
	 */
	private void SetClickTouchListener()
	{
		/*
		 * 按钮一 监听事件 查看图片
		 */
		layoutWatch.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(ProcessActivity.this, "点击按钮1", Toast.LENGTH_SHORT).show();
				//载入PopupWindow
				if (popupWindow1 != null&&popupWindow1.isShowing()) {  
		            popupWindow1.dismiss();  
		            return;  
		        } else {  
		            initmPopupWindowView(1);   //当number=1时查看图片
		            int[] location = new int[2];  
		            v.getLocationOnScreen(location); 
		            popupWindow1.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow1.getHeight());
		        }
			}
		});
		layoutWatch.setOnTouchListener(new OnTouchListener() {
			@Override
		    public boolean onTouch(View v, MotionEvent event) {               
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {       
		            //按下背景图片             	
		        	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_bg);
		        	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
		        	//设置按钮图片
		        	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_sel));    
		        	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor)); 
		        	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor)); 
		        	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor)); 
		        	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor)); 
		        }
		        return false;       
			}       
		});  
		/*
		 * 按钮二 监听事件增强图片
		 */
		layoutIncrease.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//载入PopupWindow
				if (popupWindow2 != null&&popupWindow2.isShowing()) {  
		            popupWindow2.dismiss();  
		            return;  
		        } else {  
		            initmPopupWindowView(2);   //number=2
		            int[] location = new int[2];  
		            v.getLocationOnScreen(location); 
		            popupWindow2.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow2.getHeight());
		        }
			}
		});
        layoutIncrease.setOnTouchListener(new OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {               
                if(event.getAction() == MotionEvent.ACTION_DOWN) {       
                	//按下背景图片 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//设置按钮图片                	
                	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));    
                	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_sel)); 
                	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor)); 
                	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor)); 
                	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor)); 
                }
                return false;       
        	}       
        }); 
        /*
         * 按钮三 监听事件图片特效
         */
        layoutEffect.setOnClickListener( new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		//载入PopupWindow
        		if (popupWindow3 != null&&popupWindow3.isShowing()) {  
                    popupWindow3.dismiss();  
                    return;  
                } else {  
                    initmPopupWindowView(3);   //number=3
                    int[] location = new int[2];  
                    v.getLocationOnScreen(location); 
                    popupWindow3.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow3.getHeight());
                }
        	}
        });
        layoutEffect.setOnTouchListener(new OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {               
                if(event.getAction() == MotionEvent.ACTION_DOWN) {       
                	//按下背景图片 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//图标
                	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));    
                	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor)); 
                	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_sel)); 
                	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor)); 
                	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor)); 
                }
                return false;       
        	}       
        });
		/*
		 * 按钮四 监听事件图片相框
		 */
		layoutFrame.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//载入PopupWindow
				if (popupWindow4 != null&&popupWindow4.isShowing()) {  
		            popupWindow4.dismiss();  
		            return;  
		        } else {  
		            initmPopupWindowView(4);   //number=4
		            int[] location = new int[2];  
		            v.getLocationOnScreen(location);
		            popupWindow4.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow4.getHeight());
		        }
			}
		});
        layoutFrame.setOnTouchListener(new OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {               
                if(event.getAction() == MotionEvent.ACTION_DOWN) {       
                	//按下背景图片 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//图标                	
                	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));    
                	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor)); 
                	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor)); 
                	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_sel)); 
                	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_nor)); 
                }
                return false;       
        	}       
        });
        /*
         * 按钮五 监听事件图片美白
         */
        layoutPerson.setOnClickListener( new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		//载入PopupWindow
        		if (popupWindow5 != null&&popupWindow5.isShowing()) {  
                    popupWindow5.dismiss();  
                    return;  
                } else {  
                    initmPopupWindowView(5);   //number=5
                    int[] location = new int[2];  
                    v.getLocationOnScreen(location); 
                    popupWindow5.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow5.getHeight());
                }
        	}
        });
        layoutPerson.setOnTouchListener(new OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {               
                if(event.getAction() == MotionEvent.ACTION_DOWN) {       
                	//按下背景图片 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_bg);
                	//图标
                	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));    
                	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor)); 
                	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor)); 
                	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor)); 
                	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_sel)); 
                }
                return false;       
        	}       
        });//结束监听5个事件 
	}
	
	/*
	 * 函数功能 PopupWindow窗体动画
	 * 获取自定义布局文件  
	 */
	public void initmPopupWindowView(int number) {  
    	View customView = null;
    	//触屏标记默认为0 否则点一次"缩放"总能移动
    	flagOnTouch  = 0;
      	/*
    	 * number=1 查看
    	 */
    	if(number==1) { 
    		customView = getLayoutInflater().inflate(R.layout.popup_watch, null, false);  
    		// 创建PopupWindow实例  (250,180)分别是宽度和高度  
            popupWindow1 = new PopupWindow(customView, 450, 150); 
            // 使其聚集 要想监听菜单里控件的事件就必须要调用此方法   
            popupWindow1.setFocusable(true);  
            // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]  
            popupWindow1.setOutsideTouchable(true); 
            popupWindow1.setAnimationStyle(R.style.AnimationPreview);  
            // 自定义view添加触摸事件  
            customView.setOnTouchListener(new OnTouchListener() {  
                @Override  
                public boolean onTouch(View v, MotionEvent event) {  
                    if (popupWindow1 != null && popupWindow1.isShowing()) {  
                        popupWindow1.dismiss();  
                        popupWindow1 = null;  
                    }  
                    return false;  
                }  
            }); 
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            watchProcess = new WatchProcessImage(bmp);
            LinearLayout layoutWatch2 = (LinearLayout) customView.findViewById(R.id.layout_watch2);
            layoutWatch2.setOnClickListener(new OnClickListener() {
            	
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("图像处理--水平翻转");
		    		popupWindow1.dismiss();
		    		//调用WatchProcessImage中函数实现水平翻转
		    		mbmp = watchProcess.FlipHorizontalImage(bmp,flagWatch2);
		    		imageShow.setImageBitmap(mbmp);
		    		//标记变量 0翻转 1变回原图
		    		if(flagWatch2 == 0) {
		    			flagWatch2 = 1;
		    		} else if(flagWatch2 == 1) {
		    			flagWatch2 =0;
		    		}
            	}
            });
            LinearLayout layoutWatch3 = (LinearLayout) customView.findViewById(R.id.layout_watch3);
            layoutWatch3.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("图像处理--垂直翻转");
		    		popupWindow1.dismiss();  
		    		mbmp = watchProcess.FlipVerticalImage(bmp,flagWatch3);
		    		imageShow.setImageBitmap(mbmp);
		    		//标记变量 0翻转 1变回原图
		    		if(flagWatch3 == 0) {
		    			flagWatch3 = 1;
		    		} else if(flagWatch3 == 1) {
		    			flagWatch3 =0;
		    		}
            	}
            });
            LinearLayout layoutWatch1 = (LinearLayout) customView.findViewById(R.id.layout_watch1);
            layoutWatch1.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("图像处理--旋转图片");
		    		popupWindow1.dismiss();	    
		    		//旋转一次表示增加45度 模8表示360度=0度
		    		flagWatch1 = (flagWatch1+1) % 8;
		    		//设置背景颜色黑色
		    		//imageShow.setBackgroundColor(Color.parseColor("#000000"));  
		    		mbmp = watchProcess.TurnImage(bmp, flagWatch1);
		    		imageShow.setImageBitmap(mbmp); 
            	}
            });
            LinearLayout layoutWatch4 = (LinearLayout) customView.findViewById(R.id.layout_watch4);
            layoutWatch4.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("图像处理--移动缩放");
		    		popupWindow1.dismiss();	    
		    		flagOnTouch = 1; //标志变量
		    		//动态设置android:scaleType="matrix"
		    		imageShow.setScaleType(ImageView.ScaleType.MATRIX);
		    		imageShow.setImageBitmap(bmp);
            	}
            });
            LinearLayout layoutWatch5 = (LinearLayout) customView.findViewById(R.id.layout_watch5);
            layoutWatch5.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("图像处理--绘制图片");
		    		popupWindow1.dismiss();	    
		    		flagOnTouch = 2; //标志变量
		    		//动态设置android:scaleType="matrix"
		    		imageShow.setScaleType(ImageView.ScaleType.MATRIX);
		    		//画图 图片移动至(0,0) 否则绘图线与手指存在误差
					matrix = new Matrix();
					matrix.postTranslate(0, 0);
					imageShow.setImageMatrix(matrix);
					canvas.drawBitmap(bmp, matrix, paint);  
					imageShow.setImageBitmap(alteredBitmap); //备份图片
            	}
            });
    	}
    	/*
    	 * number=2 增强
    	 */
    	 if(number==2) { 
         	customView = getLayoutInflater().inflate(R.layout.popup_increase, null, false);  
         	//设置子窗体PopupWindow高度500 饱和度 色相 亮度
         	popupWindow2 = new PopupWindow(customView, 600, 500);
         	// 使其聚集 要想监听菜单里控件的事件就必须要调用此方法   
             popupWindow2.setFocusable(true);  
             // 设置允许在外点击消失   
             popupWindow2.setOutsideTouchable(true); 
             popupWindow2.setAnimationStyle(R.style.AnimationPreview);  
             // 自定义view添加触摸事件  
             customView.setOnTouchListener(new OnTouchListener() {  
                 @Override  
                 public boolean onTouch(View v, MotionEvent event) {  
                     if (popupWindow2 != null && popupWindow2.isShowing()) {  
                         popupWindow2.dismiss();  
                         popupWindow2 = null;  
                     }  
                     return false;  
                 }  
             });  
             //SeekBar
             seekBar1 = (SeekBar) customView.findViewById(R.id.seekBarSaturation);  //饱和度
             seekBar2 = (SeekBar) customView.findViewById(R.id.seekBarHue);            //色相
             seekBar3 = (SeekBar) customView.findViewById(R.id.seekBarLum);            //亮度
             /*
    	      * 设置Seekbar变化监听事件 
    	      * 注意:此时修改活动接口 
    	      * ProcessActivity extends Activity implements OnSeekBarChangeListener
    	      */
    	     seekBar1.setOnSeekBarChangeListener(this);
    	     seekBar2.setOnSeekBarChangeListener(this);
    	     seekBar3.setOnSeekBarChangeListener(this);
    	     //自定义引用类
    	     increaseProcess = new IncreaseProcessImage(bmp); 
         }
    	/*
    	 * number=3 效果
    	 */
		if(number==3) { 
			customView = getLayoutInflater().inflate(R.layout.popup_effect, null, false);  
			popupWindow3 = new PopupWindow(customView, 450, 150);
		    popupWindow3.setFocusable(true); 
		    popupWindow3.setOutsideTouchable(true); 
		    popupWindow3.setAnimationStyle(R.style.AnimationPreview);  
		    // 自定义view添加触摸事件  
		    customView.setOnTouchListener(new OnTouchListener() {  
		        @Override  
		        public boolean onTouch(View v, MotionEvent event) {  
		            if (popupWindow3 != null && popupWindow3.isShowing()) {  
		                popupWindow3.dismiss();  
		                popupWindow3 = null;  
		            }  
		            return false;  
		        }  
		    });  
		    //判断点击子菜单不同按钮实现不同功能
		    //自定义引用类
	        effectProcess = new EffectProcessImage(bmp); 
		    LinearLayout layoutEffect1 = (LinearLayout) customView.findViewById(R.id.layout_effect_hj);
		    layoutEffect1.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("图像处理--怀旧效果");
		    		popupWindow3.dismiss();  
		    		//调用EffectProcessImage.java中函数
		    		mbmp = effectProcess.OldRemeberImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect2 = (LinearLayout) customView.findViewById(R.id.layout_effect_fd);
		    layoutEffect2.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("图像处理--浮雕效果");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.ReliefImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect3 = (LinearLayout) customView.findViewById(R.id.layout_effect_gz);
		    layoutEffect3.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("图像处理--光照效果");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SunshineImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect4 = (LinearLayout) customView.findViewById(R.id.layout_effect_sm);
		    layoutEffect4.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("图像处理--素描效果");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SuMiaoImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect5 = (LinearLayout) customView.findViewById(R.id.layout_effect_rh);
		    layoutEffect5.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("图像处理--锐化效果");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SharpenImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    
		}
		/*
		 * number=4 边框
		 */
        if(number==4) {
        	customView = getLayoutInflater().inflate(R.layout.popup_frame, null, false);  
        	popupWindow4 = new PopupWindow(customView, 450, 150);
            popupWindow4.setFocusable(true);  
            popupWindow4.setAnimationStyle(R.style.AnimationPreview);  
            // 自定义view添加触摸事件  
            customView.setOnTouchListener(new OnTouchListener() {  
                @Override  
                public boolean onTouch(View v, MotionEvent event) {  
                    if (popupWindow4 != null && popupWindow4.isShowing()) {  
                        popupWindow4.dismiss();  
                        popupWindow4 = null;  
                    }  
                    return false;  
                }  
            });
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            frameProcess = new FrameProcessImage(bmp);
            LinearLayout layoutFrame3 = (LinearLayout) customView.findViewById(R.id.layout_frame3);
            layoutFrame3.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("图像处理--框架模式三");
            		popupWindow4.dismiss();
            		//获取相框 自定义函数getImageFromAssets 获取assets中资源
            		Bitmap frameBitmap = getImageFromAssets("image_frame_big_3.png");  
            		//显示图像并增加相框
            		mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame2 = (LinearLayout) customView.findViewById(R.id.layout_frame2);
            layoutFrame2.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("图像处理--框架模式二");
            		popupWindow4.dismiss();
            		Bitmap frameBitmap = getImageFromAssets("image_frame_big_2.png");  
            		mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame1 = (LinearLayout) customView.findViewById(R.id.layout_frame1);
            layoutFrame1.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("图像处理--框架模式一");
            		popupWindow4.dismiss();
            		Bitmap frameBitmap = getImageFromAssets("image_frame_big_1.png");  
            		mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame4 = (LinearLayout) customView.findViewById(R.id.layout_frame4);
            layoutFrame4.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("图像处理--圆角矩形");
            		popupWindow4.dismiss();
            		mbmp = frameProcess.RoundedCornerBitmap(bmp);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame5 = (LinearLayout) customView.findViewById(R.id.layout_frame5);
            layoutFrame5.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("图像处理--圆形相框");
            		popupWindow4.dismiss();
            		mbmp = frameProcess.RoundedBitmap(bmp);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
        }
        /*
         * number=5 美白 -> 交互
         */
        if(number==5) {
        	customView = getLayoutInflater().inflate(R.layout.popup_person, null, false);  
        	popupWindow5 = new PopupWindow(customView, 300, 150);
            popupWindow5.setFocusable(true);  
            popupWindow5.setAnimationStyle(R.style.AnimationPreview);  
            // 自定义view添加触摸事件  
            customView.setOnTouchListener(new OnTouchListener() {  
                @Override  
                public boolean onTouch(View v, MotionEvent event) {  
                    if (popupWindow5 != null && popupWindow5.isShowing()) {  
                        popupWindow5.dismiss();  
                        popupWindow5 = null;  
                    }  
                    return false;  
                }  
            });  
            //判断点击子菜单不同按钮实现不同功能
            //自定义引用类
            personProcess = new PersonProcessImage(bmp);
            LinearLayout layoutPerson1 = (LinearLayout) customView.findViewById(R.id.layout_person1);
            layoutPerson1.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("保存图像至SD卡");
            		popupWindow5.dismiss();
            		try {     		
            			/*
            			 * 注意：由于手机重启才能显示图片 所以定义广播刷新相册 其中saveBitmapToSD保存图片
            			 */
            			if(mbmp == null) { //防止出现mbmp空
            				mbmp = bmp;
            			}
        				Uri uri = personProcess.saveBitmapToSD(mbmp);
        				Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        				intent.setData(uri);
        				sendBroadcast(intent);
        				Toast.makeText(ProcessActivity.this, "图像保存成功", Toast.LENGTH_SHORT).show();
            		} catch(Exception e) {
            			e.printStackTrace();
            			Toast.makeText(ProcessActivity.this, "图像保存失败", Toast.LENGTH_SHORT).show();
            		}
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutPerson2 = (LinearLayout) customView.findViewById(R.id.layout_person2);
            layoutPerson2.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("取消处理操作--恢复原图");
            		popupWindow5.dismiss();
            		mbmp = bmp;
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutPerson3 = (LinearLayout) customView.findViewById(R.id.layout_person3);
            layoutPerson3.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("上传图片至发布界面");
            		popupWindow5.dismiss();
            		try {
	            		if(mbmp == null) { //防止出现mbmp空
	        				mbmp = bmp;
	        			}
	            		//图像上传 先保存 后传递图片路径
	            		Uri uri = personProcess.loadBitmap(mbmp);
	    				Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    				intent.setData(uri);
	    				sendBroadcast(intent);
	    				//上传图片*
	    				Intent intentPut = new Intent(ProcessActivity.this, MainActivity.class);
	    				String pathImage = null;
	    				intentPut.putExtra("pathProcess", personProcess.pathPicture );
	    				/*
	    				 * 返回活动使用setResult 使用startActivity总是显示一张图片并RunTime
	    				 * startActivity(intentPut);
	    				 * 在onActivityResult中获取数据
	    				 */
	    				setResult(RESULT_OK, intentPut);
	            		//返回上一界面
	    				Toast.makeText(ProcessActivity.this, "图片上传成功" , Toast.LENGTH_SHORT).show();
	            		ProcessActivity.this.finish(); 
	            	} catch(Exception e) {
            			e.printStackTrace();
            			Toast.makeText(ProcessActivity.this, "图像上传失败", Toast.LENGTH_SHORT).show();
            		}
            	}
            });
           
        } //end if
    }
	
	//获取assets中资源并转换为Bitmap  
	public Bitmap getImageFromAssets(String fileName)  
	{  
	    //Android中使用assets目录存放资源,它代表应用无法直接访问的原生资源  
	    Bitmap imageAssets = null;  
	    AssetManager am = getResources().getAssets();  
	    try {  
	        InputStream is = am.open(fileName);  
	        imageAssets = BitmapFactory.decodeStream(is);  
	        is.close();  
	    } catch(IOException e) {  
	        e.printStackTrace();  
	    }  
	    return imageAssets;  
	}  
	
	/*
	 * 设置SeekBar监听事件 
	 * 但是点击PopupWindow2才弹出该界面 会有影响吗?
	 */
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
	{
		int flag = 0;
		switch(seekBar.getId()) {
		case R.id.seekBarSaturation: //饱和度
			textShow.setText("图像增强--饱和度"+progress);
			flag = 0;
			increaseProcess.setSaturation(progress);
			break;
		case R.id.seekBarHue: //色相
			textShow.setText("图像增强--色相"+progress);
			flag = 1;
			increaseProcess.SetHue(progress);
			break;
		case R.id.seekBarLum: //亮度
			textShow.setText("图像增强--亮度"+progress);
			flag = 2;
			increaseProcess.SetLum(progress);
			break;
		}
		mbmp = increaseProcess.IncreaseProcessing(bmp,flag);
		imageShow.setImageBitmap(mbmp);
	}
	//SeekBar 开始拖动 否则ProcessActivity报错
	public void onStartTrackingTouch(SeekBar seekBar)
	{
		
	}
	//SeekBar 停止拖动
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		
	}
	
	/*
	 * End ProcessActivity
	 */
	
}
