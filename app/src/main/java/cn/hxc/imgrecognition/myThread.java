package cn.hxc.imgrecognition;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

public class myThread extends Thread {
	private Bitmap screenBitmap;
	private processTools pt;
	public myThread(Context context){
		super();
	    pt=new processTools(context);
	}
     @Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();    	
    	try {
    		System.out.println("thread run...");
			pt.greyScreen(screenBitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
	public Bitmap getScreenBitmap() {
		return screenBitmap;
	}
	public void setScreenBitmap(Bitmap screenBitmap) {
		this.screenBitmap = screenBitmap;
	}
}
