package com.example.suishoupaiphotoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

//�Զ�����ʵ��ͼ���ܴ��� ����:
public class FrameProcessImage 
{
	private Bitmap mBitmap;
	
	//���췽��
	public FrameProcessImage(Bitmap bmp)
	{
		mBitmap = bmp;
	}
	
	/*
	 * 3.ͼƬ�ϳ� ģʽ�� ģʽ�� ģʽһ
	 * �������ͬ ��ʾЧ����ͬ ��������һ����   
	 */
	public Bitmap addFrameToImage(Bitmap bmp, Bitmap frameBitmap) //bmpԭͼ frameBitmap��ԴͼƬ(�߿�)  
	{  
	    //bmpԭͼ ������λͼ  
	    int width = bmp.getWidth();    
	    int height = bmp.getHeight();  
	    Bitmap drawBitmap =Bitmap.createBitmap(width, height, Config.RGB_565);  
	    //�Ա߿��������  
	    int w = frameBitmap.getWidth();  
	    int h = frameBitmap.getHeight();  
	    float scaleX = width*1F / w;        //���ű� ���ͼƬ�ߴ糬���߿�ߴ� ���Զ�ƥ��  
	    float scaleY = height*1F / h;  
	    Matrix matrix = new Matrix();  
	    matrix.postScale(scaleX, scaleY);   //����ͼƬ  
	    Bitmap copyBitmap =  Bitmap.createBitmap(frameBitmap, 0, 0, w, h, matrix, true);  
	      
	    int pixColor = 0;    
	    int layColor = 0;    
	    int newColor = 0;  
	      
	    int pixR = 0;    
	    int pixG = 0;    
	    int pixB = 0;    
	    int pixA = 0;    
	        
	    int newR = 0;    
	    int newG = 0;    
	    int newB = 0;    
	    int newA = 0;    
	        
	    int layR = 0;    
	    int layG = 0;    
	    int layB = 0;    
	    int layA = 0;    
	        
	    float alpha = 0.8F;   
	    float alphaR = 0F;    
	    float alphaG = 0F;    
	    float alphaB = 0F;  
	      
	    for (int i = 0; i < width; i++)    
	    {    
	        for (int k = 0; k < height; k++)    
	        {    
	            pixColor = bmp.getPixel(i, k);    
	            layColor = copyBitmap.getPixel(i, k);    
	            // ��ȡԭͼƬ��RGBAֵ     
	            pixR = Color.red(pixColor);    
	            pixG = Color.green(pixColor);    
	            pixB = Color.blue(pixColor);    
	            pixA = Color.alpha(pixColor);    
	            // ��ȡ�߿�ͼƬ��RGBAֵ     
	            layR = Color.red(layColor);    
	            layG = Color.green(layColor);    
	            layB = Color.blue(layColor);    
	            layA = Color.alpha(layColor);    
	            // ��ɫ�봿��ɫ����ĵ�     
	            if (layR < 20 && layG < 20 && layB < 20) {    
	                alpha = 1F;    
	            } else {    
	                alpha = 0.3F;    
	            }    
	            alphaR = alpha;    
	            alphaG = alpha;    
	            alphaB = alpha;    
	            // ������ɫ����     
	            newR = (int) (pixR * alphaR + layR * (1 - alphaR));    
	            newG = (int) (pixG * alphaG + layG * (1 - alphaG));    
	            newB = (int) (pixB * alphaB + layB * (1 - alphaB));    
	            layA = (int) (pixA * alpha + layA * (1 - alpha));    
	            // ֵ��0~255֮��     
	            newR = Math.min(255, Math.max(0, newR));    
	            newG = Math.min(255, Math.max(0, newG));    
	            newB = Math.min(255, Math.max(0, newB));    
	            newA = Math.min(255, Math.max(0, layA));    
	            //����   
	            newColor = Color.argb(newA, newR, newG, newB);    
	            drawBitmap.setPixel(i, k, newColor);    
	        }    
	    }    
	    return drawBitmap; 
	}  
	
	/*
	 * 4.Բ�Ǿ���ͼƬ���
	 */
	public Bitmap RoundedCornerBitmap(Bitmap bitmap) 
	{	
		Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), 
	         bitmap.getHeight(), Config.ARGB_8888); 
	     Canvas canvas = new Canvas(roundBitmap); 
	     int color = 0xff424242; 
	     Paint paint = new Paint(); 
	     Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	     RectF rectF = new RectF(rect); 
	     float roundPx = 80;       
	     //����Բ��
	     paint.setAntiAlias(true); 
	     canvas.drawARGB(0, 0, 0, 0); 
	     paint.setColor(color); 
	     canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	     canvas.drawBitmap(bitmap, rect, rect, paint); 
	     //����ͼƬ
	     return roundBitmap; 
	}
	
	/*
	 * 5.ԭ��ͼ�����
	 */
	public Bitmap RoundedBitmap(Bitmap bitmap)
	{
		Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), 
		         bitmap.getHeight(), Config.ARGB_8888); 
	     Canvas canvas = new Canvas(roundBitmap); 
	     int color = 0xff424242; 
	     Paint paint = new Paint(); 
	     //����Բ�ΰ뾶
	     int radius; 
	     if(bitmap.getWidth()>bitmap.getHeight()) {
	    	 radius = bitmap.getHeight()/2;
	     }
	     else {
	    	 radius = bitmap.getWidth()/2;
	     }
	     //����Բ��
	     paint.setAntiAlias(true); 
	     canvas.drawARGB(0, 0, 0, 0); 
	     paint.setColor(color); 
	     canvas.drawCircle( bitmap.getWidth()/ 2, bitmap.getHeight() / 2, radius, paint);
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	     canvas.drawBitmap(bitmap, 0, 0, paint);
	     //��ʾͼƬ
	     return roundBitmap; 
	}

	/*
	 * End
	 */
}
