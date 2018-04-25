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

//����ͼ��Ч��������
//����ͼ���ܴ�����
//����ͼ����ǿ������
//����ͼ��鿴������
//����ͼ�񽻻�������


public class ProcessActivity extends Activity implements OnSeekBarChangeListener {
	
	//�Զ������
	private TextView textShow;               //��ʾͼƬ����
	private ImageView imageShow;         //��ʾͼƬ
	private Bitmap bmp;                          //����ͼƬ
	private Bitmap mbmp;                       //����ģ��
	//����
	private LinearLayout layoutWatch;             //�鿴ͼƬ
	private LinearLayout layoutIncrease;          //��ǿͼƬ
	private LinearLayout layoutEffect;              //ͼƬ��Ч
	private LinearLayout layoutFrame;             //ͼƬ�߿�
	private LinearLayout layoutPerson;            //ͼƬ����
	private RelativeLayout toolbarLayout;        //�ײ�����
	//ͼ��
	private ImageView imageWatch;    
	private ImageView imageIncrease;  
	private ImageView imageEffect;  
	private ImageView imageFrame;  
	private ImageView imagePerson;  
	//������ť
	private PopupWindow popupWindow1;     
	private PopupWindow popupWindow2;
	private PopupWindow popupWindow3;
	private PopupWindow popupWindow4;
	private PopupWindow popupWindow5;
	//�Զ�������ͼ������
	EffectProcessImage effectProcess = null;
	FrameProcessImage frameProcess = null;
	IncreaseProcessImage increaseProcess = null;
	WatchProcessImage watchProcess = null;
	PersonProcessImage personProcess = null;
	
	//SeekBar ���Ͷ� ɫ�� ����
	private SeekBar seekBar1;
	private SeekBar seekBar2;
	private SeekBar seekBar3;
	//��Ǳ��� Watch�鿴ͼƬ��
	private int flagWatch1 = 0;  //��ת����,һ��Ϊ45�� ģ360/45=8
	private int flagWatch2 = 0;  //ˮƽ��ת  =0��һ�η�ת =1�ڶ��η�ת(ԭͼ)
	private int flagWatch3 = 0;  //��ֱ��ת  =0��һ�η�ת =1�ڶ��η�ת(ԭͼ)
	
	//������־���� 1-����ͼƬ 2-��ͼ
	private int flagOnTouch = 0;    
	private Bitmap alteredBitmap;                      //����ͼƬ
	private Canvas canvas;                                 //����
	private Paint paint;                                       //��ˢ
	//��������ͼƬ
	private static final int NONE = 0;                //��ʼ״̬
	private static final int DRAG = 1;                 //�϶�
	private static final int ZOOM = 2;                //����
	private int mode = NONE;                            //��ǰ�¼�
	private float oldDist;
	private PointF startPoint = new PointF();
	private PointF middlePoint = new PointF();
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	
	//��������
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	     setContentView(R.layout.activity_process);
	    
	     //��ȡ�ؼ�
	     textShow = (TextView) findViewById(R.id.textView1);
	     imageShow = (ImageView) findViewById(R.id.imageView1);
	     //����
         toolbarLayout = (RelativeLayout) findViewById(R.id.MyLayout_bottom);
         layoutWatch = (LinearLayout) findViewById(R.id.layout_watch);
         layoutIncrease = (LinearLayout) findViewById(R.id.layout_increase);
         layoutEffect = (LinearLayout) findViewById(R.id.layout_effect);
         layoutFrame = (LinearLayout) findViewById(R.id.layout_frame);
         layoutPerson = (LinearLayout) findViewById(R.id.layout_person);
         //ͼ��
         imageWatch = (ImageView) findViewById(R.id.image_watch);
         imageIncrease = (ImageView) findViewById(R.id.image_increase);
         imageEffect = (ImageView) findViewById(R.id.image_effect);
         imageFrame = (ImageView) findViewById(R.id.image_frame);
         imagePerson = (ImageView) findViewById(R.id.image_person);
         
	     //��������
	     Intent intent = getIntent();
	     //Toast.makeText(this, "���ݲ���", Toast.LENGTH_SHORT).show();
	     String path = intent.getStringExtra("path"); //��ӦputExtra("path", path);
	     //�Զ��庯�� ��ʾͼƬ
	     ShowPhotoByImageView(path);
	     //�Զ��庯�� ���ü����¼�
	     SetClickTouchListener();
	     
