package cn.hxc.imgrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageThumbnail {
	public static int getBitmapDegree(String path) {
	    int degree = 0;
	    try {
	        // ��ָ��·���¶�ȡͼƬ������ȡ��EXIF��Ϣ
	        ExifInterface exifInterface = new ExifInterface(path);
	        // ��ȡͼƬ����ת��Ϣ
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
	                ExifInterface.ORIENTATION_UNDEFINED);
	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            degree = 90;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            degree = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            degree = 270;
	            break;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return degree;
	} 
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
	    Bitmap returnBm = null;
	  
	    // ������ת�Ƕȣ�������ת����
	    Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
	    try {
	        // ��ԭʼͼƬ������ת���������ת�����õ��µ�ͼƬ
	        returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	    } catch (OutOfMemoryError e) {
	    }
	    if (returnBm == null) {
	        returnBm = bm;
	    }
	    if (bm != returnBm) {
	      //  bm.recycle();
	    }
	    return returnBm;
	} 

	public static Bitmap PicZoom(Bitmap bmp, float width, float height, int margain) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(margain);
		matrix.postScale( width / bmpWidth,  height / bmpHeight);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
	}
	public static Bitmap picScaleTo32(Bitmap bmp){		
		Matrix matrix = new Matrix();
		//matrix.setRotate(90);
		matrix.postScale((float)16/bmp.getWidth(), (float)32/bmp.getHeight());
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);	
		
	}
//	}
	
}
