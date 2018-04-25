package cn.hxc.imgrecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import cn.hxc.imgrecognitionSRI_OCR.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class saveSmg extends Activity {

	 private EditText content;
	 private SharedPreferences preferences;
	   @Override
	protected void onCreate(Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.savesms);
	    content=(EditText) findViewById(R.id.content); 
		
	    preferences=getSharedPreferences("set", MODE_PRIVATE);
	    content.setText(preferences.getString("content",content.getHint().toString().trim() ));	
	    content.setSelection(content.getText().length());
	}	 
	   public void save(View v){
		String smsContent=content.getText().toString().trim();
		File txtfile = new File(
				Environment.getExternalStorageDirectory()
						+ File.separator + "WR_LPAIS"+ File.separator + "txt");

		if (!txtfile.exists())
		{
			txtfile.mkdirs();
		}

		File tf=new File(
				Environment.getExternalStorageDirectory()
						+ File.separator + "WR_LPAIS"+ File.separator + "txt"+ File.separator+"threshold.txt");

		FileWriter fw = null;
		try {
			fw = new FileWriter(tf, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrintWriter pw = new PrintWriter(fw);
		pw.print(smsContent);
		pw.print("\r\n");
		pw.flush();
		try
		{
			fw.flush();
			pw.close();
			fw.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Toast.makeText(this, "设置成功！", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(saveSmg.this,MainActivity.class);
		startActivity(intent);
		}
}

