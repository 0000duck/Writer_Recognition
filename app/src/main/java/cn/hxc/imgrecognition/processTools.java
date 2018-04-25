package cn.hxc.imgrecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.hxc.imgrecognitionSRI_OCR.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.widget.Toast;

public class processTools {

	private SharedPreferences preferences;
	private Bitmap greyBitmap;
	private int[] pixels;
	private int width;
	private int height;
	private PreferencesService service;
	private String content;
	private int sorta[];
	private Bitmap greyBitmap1;
	private Bitmap sourceBitmap;
	private boolean isWriteRecogize;
	private int[] by;
	private SoundPool soundPool;
	private Context context;

//	public native String callint(int[] by1, int w, int h, String num,
//			String win2, String whi2, String model, int flag);

	public processTools(Context context) {
		super();
		this.context = context;
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(context, R.raw.sou1, 1);
		preferences = context.getSharedPreferences("set", context.MODE_PRIVATE);

	}

	/*
	 * // 灰度化
	 */
	public String greyScreen(Bitmap screenBitmap) throws IOException {

		String phoneNum = "";
		System.out.println("greyscreen...");
		// savePic(screenBitmap);
		// 转换为灰度图
		// greyBitmap = imageprocess.RGBToGrey(screenBitmap);
		greyBitmap = imageProcess.greyToArray(screenBitmap);

		width = greyBitmap.getWidth();
		height = greyBitmap.getHeight();
		
		imageProcess.noequl("width---------", width);
		imageProcess.noequl("height---------", height);

		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + "WR_LPAIS" + File.separator + "Data";
		String strNum = filePath + "//num2";
		String strWin2 = filePath + "//win2.dat";
		String strWhi2 = filePath + "//whi2.dat";
		String strModel = filePath + "//model14.dat";
		String strResult = filePath + "//result.txt";
		String xyjtxt = filePath + "//xyj.txt";

		if (!(isDateExist(strNum) && isDateExist(strWin2)
				&& isDateExist(strWhi2) && isDateExist(strModel))) {
//			Toast.makeText(context, "请重新打开，不要再把数据文件删除了！", 1).show();
			
		} else {
			// 取出截取的灰度图
			pixels = new int[width * height];
			// 数组过大，内存溢出
			greyBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			by = new int[width * height];
			// int[] by1 = new int[width * height];
			for (int i = 0; i < width * height; i++) {
				by[i] = (0xff & pixels[i]);
			}

			long start = System.currentTimeMillis();
//			 phoneNum = callint(by, width, height, strNum, strWin2,
//					strWhi2, strModel, 11);
			long end = System.currentTimeMillis();

			if (phoneNum.trim().length() == 11) {
				soundPool.play(1, 1, 1, 0, 0, 1);
			//	saveResultToSD(phoneNum + " time:" + (end - start), strResult);
			}else{
				phoneNum="";
			}
		}
		return phoneNum;

	}

	public boolean isDateExist(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void saveResultToSD(String num, String filePath) throws IOException {
		int resultCount = (int) preferences.getFloat("resultCount", 1);
		File file = new File(filePath);
		if (!file.exists()) {
			resultCount = 1;
			file.createNewFile();
		}
		FileWriter myOutput = new FileWriter(file,true);
		myOutput.write(resultCount + ":" + num + "\n");
		Editor editor = preferences.edit();
		resultCount++;
		editor.putFloat("resultCount", resultCount);
		editor.commit();

		myOutput.flush();
		myOutput.close();
	}

}
