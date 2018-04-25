package cn.hxc.imgrecognition;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Map;

import cn.hxc.imgrecognitionSRI_OCR.R;

/**
 * Created by 刘欢 on 2018/3/12.
 */

public class functionMain extends Activity{
    //自定义退出应用Action,实际应用中应该放到整个应用的Constant类中.
    private static final String EXIT_APP_ACTION = "com.micen.exit_app";

    private ImageButton button3;
    private ListView choose_image;
    private EditText tv_savethreshold;
    private ImageView iv_photo;
    private Matrix matrix = new Matrix();
    private int[] newpixels;
    private int[] pixels;
    private int width;
    private int height;
    // 触摸方框
    private float old_x;
    private float old_y;
    private float new_x;
    private float new_y;
    private boolean isInRect;// 判断手指按下时相对于矩形框的位置
    private boolean isInTop;
    private boolean isInBottom;
    private int newbottom;
    private int newtop;
    private Canvas canvas;
    private Paint paint;
    private Rect rect;
    private Bitmap paintbitMap;
    private int offset;
    private PreferencesService service;
    private int margain;
    public EditText Edit_username;
    public EditText Edit_password;

    private GuideUtil guideUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.function_main);//软件activity的布局
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.image_set);//titlebar为自己标题栏的布局


        tv_savethreshold = (EditText) findViewById(R.id.content);
        iv_photo = (ImageView) findViewById(R.id.imgView1);
        // Toast.makeText(context, text, duration)
        //button3 = (ImageButton) findViewById(R.id.button3);
        choose_image = (ListView) findViewById(R.id.lv);
        // button3.setOnTouchListener(new btnOnTouchListener());
        service = new PreferencesService(this);
        Map<String, String> params = service.getPreferences("margain");
        SharedPreferences preferences = getSharedPreferences("set", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("zoom", 8);
        editor.commit();

        SharedPreferences setting = getSharedPreferences("20180309", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {//第一次
            setting.edit().putBoolean("FIRST", false).commit();
            File file = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "WR_LPAIS");
            if (file.exists()) {
                deleteFile(file);
            }
        } else {
            //Toast.makeText(MainActivity.this, "不是第一次", Toast.LENGTH_LONG).show();
        }

        if (!preferences.contains("countSum")) {
            editor.putInt("countSum", 2000);
            editor.putInt("count", 1);
            editor.commit();
        }
    }


    public void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //Constants.Logdada("文件不存在！"+"\n");
        }
    }
    private void onTouchMove(MotionEvent event) {
        new_x = event.getX();
        new_y = event.getY();
        // imageProcess.noequl("move", (int) new_y);
        offset = (int) (new_y - old_y);
        Bitmap newbitMap = Bitmap.createBitmap(paintbitMap);
        canvas.drawBitmap(newbitMap, matrix, paint);
        rect = canvas.getClipBounds();

        if (isInRect == true) {
            // 判断边界
            if (-offset >= newtop) {
                offset = (-newtop + 2);
            }
            if (offset > height - newbottom) {
                offset = height - newbottom - 1;
            }
            rect.bottom = (newbottom + offset);
            rect.top = (newtop + offset);

        } else if (isInTop == true) {
            if (-offset >= newtop) {
                offset = (-newtop + 2);
            }
            // 判断边界
            if (offset > newbottom - newtop - 30) {
                offset = newbottom - newtop - 30;
            }
            rect.bottom = (int) (newbottom);
            rect.top = (int) (newtop + offset);

        } else if (isInBottom == true) {
            // 判断边界
            if (-offset >= newbottom - newtop - 30) {
                offset = -(newbottom - newtop) + 30;
            }

            if (offset > height - newbottom) {
                offset = height - newbottom - 1;
            }
            rect.bottom = (int) (newbottom + offset);
            rect.top = (int) (newtop);

        }
        canvas.drawRect(rect, paint);
        iv_photo.postInvalidate();

    }
    private final static int SCANNIN_GREQUEST_CODE = 1;

    //主页面点击拍照按钮执行的操作
    public void takephoto(View v) {
        SharedPreferences preferences = getSharedPreferences("set", MODE_PRIVATE);
        int count=preferences.getInt("count", 1);
        int sum=preferences.getInt("countSum", 0);
        if(count>sum){
            Toast.makeText(this, "使用次数已过,想继续使用，请购买", Toast.LENGTH_LONG).show();
            return ;
        }else{
            Intent intent = new Intent(this, takePhoto.class);
            startActivity(intent);

        }

        // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//
        // 调用android自带的照相机
        // startActivityForResult(intent, 1);
    }

    //主页面点击查询数据库按钮执行的操作
    public void SearchDatabase(View v){
        Intent intent = new Intent(this, queryDBInfor.class);
        startActivity(intent);
    }

    //主页点击查询本地按钮执行的操作
    public void SearchLocal(View v){
        Intent intent = new Intent(this, queryLocInfor.class);
        startActivity(intent);
    }

    public void setPrefer(View v) {
        Intent intent = new Intent(this, set.class);
        startActivity(intent);
    }

    //自定义一个广播接收器,用来接收应用程序退出广播.

    //重写手机返回键,点击返回退出程序
   public void onBackPressed(){
       int currentVersion = android.os.Build.VERSION.SDK_INT;
       if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
           Intent startMain = new Intent(Intent.ACTION_MAIN);
           startMain.addCategory(Intent.CATEGORY_HOME);
           startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(startMain);
           System.exit(0);
       } else {// android2.1
           ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
           am.restartPackage(getPackageName());
       }
   }
    /*public void onBackPressed(){
        Intent intent = new Intent(functionMain.this, MainActivity.class);
        startActivity(intent);
    }*/
}

