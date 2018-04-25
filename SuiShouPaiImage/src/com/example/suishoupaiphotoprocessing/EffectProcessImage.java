package com.example.suishoupaiphotoprocessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

//�Զ�����ʵ��ͼ��Ч������ ����:��Ч�����񡢹��ա����衢��
public class EffectProcessImage  
{
	private Bitmap mBitmap;
	
	//���췽��
	public EffectProcessImage(Bitmap bmp)
	{
		mBitmap = bmp;
	}
	
	/*
	 * 1.ͼƬ���ɴ���  
	 */
	public Bitmap OldRemeberImage(Bitmap bmp)  
	{  
	    /* 
	     * ���ɴ����㷨�������µ�RGB 
	     * R=0.393r+0.769g+0.189b 
	     * G=0.349r+0.686g+0.168b 
	     * B=0.272r+0.534g+0.131b 
	     */  
	    int width = bmp.getWidth();  
	    int height = bmp.getHeight();  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
	    int pixColor = 0;  
	    int pixR = 0;  
	    int pixG = 0;  
	    int pixB = 0;  
	    int newR = 0;  
	    int newG = 0;  
	    int newB = 0;  
	    int[] pixels = new int[width * height];  
	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
	    for (int i = 0; i < height; i++)  
	    {  
	        for (int k = 0; k < width; k++)  
	        {  
	            pixColor = pixels[width * i + k];  
	            pixR = Color.red(pixColor);  
	            pixG = Color.green(pixColor);  
	            pixB = Color.blue(pixColor);  
	            newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);  
	            newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);  
	            newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);  
	            int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);  
	            pixels[width * i + k] = newColor;  
	        }  
	    }  
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	    return bitmap; 
	}  
	
	/* 
	 * 2.ͼƬ������  
	 * ��ƬЧ��Ҳ�ǳ���:����ǰ���ص��RGBֵ�ֱ���255֮����ֵ��Ϊ��ǰ���RGB  
	 * �Ҷ�ͼ��:ͨ��ʹ�õķ�����gray=0.3*pixR+0.59*pixG+0.11*pixB
	 */  
	public Bitmap ReliefImage(Bitmap bmp)  
	{  
	    /* 
	     * �㷨ԭ��(ǰһ�����ص�RGB-��ǰ���ص�RGB+127)��Ϊ��ǰ���ص�RGBֵ 
	     * ��ABC�м���B�㸡��Ч��(RGBֵ��0~255) 
	     * B.r = C.r - B.r + 127 
	     * B.g = C.g - B.g + 127 
	     * B.b = C.b - B.b + 127 
	     */  
	    int width = bmp.getWidth();  
	    int height = bmp.getHeight();  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
	    int pixColor = 0;  
	    int pixR = 0;  
	    int pixG = 0;  
	    int pixB = 0;  
	    int newR = 0;  
	    int newG = 0;  
	    int newB = 0;  
	    int[] pixels = new int[width * height];  
	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
	    for (int i = 1; i < height-1; i++)  
	    {  
	        for (int k = 1; k < width-1; k++)  
	        {  
	            //��ȡǰһ��������ɫ  
	            pixColor = pixels[width * i + k];     
	            pixR = Color.red(pixColor);  
	            pixG = Color.green(pixColor);  
	            pixB = Color.blue(pixColor);  
	            //��ȡ��ǰ����  
	            pixColor = pixels[(width * i + k) + 1];  
	            newR = Color.red(pixColor) - pixR +127;  
	            newG = Color.green(pixColor) - pixG +127;  
	            newB = Color.blue(pixColor) - pixB +127;  
	            newR = Math.min(255, Math.max(0, newR));  
	            newG = Math.min(255, Math.max(0, newG));  
	            newB = Math.min(255, Math.max(0, newB));  
	            pixels[width * i + k] = Color.argb(255, newR, newG, newB);  
	        }  
	    }  
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	    return bitmap;
	}  

	/*
	 * 3.ͼƬ����Ч��  
	 */
	public Bitmap SunshineImage(Bitmap bmp)  
	{  
	    /* 
	     * �㷨ԭ��(ǰһ�����ص�RGB-��ǰ���ص�RGB+127)��Ϊ��ǰ���ص�RGBֵ 
	     * ��ABC�м���B�㸡��Ч��(RGBֵ��0~255) 
	     * B.r = C.r - B.r + 127 
	     * B.g = C.g - B.g + 127 
	     * B.b = C.b - B.b + 127 
	     * ��������ȡ�����СֵΪ�뾶,Ҳ�����Զ�������Ͻ������ 
	     */       
	    int width = bmp.getWidth();  
	    int height = bmp.getHeight();  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
	    int pixColor = 0;  
	    int pixR = 0;  
	    int pixG = 0;  
	    int pixB = 0;  
	    int newR = 0;  
	    int newG = 0;  
	    int newB = 0;  
	    //Χ��Բ�ι���  
	    int centerX = width / 2;  
	    int centerY = height / 2;  
	    int radius = Math.min(centerX, centerY);  
	    float strength = 150F;  //����ǿ��100-150  
	    int[] pixels = new int[width * height];  
	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
	    for (int i = 1; i < height-1; i++)  
	    {  
	        for (int k = 1; k < width-1; k++)  
	        {  
	            //��ȡǰһ��������ɫ  
	            pixColor = pixels[width * i + k];     
	            pixR = Color.red(pixColor);  
	            pixG = Color.green(pixColor);  
	            pixB = Color.blue(pixColor);  
	            newR = pixR;  
	            newG = pixG;  
	            newB = pixB;  
	            //���㵱ǰ�㵽�������ĵľ���,ƽ������ϵ������֮��ľ���  
	            int distance = (int) (Math.pow((centerY-i), 2) + Math.pow((centerX-k), 2));  
	            if(distance < radius*radius)  
	            {  
	                //���վ����С������ǿ�Ĺ���ֵ  
	                int result = (int)(strength*( 1.0-Math.sqrt(distance) / radius ));  
	                newR = pixR + result;  
	                newG = newG + result;  
	                newB = pixB + result;  
	            }  
	            newR = Math.min(255, Math.max(0, newR));  
	            newG = Math.min(255, Math.max(0, newG));  
	            newB = Math.min(255, Math.max(0, newB));  
	            pixels[width * i + k] = Color.argb(255, newR, newG, newB);  
	        }  
	    }  
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
	    return bitmap;
	}  


	/*
	 * 4.ͼƬ����Ч��  
	 */
	public Bitmap SuMiaoImage(Bitmap bmp)  
	{  
	    //������Bitmap  
	    int width = bmp.getWidth();    
	    int height = bmp.getHeight();    
	    int[] pixels = new int[width * height];    //�洢�任ͼ��  
	    int[] linpix = new int[width * height];     //�洢�Ҷ�ͼ��  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);    
	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
	    int pixColor = 0;  
	    int pixR = 0;  
	    int pixG = 0;  
	    int pixB = 0;  
	    int newR = 0;    
	    int newG = 0;    
	    int newB = 0;  
	    //�Ҷ�ͼ��  
	    for (int i = 1; i < width - 1; i++)    
	    {    
	        for (int j = 1; j < height - 1; j++)   //������˹����ģ�� { 0, -1, 0, -1, -5, -1, 0, -1, 0   
	        {    
	            //��ȡǰһ��������ɫ  
	            pixColor = pixels[width * i + j];     
	            pixR = Color.red(pixColor);  
	            pixG = Color.green(pixColor);  
	            pixB = Color.blue(pixColor);  
	            //�Ҷ�ͼ��  
	            int gray=(int)(0.3*pixR+0.59*pixG+0.11*pixB);  
	            linpix[width * i + j] = Color.argb(255, gray, gray, gray);  
	            //ͼ����  
	            gray=255-gray;  
	            pixels[width * i + j] = Color.argb(255, gray, gray, gray);  
	        }  
	    }  
	    int radius = Math.min(width/2, height/2);  
	    int[] copixels = gaussBlur(pixels, width, height, 10, 10/3);   //��˹ģ�� ���ð뾶10  
	    int[] result = colorDodge(linpix, copixels);   //����ͼ�� ��ɫ����  
	    bitmap.setPixels(result, 0, width, 0, 0, width, height);  
	    return bitmap;
	}  
	  
	//��˹ģ��  
	public static int[] gaussBlur(int[] data, int width, int height, int radius, float sigma) {    
	    
	    float pa = (float) (1 / (Math.sqrt(2 * Math.PI) * sigma));    
	    float pb = -1.0f / (2 * sigma * sigma);    
	    // generate the Gauss Matrix     
	    float[] gaussMatrix = new float[radius * 2 + 1];    
	    float gaussSum = 0f;    
	    for (int i = 0, x = -radius; x <= radius; ++x, ++i) {    
	        float g = (float) (pa * Math.exp(pb * x * x));    
	        gaussMatrix[i] = g;    
	        gaussSum += g;    
	    }    
	    for (int i = 0, length = gaussMatrix.length; i < length; ++i) {    
	        gaussMatrix[i] /= gaussSum;    
	    }    
	    
	    // x direction     
	    for (int y = 0; y < height; ++y) {    
	        for (int x = 0; x < width; ++x) {    
	            float r = 0, g = 0, b = 0;    
	            gaussSum = 0;    
	            for (int j = -radius; j <= radius; ++j) {    
	                int k = x + j;    
	                if (k >= 0 && k < width) {    
	                    int index = y * width + k;    
	                    int color = data[index];    
	                    int cr = (color & 0x00ff0000) >> 16;    
	                    int cg = (color & 0x0000ff00) >> 8;    
	                    int cb = (color & 0x000000ff);    
	    
	                    r += cr * gaussMatrix[j + radius];    
	                    g += cg * gaussMatrix[j + radius];    
	                    b += cb * gaussMatrix[j + radius];    
	    
	                    gaussSum += gaussMatrix[j + radius];    
	                }    
	            }    
	            int index = y * width + x;    
	            int cr = (int) (r / gaussSum);    
	            int cg = (int) (g / gaussSum);    
	            int cb = (int) (b / gaussSum);    
	            data[index] = cr << 16 | cg << 8 | cb | 0xff000000;    
	        }    
	    }    
	    
	    // y direction     
	    for (int x = 0; x < width; ++x) {    
	        for (int y = 0; y < height; ++y) {    
	            float r = 0, g = 0, b = 0;    
	            gaussSum = 0;    
	            for (int j = -radius; j <= radius; ++j) {    
	                int k = y + j;    
	                if (k >= 0 && k < height) {    
	                    int index = k * width + x;    
	                    int color = data[index];    
	                    int cr = (color & 0x00ff0000) >> 16;    
	                    int cg = (color & 0x0000ff00) >> 8;    
	                    int cb = (color & 0x000000ff);    
	    
	                    r += cr * gaussMatrix[j + radius];    
	                    g += cg * gaussMatrix[j + radius];    
	                    b += cb * gaussMatrix[j + radius];    
	    
	                    gaussSum += gaussMatrix[j + radius];    
	                }    
	            }    
	            int index = y * width + x;    
	            int cr = (int) (r / gaussSum);    
	            int cg = (int) (g / gaussSum);    
	            int cb = (int) (b / gaussSum);    
	            data[index] = cr << 16 | cg << 8 | cb | 0xff000000;    
	        }    
	    }      
	    return data;  
	}    
	  
	//��ɫ����  
	public static int[] colorDodge(int[] baseColor, int[] mixColor) {    
	    for (int i = 0, length = baseColor.length; i < length; ++i) {    
	        int bColor = baseColor[i];    
	        int br = (bColor & 0x00ff0000) >> 16;    
	        int bg = (bColor & 0x0000ff00) >> 8;    
	        int bb = (bColor & 0x000000ff);    
	    
	        int mColor = mixColor[i];    
	        int mr = (mColor & 0x00ff0000) >> 16;    
	        int mg = (mColor & 0x0000ff00) >> 8;    
	        int mb = (mColor & 0x000000ff);    
	    
	        int nr = colorDodgeFormular(br, mr);    
	        int ng = colorDodgeFormular(bg, mg);    
	        int nb = colorDodgeFormular(bb, mb);    
	    
	        baseColor[i] = nr << 16 | ng << 8 | nb | 0xff000000;    
	    }    
	    return baseColor;  
	}    
	    
	private static int colorDodgeFormular(int base, int mix) {    
	    int result = base + (base * mix) / (255 - mix);    
	    result = result > 255 ? 255 : result;    
	    return result;    
	}  

	/*
	 * 5.ͼ���񻯴��� ������˹���Ӵ���  
	 */
	public Bitmap SharpenImage(Bitmap bmp)  
	{  
	    /* 
	     * �񻯻���˼���Ǽ�ǿͼ���о���ı�Ե������,ʹͼ�������� 
	     * ��ͼ��ƽ����ʹͼ���б߽���������ģ�� 
	     *  
	     * ������˹����ͼ���� 
	     * ��ȡ��Χ9����ľ������ģ��9���ľ��� ��� 
	     */  
	    //������˹����ģ�� { 0, -1, 0, -1, -5, -1, 0, -1, 0 } { -1, -1, -1, -1, 9, -1, -1, -1, -1 }  
	    int[] laplacian = new int[] {  -1, -1, -1, -1, 9, -1, -1, -1, -1 };   
	    int width = bmp.getWidth();    
	    int height = bmp.getHeight();    
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);    
	    int pixR = 0;    
	    int pixG = 0;    
	    int pixB = 0;    
	    int pixColor = 0;    
	    int newR = 0;    
	    int newG = 0;    
	    int newB = 0;    
	    int idx = 0;    
	    float alpha = 0.3F;  //ͼƬ͸����  
	    int[] pixels = new int[width * height];    
	    bmp.getPixels(pixels, 0, width, 0, 0, width, height);    
	    //ͼ����  
	    for (int i = 1; i < height - 1; i++)    
	    {    
	        for (int k = 1; k < width - 1; k++)    
	        {    
	            idx = 0;  
	            newR = 0;    
	            newG = 0;    
	            newB = 0;    
	            for (int n = -1; n <= 1; n++)   //ȡ��ͼ��3*3��������   
	            {    
	                for (int m = -1; m <= 1; m++)  //n�������� m�б任  
	                {    
	                    pixColor = pixels[(i + n) * width + k + m];  //��ǰ��(i,k)  
	                    pixR = Color.red(pixColor);    
	                    pixG = Color.green(pixColor);    
	                    pixB = Color.blue(pixColor);    
	                    //ͼ���������Ӧ�������     
	                    newR = newR + (int) (pixR * laplacian[idx] * alpha);    
	                    newG = newG + (int) (pixG * laplacian[idx] * alpha);    
	                    newB = newB + (int) (pixB * laplacian[idx] * alpha);    
	                    idx++;   
	                }    
	            }  
	            newR = Math.min(255, Math.max(0, newR));    
	            newG = Math.min(255, Math.max(0, newG));    
	            newB = Math.min(255, Math.max(0, newB));    
	            //��ֵ    
	            pixels[i * width + k] = Color.argb(255, newR, newG, newB);     
	        }  
	    }  
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);    
	    return bitmap;
	}  
}
