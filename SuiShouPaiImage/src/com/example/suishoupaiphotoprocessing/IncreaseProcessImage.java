package com.example.suishoupaiphotoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

//自定义类实现图像增强效果处理 包括:饱和度、色相、亮度
public class IncreaseProcessImage 
{
	private Bitmap mBitmap;
	
	//构造函数
	public IncreaseProcessImage(Bitmap bmp)
	{
			mBitmap = bmp;
	}
	
	//自定义变量
	private ColorMatrix mSaturationMatrix;  //饱和度
	private ColorMatrix mHueMatrix;            //色相
	private ColorMatrix mLumMatrix;            //亮度
	private ColorMatrix mAllMatrix;            
	private float mSaturationValue = 0F;
	private float mHueValue = 0F;
	private float mLumValue = 0F;
	//SeekBar中间值127 [0-255]
	private static final int MIDDLE_VALUE = 127;
	private static final int MAX_VALUE =255;
	
	/*
	 * 设置饱和度值
	 */
	public void setSaturation(int value) {
		mSaturationValue = value * 1.0F / MIDDLE_VALUE;
	}
	
	/*
	 * 设置色相值
	 */
	public void SetHue(int value) {
		mHueValue = (value - MIDDLE_VALUE) * 1.0F / MIDDLE_VALUE  * 180;
	}
	
	/*
	 * 设置亮度值
	 */
	public void SetLum(int value) {
		mLumValue = value * 1.0F / MIDDLE_VALUE;
	}
	
	/*
	 * 图像增强 
	 * 饱和度处理 色相处理 亮度处理
	 * flag=0表示是否改变饱和度 flag=1表示是否改变色相 flag=2表示是否改变亮度
	 */
	public Bitmap IncreaseProcessing(Bitmap bmp, int flag)
	{
		//创建一个相同尺寸可变的位图区,用于绘制新图
		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);   
		Paint paint = new Paint();
		paint.setAntiAlias(true);       //给Canvas加上抗锯齿标志,边缘平滑处理
		if(mAllMatrix == null) {
			mAllMatrix = new ColorMatrix();
		}
		if(mSaturationMatrix == null) {
			mSaturationMatrix = new ColorMatrix();
		}
		if(mHueMatrix == null) {
			mHueMatrix = new ColorMatrix();
		}
		if(mLumMatrix == null) {
			mLumMatrix = new ColorMatrix();
		}
		//图像处理
		if(flag==0) { //饱和度
			//饱和度值最小可设为0,此时表示灰度图 为1表示饱和度不变,设置大于1就是过饱和
			mSaturationMatrix.reset(); //设为默认值
			mSaturationMatrix.setSaturation(mSaturationValue);
		}
		else if(flag==1) { //色相
			//hueColor是色轮旋转角度,正值表示顺时针旋转,负值表示逆时针旋转
			mHueMatrix.reset(); 
			mHueMatrix.setRotate(0, mHueValue); //控制让红色区在色轮上旋转的角度
			mHueMatrix.setRotate(1, mHueValue); //控制让绿色区在色轮上旋转的角度
			mHueMatrix.setRotate(2, mHueValue); //控制让蓝色取在色轮上旋转的角度					
		}
		else if(flag==2) { //亮度 
			mLumMatrix.reset(); 
			//红绿蓝三色按相同比例,最后参数1表示透明度不变
			mLumMatrix.setScale(mLumValue, mLumValue, mLumValue, 1);
		}
		//设置AllMatrix 
		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix); //效果叠加
		mAllMatrix.postConcat(mSaturationMatrix);
		mAllMatrix.postConcat(mLumMatrix);
		//设置颜色变换效果
		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
		canvas.drawBitmap(bmp, 0, 0, paint); //颜色变化后输出到新创建位图区
		return bitmap;
	}
	
	/*
	 * End
	 */
}
