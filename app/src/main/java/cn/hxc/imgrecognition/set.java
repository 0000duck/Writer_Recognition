package cn.hxc.imgrecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hxc.imgrecognitionSRI_OCR.R;

public class set extends Activity {
	static int marg = 0;
	private PreferencesService service;
	private SharedPreferences preferences;

	public ImageButton B_contrast;
	public ImageButton B_QueryLoc;
	public ImageButton B_QueryDB;
	public ImageButton B_set;
	public ImageButton B_blist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		B_contrast = (ImageButton) findViewById(R.id.B_contrast);
		B_QueryLoc = (ImageButton) findViewById(R.id.B_QueryLoc);
		B_QueryDB = (ImageButton) findViewById(R.id.B_QueryDB);
		B_set = (ImageButton) findViewById(R.id.B_set);
		B_blist = (ImageButton) findViewById(R.id.B_blist);

		B_contrast.setBackgroundResource(R.drawable.contrast);
		B_QueryLoc.setBackgroundResource(R.drawable.searchloc);
		B_QueryDB.setBackgroundResource(R.drawable.searchdb);
		B_set.setBackgroundResource(R.drawable.set_change);
		B_blist.setBackgroundResource(R.drawable.blacklist);

		service = new PreferencesService(this);
		Map<String, String> params = service.getPreferences("margain");// new
																		// Integer(params.get("margain")
		if (params.get("margain") == null) {
			service.save("0", "margain");
		}

		preferences = getSharedPreferences("set",
				MODE_PRIVATE);
		
		if (!preferences.contains("numLen")) {
			Editor editor = preferences.edit();
			editor.putInt("numLen", 0);
			editor.commit();
		}
		
		int numlen=preferences.getInt("numLen", 0);

	}

	public void checkClick(View v) {

		int numlen=preferences.getInt("numLen", 0);
			Editor editor = preferences.edit();
			editor.putInt("numLen", (numlen+1)%2);
			editor.commit();
		
		
	}

	public void margain(View v) {

		inputTitleDialog();
	}

	public void save(View v) {
		Intent intent = new Intent(this, savecandidate.class);
		startActivity(intent);
	}

	public void shujuzhengli(View v) {
		Intent intent = new Intent(this, chooseImage.class);
		startActivity(intent);
	}

	public void Yuzhisave(View v) {
		int x = 4;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setSingleChoiceItems(new String[] {"严格","标准","宽松","排序"}, 0,new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				File txtfile = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "WR_LPAIS" + File.separator + "txt");

				if (!txtfile.exists()) {
					txtfile.mkdirs();
				}

				File tf = new File(
						Environment.getExternalStorageDirectory()
								+ File.separator + "WR_LPAIS" + File.separator + "txt" + File.separator + "threshold.txt");
				FileWriter fw = null;
				try {
					fw = new FileWriter(tf, false);
				} catch (IOException e) {
					e.printStackTrace();
				}

				PrintWriter pw = new PrintWriter(fw);
				pw.print(which);
				pw.flush();
				try {
					fw.flush();
					pw.close();
					fw.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		}).show();
	}

	//点击日志管理按钮执行的操作
	public void logManage(View v){

	}


	//删除手机本地的所有图片
	public  void  deleteAllimages()
	{
		File txtfile = new File(
				Environment.getExternalStorageDirectory()
						+ File.separator + "WR_LPAIS"+ File.separator + "txt");

		if (txtfile.exists()) {
			File[] files = txtfile.listFiles();
			if(files.length>0)
			{
				FileOutputStream fos = null;
				int temp = files.length;
				while (temp > 0) {
					temp--;
					File dfile = new File(files[temp].toString());
					if (dfile.isFile()) {
						boolean flag = dfile.delete();
					}
				}
			}
		}
//-----------------------------删除Image里面和ShowImage里面的全部图片-----------------------------------------------------
		File file = new File(
			Environment.getExternalStorageDirectory()
					+ File.separator + "WR_LPAIS"+ File.separator + "Image");

		File file1 = new File(
				Environment.getExternalStorageDirectory()
						+ File.separator + "WR_LPAIS"+ File.separator + "ShowImage");

		if (file.exists()) {
			File[] files = file.listFiles();
			if(files.length>0)
			{
				FileOutputStream fos = null;
				int temp = files.length;
				while (temp > 0) {
					temp--;
					File dfile = new File(files[temp].toString());
					if (dfile.isFile()) {
						boolean flag = dfile.delete();
					}
				}
			}
		}
		if (file1.exists()) {
			File[] files = file1.listFiles();
			if(files.length>0)
			{
				FileOutputStream fos = null;
				int temp = files.length;
				while (temp > 0) {
					temp--;
					File dfile = new File(files[temp].toString());
					if (dfile.isFile()) {
						boolean flag = dfile.delete();
					}
				}
			}
		}
		Toast.makeText(this, "删除成功！", Toast.LENGTH_LONG).show();
	}

	private void inputTitleDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(("是否确认删除全部数据？")).setIcon(
				android.R.drawable.ic_dialog_info).setNegativeButton(
				"取消", null);
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						deleteAllimages();
					}
				});
		builder.show();
	}

	public void botmSet(View v){
		Intent intent = new Intent(this, set.class);
		startActivity(intent);
	}
	public void botmContrast(View v){
		Intent intent = new Intent(this, takePhoto.class);
		startActivity(intent);
	}
	public void botmBlacklist(View v){
		Intent intent = new Intent(this, blackList.class);
		startActivity(intent);
	}
	public void botmQueryLoc(View v){
		Intent intent = new Intent(this, queryLocInfor.class);
		startActivity(intent);
	}
	public void botmQueryDB(View v){
		Intent intent = new Intent(this, queryDBInfor.class);
		startActivity(intent);
	}
	public void onBackPressed(){
		Intent intent = new Intent(set.this, takePhoto.class);
		startActivity(intent);
	}
}
