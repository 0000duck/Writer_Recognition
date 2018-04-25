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

//自定义类实现图像框架处理 包括:
public class FrameProcessImage 
{
	private Bitmap mBitmap;
	
	//构造方法
	public FrameProcessImage(Bitmap bmp)
	{
		mBitmap = bmp;
	}
	
	/*
	 * 3.图片合成 模式三 模式二 模式一
	 * 载入相框不同 显示效果不同 方法都是一样的   
	 */
	public Bitmap addFrameToImage(Bitmap bmp, Bitmap frameBitmap) //bmp原图 frameBitmap资源图片(边框)  
	{  
	    //bmp原图 创建新位图  
	    int width = bmp.getWidth();    
	    int height = bmp.getHeight();  
	    Bitmap drawBitmap =Bitmap.createBitmap(width, height, Config.RGB_565);  
	    //对边框进行缩放  
	    int w = frameBitmap.getWidth();  
	    int h = frameBitmap.getHeight();  
	    float scaleX = width*1F / w;        //缩放比 如果图片尺寸超过边框尺寸 会自动匹配  
	    float scaleY = height*1F / h;  
	    Matrix matrix = new Matrix();  
	    matrix.postScale(scaleX, scaleY);   //缩放图片  
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
	            // 获取原图片的RGBA值     
	            pixR = Color.red(pixColor);    
	            pixG = Color.green(pixColor);    
	            pixB = Color.blue(pixColor);    
	            pixA = Color.alpha(pixColor);    
	            // 获取边框图片的RGBA值     
	            layR = Color.red(layColor);    
	            layG = Color.green(layColor);    
	            layB = Color.blue(layColor);    
	            layA = Color.alpha(layColor);    
	            // 颜色与纯黑色相近的点     
	            if (layR < 20 && layG < 20 && layB < 20) {    
	                alpha = 1F;    
	            } else {    
	                alpha = 0.3F;    
	            }    
	            alphaR = alpha;    
	            alphaG = alpha;    
	            alphaB = alpha;    
	            // 两种颜色叠加     
	            newR = (int) (pixR * alphaR + layR * (1 - alphaR));    
	            newG = (int) (pixG * alphaG + layG * (1 - alphaG));    
	            newB = (int) (pixB * alphaB + layB * (1 - alphaB));    
	            layA = (int) (pixA * alpha + layA * (1 - alpha));    
	            // 值在0~255之间     
	            newR = Math.min(255, Math.max(0, newR));    
	            newG = Math.min(255, Math.max(0, newG));    
	            newB = Math.min(255, Math.max(0, newB));    
	            newA = Math.min(255, Math.max(0, layA));    
	            //绘制   
	            newColor = Color.argb(newA, newR, newG, newB);    
	            drawBitmap.setPixel(i, k, newColor);    
	        }    
	    }    
	    return drawBitmap; 
	}  
	
	/*
	 * 4.圆角矩形图片相框
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
	     //绘制圆形
	     paint.setAntiAlias(true); 
	     canvas.drawARGB(0, 0, 0, 0); 
	     paint.setColor(color); 
	     canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	     canvas.drawBitmap(bitmap, rect, rect, paint); 
	     //返回图片
	     return roundBitmap; 
	}
	
	/*
	 * 5.原型图像相框
	 */
	public Bitmap RoundedBitmap(Bitmap bitmap)
	{
		Bitmap roundBitmap = Bitmap.createBitmap(bitmap.getWidth(), 
		         bitmap.getHeight(), Config.ARGB_8888); 
	     Canvas canvas = new Canvas(roundBitmap); 
	     int color = 0xff424242; 
	     Paint paint = new Paint(); 
	     //设置圆形半径
	     int radius; 
	     if(bitmap.getWidth()>bitmap.getHeight()) {
	    	 radius = bitmap.getHeight()/2;
	     }
	     else {
	    	 radius = bitmap.getWidth()/2;
	     }
	     //绘制圆形
	     paint.setAntiAlias(true); 
	     canvas.drawARGB(0, 0, 0, 0); 
	     paint.setColor(color); 
	     canvas.drawCircle( bitmap.getWidth()/ 2, bitmap.getHeight() / 2, radius, paint);
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	     canvas.drawBitmap(bitmap, 0, 0, paint);
	     //显示图片
	     return roundBitmap; 
	}

	/*
	 * End
	 */
}