	     /*
	      * ��������ͼƬ���� ע:XML���޸�android:scaleType="matrix"
	      * ��ʱ���õ����ťʱ��̬����matrix
	      */
	     imageShow.setOnTouchListener(new OnTouchListener() {

	    	//���������� ��������(downx, downy)��̧������(upx, upy)
	 		float downx = 0;
	 		float downy = 0;
	 		float upx = 0;
	 		float upy = 0;
	 		
	 		//�����¼�
	 		@Override
	 		public boolean onTouch(View v, MotionEvent event) {
	 			ImageView view = (ImageView) v;
	 			if(flagOnTouch == 1) { 
	 				//ͼƬ����
	 				switch (event.getAction() & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN: //��ָ����
						savedMatrix.set(matrix);
						startPoint.set(event.getX(), event.getY());
						mode = DRAG;
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						mode = NONE;
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						oldDist = spacing(event); //�������������10 ���ģʽ
						if (oldDist > 10f) {
							savedMatrix.set(matrix);
							midPoint(middlePoint, event);
							mode = ZOOM;
						}
						break;
					case MotionEvent.ACTION_MOVE:
						if (mode == DRAG) { //�϶�
							matrix.set(savedMatrix);
							matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
						} else if (mode == ZOOM) { //����
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
	 				//ͼƬ����
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
	 		//�������
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}
			//�����е�
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
	     }); //end ���ż���
	
	} 
	
	/*
	 * �������� ��ʾͼƬ
	 * ���� String path ͼƬ·��,Դ��MainActivityѡ�񴫲�
	 */
	private void ShowPhotoByImageView(String path)
	{
		if (null == path) {
			Toast.makeText(this, "����ͼƬʧ��", Toast.LENGTH_SHORT).show();
			finish();
		}
		/*
		 * ����:
		 * ��ȡUri��֪��getStringExtra()û��Ӧuri����
		 * ʹ�÷���Uri uri=Uri.parse(path)��ȡ·��������ʾͼƬ
		 * mBitmap=BitmapFactory.decodeFile(path)����������Ӧ��С
		 * ���:
		 * ���Ҿ���ķ���decodeFile(path,opts)��������ʵ��,������
		 */
		//��ȡ�ֱ���
		DisplayMetrics dm = new DisplayMetrics();  
	    getWindowManager().getDefaultDisplay().getMetrics(dm);  
	    int width = dm.widthPixels;    //��Ļˮƽ�ֱ���  
	    int height = dm.heightPixels;  //��Ļ��ֱ�ֱ���  
	    try {
	    	//Load up the image's dimensions not the image itself  
	        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();  
	        bmpFactoryOptions.inJustDecodeBounds = true; 
	        bmp = BitmapFactory.decodeFile(path,bmpFactoryOptions);
	        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);  
	        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);  
	        //ѹ����ʾ
	        if(heightRatio>1&&widthRatio>1) {  
	            if(heightRatio>widthRatio) {  
	                bmpFactoryOptions.inSampleSize = heightRatio*2;  
	            }  
	            else {  
	                bmpFactoryOptions.inSampleSize = widthRatio*2;  
	            }  
	        }  
	        //ͼ����������   
	        bmpFactoryOptions.inJustDecodeBounds = false;                 
	        bmp = BitmapFactory.decodeFile(path,bmpFactoryOptions);
	        mbmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
	        imageShow.setImageBitmap(bmp); //��ʾ��Ƭ
	        /*
	         * [ʧ��] ��̬��������
	         ������android:scaleType="matrix"��ͼ����ʾ���Ͻ�
	         ����ͼƬ���� ���=δʹ����Ļ/2=(��Ļ�ֱ���-ͼƬ���)/2   
	         int widthCenter=imageShow.getWidth()/2-bmp.getWidth()/2;   
	         int heightCenter=imageShow.getHeight()/2-bmp.getHeight()/2;  
	         Matrix matrix = new Matrix();  
	         matrix.postTranslate(widthCenter, heightCenter);  
	         imageShow.setImageMatrix(matrix); 
	         imageShow.setImageBitmap(bmp);
	         */  
	        //���ر���ͼƬ ��ͼʹ��
            alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp
					.getHeight(), bmp.getConfig());
			canvas = new Canvas(alteredBitmap);  //����
			paint = new Paint(); //��ˢ
			paint.setColor(Color.GREEN);
			paint.setStrokeWidth(5);
			paint.setTextSize(30);
			paint.setTypeface(Typeface.DEFAULT_BOLD);  //���ߴ���
			matrix = new Matrix();
			canvas.drawBitmap(bmp, matrix, paint);
			//imageShow.setImageBitmap(alteredBitmap);

	    } catch(Exception e) {   
            e.printStackTrace();    
        }    
	}
	
	
	/*
	 * �������� ���ü����¼�
	 * ���������¼� ��������¼�
	 */
	private void SetClickTouchListener()
	{
		/*
		 * ��ťһ �����¼� �鿴ͼƬ
		 */
		layoutWatch.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(ProcessActivity.this, "�����ť1", Toast.LENGTH_SHORT).show();
				//����PopupWindow
				if (popupWindow1 != null&&popupWindow1.isShowing()) {  
		            popupWindow1.dismiss();  
		            return;  
		        } else {  
		            initmPopupWindowView(1);   //��number=1ʱ�鿴ͼƬ
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
		            //���±���ͼƬ             	
		        	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_bg);
		        	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
		        	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
		        	//���ð�ťͼƬ
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
		 * ��ť�� �����¼���ǿͼƬ
		 */
		layoutIncrease.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//����PopupWindow
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
                	//���±���ͼƬ 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//���ð�ťͼƬ                	
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
         * ��ť�� �����¼�ͼƬ��Ч
         */
        layoutEffect.setOnClickListener( new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		//����PopupWindow
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
                	//���±���ͼƬ 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//ͼ��
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
		 * ��ť�� �����¼�ͼƬ���
		 */
		layoutFrame.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				//����PopupWindow
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
                	//���±���ͼƬ 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_bg);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_no);
                	//ͼ��                	
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
         * ��ť�� �����¼�ͼƬ����
         */
        layoutPerson.setOnClickListener( new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		//����PopupWindow
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
                	//���±���ͼƬ 
                	layoutWatch.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutIncrease.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutEffect.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutFrame.setBackgroundResource(R.drawable.image_home_layout_no);
                	layoutPerson.setBackgroundResource(R.drawable.image_home_layout_bg);
                	//ͼ��
                	imageWatch.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_watch_nor));    
                	imageIncrease.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_increase_nor)); 
                	imageEffect.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_effect_nor)); 
                	imageFrame.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_frame_nor)); 
                	imagePerson.setImageDrawable(getResources().getDrawable(R.drawable.image_icon_person_sel)); 
                }
                return false;       
        	}       
        });//��������5���¼� 
	}
	
	/*
	 * �������� PopupWindow���嶯��
	 * ��ȡ�Զ��岼���ļ�  
	 */
	public void initmPopupWindowView(int number) {  
    	View customView = null;
    	//�������Ĭ��Ϊ0 �����һ��"����"�����ƶ�
    	flagOnTouch  = 0;
      	/*
    	 * number=1 �鿴
    	 */
    	if(number==1) { 
    		customView = getLayoutInflater().inflate(R.layout.popup_watch, null, false);  
    		// ����PopupWindowʵ��  (250,180)�ֱ��ǿ�Ⱥ͸߶�  
            popupWindow1 = new PopupWindow(customView, 450, 150); 
            // ʹ��ۼ� Ҫ������˵���ؼ����¼��ͱ���Ҫ���ô˷���   
            popupWindow1.setFocusable(true);  
            // ���ö���Ч�� [R.style.AnimationFade ���Լ����ȶ���õ�]  
            popupWindow1.setOutsideTouchable(true); 
            popupWindow1.setAnimationStyle(R.style.AnimationPreview);  
            // �Զ���view��Ӵ����¼�  
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
            //�жϵ���Ӳ˵���ͬ��ťʵ�ֲ�ͬ����
            //�Զ���������
            watchProcess = new WatchProcessImage(bmp);
            LinearLayout layoutWatch2 = (LinearLayout) customView.findViewById(R.id.layout_watch2);
            layoutWatch2.setOnClickListener(new OnClickListener() {
            	
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("ͼ����--ˮƽ��ת");
		    		popupWindow1.dismiss();
		    		//����WatchProcessImage�к���ʵ��ˮƽ��ת
		    		mbmp = watchProcess.FlipHorizontalImage(bmp,flagWatch2);
		    		imageShow.setImageBitmap(mbmp);
		    		//��Ǳ��� 0��ת 1���ԭͼ
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
            		textShow.setText("ͼ����--��ֱ��ת");
		    		popupWindow1.dismiss();  
		    		mbmp = watchProcess.FlipVerticalImage(bmp,flagWatch3);
		    		imageShow.setImageBitmap(mbmp);
		    		//��Ǳ��� 0��ת 1���ԭͼ
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
            		textShow.setText("ͼ����--��תͼƬ");
		    		popupWindow1.dismiss();	    
		    		//��תһ�α�ʾ����45�� ģ8��ʾ360��=0��
		    		flagWatch1 = (flagWatch1+1) % 8;
		    		//���ñ�����ɫ��ɫ
		    		//imageShow.setBackgroundColor(Color.parseColor("#000000"));  
		    		mbmp = watchProcess.TurnImage(bmp, flagWatch1);
		    		imageShow.setImageBitmap(mbmp); 
            	}
            });
            LinearLayout layoutWatch4 = (LinearLayout) customView.findViewById(R.id.layout_watch4);
            layoutWatch4.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("ͼ����--�ƶ�����");
		    		popupWindow1.dismiss();	    
		    		flagOnTouch = 1; //��־����
		    		//��̬����android:scaleType="matrix"
		    		imageShow.setScaleType(ImageView.ScaleType.MATRIX);
		    		imageShow.setImageBitmap(bmp);
            	}
            });
            LinearLayout layoutWatch5 = (LinearLayout) customView.findViewById(R.id.layout_watch5);
            layoutWatch5.setOnClickListener(new OnClickListener() {
            	@Override
		    	public void onClick(View v) {
            		textShow.setText("ͼ����--����ͼƬ");
		    		popupWindow1.dismiss();	    
		    		flagOnTouch = 2; //��־����
		    		//��̬����android:scaleType="matrix"
		    		imageShow.setScaleType(ImageView.ScaleType.MATRIX);
		    		//��ͼ ͼƬ�ƶ���(0,0) �����ͼ������ָ�������
					matrix = new Matrix();
					matrix.postTranslate(0, 0);
					imageShow.setImageMatrix(matrix);
					canvas.drawBitmap(bmp, matrix, paint);  
					imageShow.setImageBitmap(alteredBitmap); //����ͼƬ
            	}
            });
    	}
    	/*
    	 * number=2 ��ǿ
    	 */
    	 if(number==2) { 
         	customView = getLayoutInflater().inflate(R.layout.popup_increase, null, false);  
         	//�����Ӵ���PopupWindow�߶�500 ���Ͷ� ɫ�� ����
         	popupWindow2 = new PopupWindow(customView, 600, 500);
         	// ʹ��ۼ� Ҫ������˵���ؼ����¼��ͱ���Ҫ���ô˷���   
             popupWindow2.setFocusable(true);  
             // ����������������ʧ   
             popupWindow2.setOutsideTouchable(true); 
             popupWindow2.setAnimationStyle(R.style.AnimationPreview);  
             // �Զ���view��Ӵ����¼�  
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
             seekBar1 = (SeekBar) customView.findViewById(R.id.seekBarSaturation);  //���Ͷ�
             seekBar2 = (SeekBar) customView.findViewById(R.id.seekBarHue);            //ɫ��
             seekBar3 = (SeekBar) customView.findViewById(R.id.seekBarLum);            //����
             /*
    	      * ����Seekbar�仯�����¼� 
    	      * ע��:��ʱ�޸Ļ�ӿ� 
    	      * ProcessActivity extends Activity implements OnSeekBarChangeListener
    	      */
    	     seekBar1.setOnSeekBarChangeListener(this);
    	     seekBar2.setOnSeekBarChangeListener(this);
    	     seekBar3.setOnSeekBarChangeListener(this);
    	     //�Զ���������
    	     increaseProcess = new IncreaseProcessImage(bmp); 
         }
    	/*
    	 * number=3 Ч��
    	 */
		if(number==3) { 
			customView = getLayoutInflater().inflate(R.layout.popup_effect, null, false);  
			popupWindow3 = new PopupWindow(customView, 450, 150);
		    popupWindow3.setFocusable(true); 
		    popupWindow3.setOutsideTouchable(true); 
		    popupWindow3.setAnimationStyle(R.style.AnimationPreview);  
		    // �Զ���view��Ӵ����¼�  
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
		    //�жϵ���Ӳ˵���ͬ��ťʵ�ֲ�ͬ����
		    //�Զ���������
	        effectProcess = new EffectProcessImage(bmp); 
		    LinearLayout layoutEffect1 = (LinearLayout) customView.findViewById(R.id.layout_effect_hj);
		    layoutEffect1.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("ͼ����--����Ч��");
		    		popupWindow3.dismiss();  
		    		//����EffectProcessImage.java�к���
		    		mbmp = effectProcess.OldRemeberImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect2 = (LinearLayout) customView.findViewById(R.id.layout_effect_fd);
		    layoutEffect2.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("ͼ����--����Ч��");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.ReliefImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect3 = (LinearLayout) customView.findViewById(R.id.layout_effect_gz);
		    layoutEffect3.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("ͼ����--����Ч��");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SunshineImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect4 = (LinearLayout) customView.findViewById(R.id.layout_effect_sm);
		    layoutEffect4.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("ͼ����--����Ч��");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SuMiaoImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    LinearLayout layoutEffect5 = (LinearLayout) customView.findViewById(R.id.layout_effect_rh);
		    layoutEffect5.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		textShow.setText("ͼ����--��Ч��");
		    		popupWindow3.dismiss();  
		    		mbmp = effectProcess.SharpenImage(bmp);
		    		imageShow.setImageBitmap(mbmp);
		    	}
		    });
		    
		}
		/*
		 * number=4 �߿�
		 */
        if(number==4) {
        	customView = getLayoutInflater().inflate(R.layout.popup_frame, null, false);  
        	popupWindow4 = new PopupWindow(customView, 450, 150);
            popupWindow4.setFocusable(true);  
            popupWindow4.setAnimationStyle(R.style.AnimationPreview);  
            // �Զ���view��Ӵ����¼�  
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
            //�жϵ���Ӳ˵���ͬ��ťʵ�ֲ�ͬ����
            //�Զ���������
            frameProcess = new FrameProcessImage(bmp);
            LinearLayout layoutFrame3 = (LinearLayout) customView.findViewById(R.id.layout_frame3);
            layoutFrame3.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("ͼ����--���ģʽ��");
            		popupWindow4.dismiss();
            		//��ȡ��� �Զ��庯��getImageFromAssets ��ȡassets����Դ
            		Bitmap frameBitmap = getImageFromAssets("image_frame_big_3.png");  
            		//��ʾͼ���������
            		mbmp = frameProcess.addFrameToImage(bmp,frameBitmap);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame2 = (LinearLayout) customView.findViewById(R.id.layout_frame2);
            layoutFrame2.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("ͼ����--���ģʽ��");
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
            		textShow.setText("ͼ����--���ģʽһ");
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
            		textShow.setText("ͼ����--Բ�Ǿ���");
            		popupWindow4.dismiss();
            		mbmp = frameProcess.RoundedCornerBitmap(bmp);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutFrame5 = (LinearLayout) customView.findViewById(R.id.layout_frame5);
            layoutFrame5.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("ͼ����--Բ�����");
            		popupWindow4.dismiss();
            		mbmp = frameProcess.RoundedBitmap(bmp);
            		imageShow.setImageBitmap(mbmp);
            	}
            });
        }
        /*
         * number=5 ���� -> ����
         */
        if(number==5) {
        	customView = getLayoutInflater().inflate(R.layout.popup_person, null, false);  
        	popupWindow5 = new PopupWindow(customView, 300, 150);
            popupWindow5.setFocusable(true);  
            popupWindow5.setAnimationStyle(R.style.AnimationPreview);  
            // �Զ���view��Ӵ����¼�  
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
            //�жϵ���Ӳ˵���ͬ��ťʵ�ֲ�ͬ����
            //�Զ���������
            personProcess = new PersonProcessImage(bmp);
            LinearLayout layoutPerson1 = (LinearLayout) customView.findViewById(R.id.layout_person1);
            layoutPerson1.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("����ͼ����SD��");
            		popupWindow5.dismiss();
            		try {     		
            			/*
            			 * ע�⣺�����ֻ�����������ʾͼƬ ���Զ���㲥ˢ����� ����saveBitmapToSD����ͼƬ
            			 */
            			if(mbmp == null) { //��ֹ����mbmp��
            				mbmp = bmp;
            			}
        				Uri uri = personProcess.saveBitmapToSD(mbmp);
        				Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        				intent.setData(uri);
        				sendBroadcast(intent);
        				Toast.makeText(ProcessActivity.this, "ͼ�񱣴�ɹ�", Toast.LENGTH_SHORT).show();
            		} catch(Exception e) {
            			e.printStackTrace();
            			Toast.makeText(ProcessActivity.this, "ͼ�񱣴�ʧ��", Toast.LENGTH_SHORT).show();
            		}
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutPerson2 = (LinearLayout) customView.findViewById(R.id.layout_person2);
            layoutPerson2.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("ȡ���������--�ָ�ԭͼ");
            		popupWindow5.dismiss();
            		mbmp = bmp;
            		imageShow.setImageBitmap(mbmp);
            	}
            });
            LinearLayout layoutPerson3 = (LinearLayout) customView.findViewById(R.id.layout_person3);
            layoutPerson3.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		textShow.setText("�ϴ�ͼƬ����������");
            		popupWindow5.dismiss();
            		try {
	            		if(mbmp == null) { //��ֹ����mbmp��
	        				mbmp = bmp;
	        			}
	            		//ͼ���ϴ� �ȱ��� �󴫵�ͼƬ·��
	            		Uri uri = personProcess.loadBitmap(mbmp);
	    				Intent intent  = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    				intent.setData(uri);
	    				sendBroadcast(intent);
	    				//�ϴ�ͼƬ*
	    				Intent intentPut = new Intent(ProcessActivity.this, MainActivity.class);
	    				String pathImage = null;
	    				intentPut.putExtra("pathProcess", personProcess.pathPicture );
	    				/*
	    				 * ���ػʹ��setResult ʹ��startActivity������ʾһ��ͼƬ��RunTime
	    				 * startActivity(intentPut);
	    				 * ��onActivityResult�л�ȡ����
	    				 */
	    				setResult(RESULT_OK, intentPut);
	            		//������һ����
	    				Toast.makeText(ProcessActivity.this, "ͼƬ�ϴ��ɹ�" , Toast.LENGTH_SHORT).show();
	            		ProcessActivity.this.finish(); 
	            	} catch(Exception e) {
            			e.printStackTrace();
            			Toast.makeText(ProcessActivity.this, "ͼ���ϴ�ʧ��", Toast.LENGTH_SHORT).show();
            		}
            	}
            });
           
        } //end if
    }
	
	//��ȡassets����Դ��ת��ΪBitmap  
	public Bitmap getImageFromAssets(String fileName)  
	{  
	    //Android��ʹ��assetsĿ¼�����Դ,������Ӧ���޷�ֱ�ӷ��ʵ�ԭ����Դ  
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
	 * ����SeekBar�����¼� 
	 * ���ǵ��PopupWindow2�ŵ����ý��� ����Ӱ����?
	 */
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
	{
		int flag = 0;
		switch(seekBar.getId()) {
		case R.id.seekBarSaturation: //���Ͷ�
			textShow.setText("ͼ����ǿ--���Ͷ�"+progress);
			flag = 0;
			increaseProcess.setSaturation(progress);
			break;
		case R.id.seekBarHue: //ɫ��
			textShow.setText("ͼ����ǿ--ɫ��"+progress);
			flag = 1;
			increaseProcess.SetHue(progress);
			break;
		case R.id.seekBarLum: //����
			textShow.setText("ͼ����ǿ--����"+progress);
			flag = 2;
			increaseProcess.SetLum(progress);
			break;
		}
		mbmp = increaseProcess.IncreaseProcessing(bmp,flag);
		imageShow.setImageBitmap(mbmp);
	}
	//SeekBar ��ʼ�϶� ����ProcessActivity����
	public void onStartTrackingTouch(SeekBar seekBar)
	{
		
	}
	//SeekBar ֹͣ�϶�
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		
	}
	
	/*
	 * End ProcessActivity
	 */
	
}
